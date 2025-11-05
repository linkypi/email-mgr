package com.ruoyi.framework.web.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.exception.user.UserPasswordRetryLimitExceedException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.framework.security.context.AuthenticationContextHolder;

/**
 * 登录密码方法
 * 
 * @author ruoyi
 */
@Component
public class SysPasswordService
{
    @Autowired
    private RedisCache redisCache;

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount;

    @Value(value = "${user.password.lockTime}")
    private int lockTime;
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    /**
     * 登录账户密码错误次数缓存键名
     * 
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username)
    {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(SysUser user)
    {
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        if (usernamePasswordAuthenticationToken == null)
        {
            log.error("AuthenticationContextHolder中未找到认证信息");
            throw new UserPasswordNotMatchException();
        }
        
        String username = usernamePasswordAuthenticationToken.getName();
        Object credentials = usernamePasswordAuthenticationToken.getCredentials();
        
        // 检查密码是否为空或已被擦除
        if (credentials == null)
        {
            log.error("认证信息中的密码为空，可能已被Spring Security擦除，登录用户：{}", username);
            throw new UserPasswordNotMatchException();
        }
        
        String password = credentials.toString();
        
        // 检查密码是否为空字符串
        if (password == null || password.trim().isEmpty())
        {
            log.error("密码为空，登录用户：{}", username);
            throw new UserPasswordNotMatchException();
        }

        Integer retryCount = redisCache.getCacheObject(getCacheKey(username));

        if (retryCount == null)
        {
            retryCount = 0;
        }

        if (retryCount >= Integer.valueOf(maxRetryCount).intValue())
        {
            throw new UserPasswordRetryLimitExceedException(maxRetryCount, lockTime);
        }

        // 验证密码
        boolean passwordMatches = matches(user, password);
        
        if (!passwordMatches)
        {
            // 记录日志时只显示密码长度和部分字符，不记录完整密码
            String passwordMask = password.length() > 2 
                ? password.substring(0, 1) + "***" + password.substring(password.length() - 1)
                : "***";
            log.error("用户密码不匹配，登录用户：{}, 密码长度: {}, 密码掩码: {}, 数据库用户: {}, 数据库用户Id: {}, 数据库密码哈希: {}",
                    username, password.length(), passwordMask, user.getUserName(), user.getUserId(), 
                    user.getPassword() != null ? user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "..." : "null");
            
            // 添加详细的调试信息
            log.debug("密码验证详情 - 原始密码: [{}], 密码字节长度: {}, 数据库密码哈希: [{}]", 
                    password, password.getBytes().length, 
                    user.getPassword() != null ? user.getPassword() : "null");
            
            retryCount = retryCount + 1;
            redisCache.setCacheObject(getCacheKey(username), retryCount, lockTime, TimeUnit.MINUTES);
            throw new UserPasswordNotMatchException();
        }
        else
        {
            clearLoginRecordCache(username);
        }
    }

    public boolean matches(SysUser user, String rawPassword)
    {
        if (rawPassword == null || rawPassword.trim().isEmpty())
        {
            log.warn("待验证的密码为空，用户ID: {}", user.getUserId());
            return false;
        }
        
        String encodedPassword = user.getPassword();
        if (encodedPassword == null || encodedPassword.trim().isEmpty())
        {
            log.warn("数据库中的密码哈希为空，用户ID: {}", user.getUserId());
            return false;
        }
        
        // 检查是否为BCrypt格式（以$2a$、$2b$或$2y$开头）
        if (!encodedPassword.startsWith("$2a$") && !encodedPassword.startsWith("$2b$") && !encodedPassword.startsWith("$2y$"))
        {
            log.error("数据库中的密码哈希格式不正确，不是BCrypt格式，用户ID: {}", user.getUserId());
            return false;
        }
        
        try
        {
            boolean result = SecurityUtils.matchesPassword(rawPassword, encodedPassword);
            log.debug("密码验证结果: {}, 用户ID: {}, 密码长度: {}", result, user.getUserId(), rawPassword.length());
            return result;
        }
        catch (Exception e)
        {
            log.error("密码验证过程中发生异常，用户ID: {}, 错误信息: {}", user.getUserId(), e.getMessage(), e);
            return false;
        }
    }

    public void clearLoginRecordCache(String loginName)
    {
        if (redisCache.hasKey(getCacheKey(loginName)))
        {
            redisCache.deleteObject(getCacheKey(loginName));
        }
    }
}
