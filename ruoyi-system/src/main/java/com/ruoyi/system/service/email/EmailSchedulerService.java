package com.ruoyi.system.service.email;

import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.email.EmailSendTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.*;

/**
 * 邮件定时发送调度器
 * 基于时间轮算法实现定时发送功能，使用Redis ZSet持久化定时任务
 */
@Service
@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class EmailSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(EmailSchedulerService.class);
    
    private static final String SCHEDULED_EMAIL_TASKS_KEY = "email:scheduled:tasks";
    private static final String PROCESSING_EMAIL_TASKS_KEY = "email:processing:tasks";
    
    private RedisTemplate<Object, Object> redisTemplate;
    
    @Autowired
    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    @Autowired
    private EmailSendService emailSendService;
    
    @Autowired
    private EmailServiceMonitorService emailServiceMonitorService;
    
    // 时间轮相关
    private ScheduledExecutorService scheduler;
    private ExecutorService taskExecutor;
    private volatile boolean running = false;
    
    // 检查间隔（秒）
    private static final int CHECK_INTERVAL = 10;
    
    @PostConstruct
    public void init() {
        // 减少调度器初始化的日志输出
        if (logger.isDebugEnabled()) {
            logger.debug("邮件定时调度器初始化开始");
        }
        try {
            initThreadPools();
            startScheduler();
            logger.info("邮件定时调度器初始化完成");
        } catch (Exception e) {
            logger.error("邮件定时调度器初始化失败", e);
        }
    }
    
    @PreDestroy
    public void destroy() {
        logger.info("邮件定时调度器销毁");
        stopScheduler();
        shutdownThreadPools();
    }
    
    private void initThreadPools() {
        scheduler = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r, "Email-Scheduler-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
        
        taskExecutor = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "Email-Task-Executor-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
    }
    
    private void shutdownThreadPools() {
        if (scheduler != null && !scheduler.isShutdown()) {
            try {
                scheduler.shutdown();
                if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (taskExecutor != null && !taskExecutor.isShutdown()) {
            try {
                taskExecutor.shutdown();
                if (!taskExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    taskExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                taskExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    private void startScheduler() {
        if (running) {
            logger.warn("邮件定时调度器已在运行");
            return;
        }
        
        running = true;
        
        // 启动定时任务检查
        scheduler.scheduleAtFixedRate(this::checkAndExecuteTasks, 
            CHECK_INTERVAL, CHECK_INTERVAL, TimeUnit.SECONDS);
            
        // 减少调度器启动的日志输出
        if (logger.isDebugEnabled()) {
            logger.debug("邮件定时调度器已启动，检查间隔: {} 秒", CHECK_INTERVAL);
        }
    }
    
    private void stopScheduler() {
        running = false;
        logger.info("邮件定时调度器已停止");
    }
    
    /**
     * 添加定时发送任务到Redis ZSet
     */
    public void scheduleEmailTask(EmailSendTask task) {
        try {
            if (task == null || task.getTaskId() == null) {
                logger.error("任务对象或任务ID不能为空");
                return;
            }
            
            Date scheduleTime = task.getStartTime();
            if (scheduleTime == null) {
                logger.error("定时发送时间不能为空");
                return;
            }
            
            // 检查是否是过去的时间
            if (scheduleTime.before(new Date())) {
                logger.warn("定时发送时间已过期，立即执行任务: {}", task.getTaskId());
                executeTaskImmediately(task);
                return;
            }
            
            // 使用任务ID作为member，发送时间戳作为score存储到Redis ZSet中
            long timestamp = scheduleTime.getTime();
            redisTemplate.opsForZSet().add(SCHEDULED_EMAIL_TASKS_KEY, task.getTaskId().toString(), timestamp);
            
            logger.info("已添加定时任务到调度器: taskId={}, scheduleTime={}", 
                task.getTaskId(), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, scheduleTime));
                
        } catch (Exception e) {
            logger.error("添加定时任务失败: taskId={}", task != null ? task.getTaskId() : "null", e);
        }
    }
    
    /**
     * 取消定时发送任务
     */
    public void cancelScheduledTask(Long taskId) {
        try {
            if (taskId == null) {
                logger.error("任务ID不能为空");
                return;
            }
            
            // 从调度队列中移除
            Long removed = redisTemplate.opsForZSet().remove(SCHEDULED_EMAIL_TASKS_KEY, taskId.toString());
            // 从处理队列中移除（如果存在）
            Long removedFromProcessing = redisTemplate.opsForZSet().remove(PROCESSING_EMAIL_TASKS_KEY, taskId.toString());
            
            logger.info("已取消定时任务: taskId={}, removedFromScheduled={}, removedFromProcessing={}", 
                taskId, removed, removedFromProcessing);
                
        } catch (Exception e) {
            logger.error("取消定时任务失败: taskId={}", taskId, e);
        }
    }
    
    /**
     * 检查并执行到期的任务
     */
    private void checkAndExecuteTasks() {
        if (!running) {
            return;
        }
        
        try {
            long currentTime = System.currentTimeMillis();
            
            // 从Redis ZSet中获取到期的任务（score <= currentTime）
            Set<ZSetOperations.TypedTuple<Object>> expiredTasks = redisTemplate.opsForZSet()
                .rangeByScoreWithScores(SCHEDULED_EMAIL_TASKS_KEY, 0, currentTime);
            
            if (expiredTasks != null && !expiredTasks.isEmpty()) {
                logger.info("发现 {} 个到期任务", expiredTasks.size());
                
                for (ZSetOperations.TypedTuple<Object> task : expiredTasks) {
                    String taskIdStr = (String) task.getValue();
                    Double score = task.getScore();
                    
                    if (StringUtils.isEmpty(taskIdStr) || score == null) {
                        continue;
                    }
                    
                    Long taskId;
                    try {
                        taskId = Long.parseLong(taskIdStr);
                    } catch (NumberFormatException e) {
                        logger.error("无效的任务ID格式: {}", taskIdStr);
                        // 移除无效任务
                        redisTemplate.opsForZSet().remove(SCHEDULED_EMAIL_TASKS_KEY, taskIdStr);
                        continue;
                    }
                    
                    // 将任务移动到处理队列（防止重复执行）
                    if (moveTaskToProcessing(taskId, score)) {
                        // 异步执行任务
                        taskExecutor.submit(() -> executeScheduledTask(taskId));
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("检查并执行定时任务时发生异常", e);
        }
    }
    
    /**
     * 将任务从调度队列移动到处理队列
     */
    private boolean moveTaskToProcessing(Long taskId, Double score) {
        try {
            String taskIdStr = taskId.toString();
            
            // 使用Redis事务确保原子性
            return redisTemplate.execute((org.springframework.data.redis.core.RedisCallback<Boolean>) connection -> {
                try {
                    // 开始事务
                    connection.multi();
                    
                    // 从调度队列中移除
                    connection.zRem(SCHEDULED_EMAIL_TASKS_KEY.getBytes(), taskIdStr.getBytes());
                    // 添加到处理队列
                    connection.zAdd(PROCESSING_EMAIL_TASKS_KEY.getBytes(), score, taskIdStr.getBytes());
                    
                    // 执行事务
                    connection.exec();
                    return true;
                } catch (Exception e) {
                    logger.error("移动任务到处理队列失败: taskId={}", taskId, e);
                    return false;
                }
            });
            
        } catch (Exception e) {
            logger.error("移动任务到处理队列时发生异常: taskId={}", taskId, e);
            return false;
        }
    }
    
    /**
     * 执行定时任务
     */
    private void executeScheduledTask(Long taskId) {
        try {
            logger.info("开始执行定时任务: {}", taskId);
            
            // 使用长连接发送邮件
            executeTaskWithLongConnection(taskId);
            
            // 任务执行完成后从处理队列中移除
            removeFromProcessingQueue(taskId);
            
            logger.info("定时任务执行完成: {}", taskId);
            
        } catch (Exception e) {
            logger.error("执行定时任务失败: taskId={}", taskId, e);
            
            // 执行失败时也要从处理队列中移除，避免任务堆积
            removeFromProcessingQueue(taskId);
            
            // 可以考虑添加重试逻辑或错误通知
            handleTaskExecutionError(taskId, e);
        }
    }
    
    /**
     * 立即执行任务
     */
    private void executeTaskImmediately(EmailSendTask task) {
        taskExecutor.submit(() -> {
            try {
                logger.info("立即执行过期任务: {}", task.getTaskId());
                executeTaskWithLongConnection(task.getTaskId());
            } catch (Exception e) {
                logger.error("立即执行过期任务失败: taskId={}", task.getTaskId(), e);
                handleTaskExecutionError(task.getTaskId(), e);
            }
        });
    }
    
    /**
     * 使用长连接执行任务
     */
    private void executeTaskWithLongConnection(Long taskId) {
        try {
            // 调用EmailSendService的长连接发送方法
            emailSendService.startSendTaskWithLongConnection(taskId);
            
        } catch (Exception e) {
            logger.error("使用长连接执行任务失败: taskId={}", taskId, e);
            throw e;
        }
    }
    
    /**
     * 从处理队列中移除任务
     */
    private void removeFromProcessingQueue(Long taskId) {
        try {
            redisTemplate.opsForZSet().remove(PROCESSING_EMAIL_TASKS_KEY, taskId.toString());
            logger.debug("已从处理队列移除任务: {}", taskId);
        } catch (Exception e) {
            logger.error("从处理队列移除任务失败: taskId={}", taskId, e);
        }
    }
    
    /**
     * 处理任务执行错误
     */
    private void handleTaskExecutionError(Long taskId, Exception error) {
        try {
            logger.error("任务执行失败，记录错误信息: taskId={}, error={}", taskId, error.getMessage());
            
            // 可以在这里添加错误通知逻辑
            // 例如：发送管理员通知、记录错误日志等
            
        } catch (Exception e) {
            logger.error("处理任务执行错误时发生异常: taskId={}", taskId, e);
        }
    }
    
    /**
     * 获取调度器状态
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * 获取待执行任务数量
     */
    public long getPendingTaskCount() {
        try {
            Long count = redisTemplate.opsForZSet().count(SCHEDULED_EMAIL_TASKS_KEY, 0, System.currentTimeMillis());
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("获取待执行任务数量失败", e);
            return 0;
        }
    }
    
    /**
     * 获取正在处理的任务数量
     */
    public long getProcessingTaskCount() {
        try {
            Long count = redisTemplate.opsForZSet().zCard(PROCESSING_EMAIL_TASKS_KEY);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("获取正在处理任务数量失败", e);
            return 0;
        }
    }
    
    /**
     * 获取总的调度任务数量
     */
    public long getTotalScheduledTaskCount() {
        try {
            Long count = redisTemplate.opsForZSet().zCard(SCHEDULED_EMAIL_TASKS_KEY);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("获取总调度任务数量失败", e);
            return 0;
        }
    }
    
    /**
     * 清理过期的处理任务（防止处理队列堆积）
     */
    public void cleanupExpiredProcessingTasks() {
        try {
            // 清理超过1小时的处理任务（认为已经执行失败或超时）
            long expiredTime = System.currentTimeMillis() - 3600000; // 1小时前
            Long removedCount = redisTemplate.opsForZSet().removeRangeByScore(PROCESSING_EMAIL_TASKS_KEY, 0, expiredTime);
            
            if (removedCount != null && removedCount > 0) {
                logger.info("清理了 {} 个过期的处理任务", removedCount);
            }
            
        } catch (Exception e) {
            logger.error("清理过期处理任务失败", e);
        }
    }
}