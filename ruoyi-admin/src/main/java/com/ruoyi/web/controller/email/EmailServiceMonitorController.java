package com.ruoyi.web.controller.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailServiceMonitor;
import com.ruoyi.system.domain.email.EmailServiceMonitorLog;
import com.ruoyi.system.service.email.EmailServiceMonitorService;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;

/**
 * 邮件服务监控Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/monitor")
public class EmailServiceMonitorController extends BaseController
{
    @Autowired
    private EmailServiceMonitorService emailServiceMonitorService;
    
    @Autowired
    private EmailListener emailListener;

    @Autowired
    private IEmailAccountService emailAccountService;

    /**
     * 查询邮件服务监控列表
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailServiceMonitor emailServiceMonitor)
    {
        startPage();
        List<EmailServiceMonitor> list = emailServiceMonitorService.getMonitorList(emailServiceMonitor);
        return getDataTable(list);
    }

    /**
     * 导出邮件服务监控列表
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:export')")
    @Log(title = "邮件服务监控", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(EmailServiceMonitor emailServiceMonitor)
    {
        List<EmailServiceMonitor> list = emailServiceMonitorService.getMonitorList(emailServiceMonitor);
        ExcelUtil<EmailServiceMonitor> util = new ExcelUtil<EmailServiceMonitor>(EmailServiceMonitor.class);
        return util.exportExcel(list, "邮件服务监控数据");
    }

    /**
     * 获取邮件服务监控详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(emailServiceMonitorService.getMonitorList(new EmailServiceMonitor()).stream()
                .filter(monitor -> monitor.getId().equals(id))
                .findFirst()
                .orElse(null));
    }

    /**
     * 启动全局监控
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:start')")
    @Log(title = "启动邮件服务全局监控", businessType = BusinessType.UPDATE)
    @PostMapping("/start")
    public AjaxResult startGlobalMonitor()
    {
        emailServiceMonitorService.startGlobalMonitor();
        return success("全局监控启动成功");
    }

    /**
     * 停止全局监控
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:stop')")
    @Log(title = "停止邮件服务全局监控", businessType = BusinessType.UPDATE)
    @PostMapping("/stop")
    public AjaxResult stopGlobalMonitor()
    {
        emailServiceMonitorService.stopGlobalMonitor();
        return success("全局监控停止成功");
    }

    /**
     * 启动指定账号监控
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:start')")
    @Log(title = "启动账号监控", businessType = BusinessType.UPDATE)
    @PostMapping("/start/{accountId}")
    public AjaxResult startAccountMonitor(@PathVariable("accountId") Long accountId)
    {
        emailServiceMonitorService.startAccountMonitor(accountId);
        return success("账号监控启动成功");
    }

    /**
     * 停止指定账号监控
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:stop')")
    @Log(title = "停止账号监控", businessType = BusinessType.UPDATE)
    @PostMapping("/stop/{accountId}")
    public AjaxResult stopAccountMonitor(@PathVariable("accountId") Long accountId)
    {
        emailServiceMonitorService.stopAccountMonitor(accountId);
        return success("账号监控停止成功");
    }

    /**
     * 重启指定账号监控
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:restart')")
    @Log(title = "重启账号监控", businessType = BusinessType.UPDATE)
    @PostMapping("/restart/{accountId}")
    public AjaxResult restartAccountMonitor(@PathVariable("accountId") Long accountId)
    {
        emailServiceMonitorService.restartAccountMonitor(accountId);
        return success("账号监控重启成功");
    }

    /**
     * 测试 IMAP 服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:test')")
    @Log(title = "测试IMAP服务", businessType = BusinessType.UPDATE)
    @PostMapping("/test/imap/{accountId}")
    public AjaxResult testImapService(@PathVariable("accountId") Long accountId)
    {
        emailServiceMonitorService.testImapService(accountId);
        return success("IMAP服务测试完成");
    }

    /**
     * 测试 SMTP 服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:test')")
    @Log(title = "测试SMTP服务", businessType = BusinessType.UPDATE)
    @PostMapping("/test/smtp/{accountId}")
    public AjaxResult testSmtpService(@PathVariable("accountId") Long accountId)
    {
        emailServiceMonitorService.testSmtpService(accountId);
        return success("SMTP服务测试完成");
    }

    /**
     * 获取监控状态
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:query')")
    @GetMapping("/status")
    public AjaxResult getMonitorStatus()
    {
        return success(emailServiceMonitorService.isGlobalMonitorRunning());
    }

    /**
     * 获取账号监控状态
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:query')")
    @GetMapping("/status/{accountId}")
    public AjaxResult getAccountMonitorStatus(@PathVariable("accountId") Long accountId)
    {
        return success(emailServiceMonitorService.isAccountMonitoring(accountId));
    }

    /**
     * 查询监控记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:log')")
    @GetMapping("/log/list")
    public TableDataInfo logList(EmailServiceMonitorLog emailServiceMonitorLog)
    {
        startPage();
        List<EmailServiceMonitorLog> list = emailServiceMonitorService.getMonitorLogList(emailServiceMonitorLog);
        return getDataTable(list);
    }

    /**
     * 导出监控记录列表
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:log:export')")
    @Log(title = "邮件服务监控记录", businessType = BusinessType.EXPORT)
    @GetMapping("/log/export")
    public AjaxResult exportLog(EmailServiceMonitorLog emailServiceMonitorLog)
    {
        List<EmailServiceMonitorLog> list = emailServiceMonitorService.getMonitorLogList(emailServiceMonitorLog);
        ExcelUtil<EmailServiceMonitorLog> util = new ExcelUtil<EmailServiceMonitorLog>(EmailServiceMonitorLog.class);
        return util.exportExcel(list, "邮件服务监控记录数据");
    }

    /**
     * 获取账号监控记录
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:log')")
    @GetMapping("/log/{accountId}")
    public TableDataInfo getAccountMonitorLogs(@PathVariable("accountId") Long accountId)
    {
        startPage();
        List<EmailServiceMonitorLog> list = emailServiceMonitorService.getAccountMonitorLogs(accountId);
        return getDataTable(list);
    }

    /**
     * 获取监控统计
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:stats')")
    @GetMapping("/stats")
    public AjaxResult getMonitorStats()
    {
        Map<String, Object> stats = emailServiceMonitorService.getMonitorStats();
        return success(stats);
    }
    
    /**
     * 手动启动指定账号的指定服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:service:start')")
    @Log(title = "启动邮件服务", businessType = BusinessType.UPDATE)
    @PostMapping("/service/start/{accountId}")
    public AjaxResult startAccountService(@PathVariable("accountId") Long accountId, 
                                        @RequestParam("serviceType") String serviceType)
    {
        try {
            boolean success = emailListener.startAccountService(accountId, serviceType);
            if (success) {
                return success(String.format("%s 服务启动成功", serviceType.toUpperCase()));
            } else {
                return error(String.format("%s 服务启动失败", serviceType.toUpperCase()));
            }
        } catch (Exception e) {
            return error(String.format("%s 服务启动失败: %s", serviceType.toUpperCase(), e.getMessage()));
        }
    }
    
    /**
     * 手动停止指定账号的指定服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:service:stop')")
    @Log(title = "停止邮件服务", businessType = BusinessType.UPDATE)
    @PostMapping("/service/stop/{accountId}")
    public AjaxResult stopAccountService(@PathVariable("accountId") Long accountId, 
                                       @RequestParam("serviceType") String serviceType)
    {
        try {
            boolean success = emailListener.stopAccountService(accountId, serviceType);
            if (success) {
                return success(String.format("%s 服务停止成功", serviceType.toUpperCase()));
            } else {
                return error(String.format("%s 服务停止失败", serviceType.toUpperCase()));
            }
        } catch (Exception e) {
            return error(String.format("%s 服务停止失败: %s", serviceType.toUpperCase(), e.getMessage()));
        }
    }
    
    /**
     * 手动重启指定账号的指定服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:service:restart')")
    @Log(title = "重启邮件服务", businessType = BusinessType.UPDATE)
    @PostMapping("/service/restart/{accountId}")
    public AjaxResult restartAccountService(@PathVariable("accountId") Long accountId, 
                                          @RequestParam("serviceType") String serviceType)
    {
        try {
            boolean success = emailListener.restartAccountService(accountId, serviceType);
            if (success) {
                return success(String.format("%s 服务重启成功", serviceType.toUpperCase()));
            } else {
                return error(String.format("%s 服务重启失败", serviceType.toUpperCase()));
            }
        } catch (Exception e) {
            return error(String.format("%s 服务重启失败: %s", serviceType.toUpperCase(), e.getMessage()));
        }
    }
    
    /**
     * 获取指定账号的连接状态
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:status')")
    @GetMapping("/connection/status/{accountId}")
    public AjaxResult getAccountConnectionStatus(@PathVariable("accountId") Long accountId)
    {
        try {
            Map<String, Object> status = emailListener.getConnectionStatus(accountId);
            return success(status);
        } catch (Exception e) {
            return error("获取连接状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有账号的连接状态
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:status')")
    @GetMapping("/connection/status/all")
    public AjaxResult getAllConnectionStatus()
    {
        try {
            Map<Long, Map<String, Object>> allStatus = emailListener.getAllConnectionStatus();
            return success(allStatus);
        } catch (Exception e) {
            return error("获取连接状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 启动EmailListener服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:listener:start')")
    @Log(title = "启动邮件监听服务", businessType = BusinessType.UPDATE)
    @PostMapping("/listener/start")
    public AjaxResult startEmailListener()
    {
        try {
            // 启动EmailListener服务
            emailListener.startListener();
            
            // 同时启动EmailServiceMonitorService的全局监控
            emailServiceMonitorService.startGlobalMonitor();
            
            return success("邮件监听服务启动成功");
        } catch (Exception e) {
            return error("邮件监听服务启动失败: " + e.getMessage());
        }
    }
    
    /**
     * 停止EmailListener服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:listener:stop')")
    @Log(title = "停止邮件监听服务", businessType = BusinessType.UPDATE)
    @PostMapping("/listener/stop")
    public AjaxResult stopEmailListener()
    {
        try {
            // 停止EmailListener服务
            emailListener.stopListener();
            
            // 同时停止EmailServiceMonitorService的全局监控
            emailServiceMonitorService.stopGlobalMonitor();
            
            return success("邮件监听服务停止成功");
        } catch (Exception e) {
            return error("邮件监听服务停止失败: " + e.getMessage());
        }
    }
    

    
    /**
     * 重启EmailListener服务
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:listener:restart')")
    @Log(title = "重启邮件监听服务", businessType = BusinessType.UPDATE)
    @PostMapping("/listener/restart")
    public AjaxResult restartEmailListener()
    {
        try {
            // 重启EmailListener服务
            emailListener.restartListener();
            
            // 同时重启EmailServiceMonitorService的全局监控
            emailServiceMonitorService.stopGlobalMonitor();
            emailServiceMonitorService.startGlobalMonitor();
            
            return success("邮件监听服务重启成功");
        } catch (Exception e) {
            return error("邮件监听服务重启失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取EmailListener服务状态
     */
    @PreAuthorize("@ss.hasPermi('email:monitor:listener:status')")
    @GetMapping("/listener/status")
    public AjaxResult getEmailListenerStatus()
    {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 检查EmailListener状态
            boolean listenerRunning = emailListener.isRunning();
            int listenerConnections = emailListener.getActiveConnectionCount();
            
            // 检查EmailServiceMonitorService状态
            boolean monitorRunning = emailServiceMonitorService.isGlobalMonitorRunning();
            
            // 综合状态：只有当两个服务都在运行时，才认为监听服务在运行
            boolean overallRunning = listenerRunning && monitorRunning;
            
            status.put("running", overallRunning);
            status.put("activeConnections", listenerConnections);
            status.put("listenerRunning", listenerRunning);
            status.put("monitorRunning", monitorRunning);
            
            return success(status);
        } catch (Exception e) {
            return error("获取监听服务状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取线程池状态（调试用）
     */
    @GetMapping("/threadpool/status")
    public AjaxResult getThreadPoolStatus()
    {
        try {
            Map<String, Object> status = emailServiceMonitorService.getThreadPoolStatus();
            return success(status);
        } catch (Exception e) {
            return error("获取线程池状态失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取账号状态信息（用于调试）
     */
    @GetMapping("/account/status")
    public AjaxResult getAccountStatus()
    {
        try {
            Map<String, Object> status = emailServiceMonitorService.getAccountStatusInfo();
            return success(status);
        } catch (Exception e) {
            return error("获取账号状态失败: " + e.getMessage());
        }
    }
}
