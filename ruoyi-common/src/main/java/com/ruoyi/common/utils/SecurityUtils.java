package com.ruoyi.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.PatternMatchUtils;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.spring.SpringUtils;

/**
 * 安全服务工具类
 * 
 * @author ruoyi
 */
public class SecurityUtils
{
    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);
    /**
     * 用户ID
     **/
    public static Long getUserId()
    {
        try
        {
            return getLoginUser().getUserId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId()
    {
        try
        {
            return getLoginUser().getDeptId();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取部门ID异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户账户
     **/
    public static String getUsername()
    {
        try
        {
            return getLoginUser().getUsername();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户账户异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser()
    {
        try
        {
            return (LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e)
        {
            throw new ServiceException("获取用户信息异常", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取BCryptPasswordEncoder实例（优先使用Spring容器中的单例）
     *
     * @return BCryptPasswordEncoder实例
     */
    private static BCryptPasswordEncoder getPasswordEncoder()
    {
        try
        {
            // 尝试从Spring容器获取单例实例
            return SpringUtils.getBean(BCryptPasswordEncoder.class);
        }
        catch (Exception e)
        {
            // 如果获取失败，创建新实例（向后兼容）
            return new BCryptPasswordEncoder();
        }
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password)
    {
        if (password == null || password.trim().isEmpty())
        {
            throw new IllegalArgumentException("密码不能为空");
        }
        // 对密码进行trim处理，确保前后空格不影响加密
        String trimmedPassword = password.trim();
        BCryptPasswordEncoder passwordEncoder = getPasswordEncoder();
        return passwordEncoder.encode(trimmedPassword);
    }

    /**
     * 生成密码掩码（用于安全日志记录）
     * 
     * @param password 原始密码
     * @return 掩码后的密码（只显示首尾字符）
     */
    private static String maskPassword(String password)
    {
        if (password == null || password.isEmpty())
        {
            return "***";
        }
        if (password.length() <= 2)
        {
            return "***";
        }
        if (password.length() <= 4)
        {
            return password.substring(0, 1) + "***";
        }
        return password.substring(0, 1) + "***" + password.substring(password.length() - 1);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 真实密码
     * @param encodedPassword 加密后字符（BCrypt哈希值）
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword)
    {
        if (rawPassword == null || rawPassword.trim().isEmpty())
        {
            log.debug("密码验证失败：输入密码为空");
            return false;
        }
        if (encodedPassword == null || encodedPassword.trim().isEmpty())
        {
            log.debug("密码验证失败：数据库密码哈希为空");
            return false;
        }
        
        // 检查BCrypt格式
        if (!encodedPassword.startsWith("$2a$") && !encodedPassword.startsWith("$2b$") && !encodedPassword.startsWith("$2y$"))
        {
            log.error("密码验证失败：数据库密码哈希格式不正确，不是BCrypt格式。哈希值前缀: {}", 
                    encodedPassword.length() > 10 ? encodedPassword.substring(0, 10) : encodedPassword);
            return false;
        }
        
        try
        {
            BCryptPasswordEncoder passwordEncoder = getPasswordEncoder();
            // 先尝试用原始密码验证（兼容已存储的带空格的密码哈希）
            boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
            
            // 如果原始密码验证失败，尝试用trim后的密码验证（兼容新存储的密码哈希）
            if (!matches) {
                String trimmedPassword = rawPassword.trim();
                // 只有当trim后的密码与原始密码不同时，才进行二次验证
                if (!trimmedPassword.equals(rawPassword)) {
                    matches = passwordEncoder.matches(trimmedPassword, encodedPassword);
                }
            }
            
            if (!matches)
            {
                // 密码验证失败时，记录详细的调试信息（不输出完整密码）
                String passwordMask = maskPassword(rawPassword);
                String encodedPrefix = encodedPassword.length() > 30 
                    ? encodedPassword.substring(0, 30) + "..." 
                    : encodedPassword;
                
                log.error("密码验证失败 - 输入密码掩码: {}, 密码长度: {}, 字符编码: {}, 数据库密码哈希: {}, 哈希长度: {}, 哈希前缀: {}", 
                        passwordMask,
                        rawPassword.length(),
                        java.nio.charset.StandardCharsets.UTF_8.name(),
                        encodedPrefix,
                        encodedPassword.length(),
                        encodedPassword.substring(0, Math.min(10, encodedPassword.length())));
                
                // 记录密码的字节信息（用于排查编码问题）
                byte[] passwordBytes = rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                log.debug("密码字节详情 - 长度: {}, 前3字节: {}, 后3字节: {}", 
                        passwordBytes.length,
                        passwordBytes.length > 0 ? String.format("%02X%02X%02X", 
                                passwordBytes[0], 
                                passwordBytes.length > 1 ? passwordBytes[1] : 0,
                                passwordBytes.length > 2 ? passwordBytes[2] : 0) : "N/A",
                        passwordBytes.length > 3 ? String.format("%02X%02X%02X", 
                                passwordBytes[passwordBytes.length - 3], 
                                passwordBytes[passwordBytes.length - 2],
                                passwordBytes[passwordBytes.length - 1]) : "N/A");
            }
            
            return matches;
        }
        catch (Exception e)
        {
            // 密码验证过程中发生异常
            String passwordMask = maskPassword(rawPassword);
            String encodedPrefix = encodedPassword.length() > 30 
                ? encodedPassword.substring(0, 30) + "..." 
                : encodedPassword;
            
            log.error("密码验证过程中发生异常 - 输入密码掩码: {}, 密码长度: {}, 数据库密码哈希: {}, 异常类型: {}, 异常信息: {}", 
                    passwordMask,
                    rawPassword != null ? rawPassword.length() : 0,
                    encodedPrefix,
                    e.getClass().getSimpleName(),
                    e.getMessage(), 
                    e);
            
            // 记录异常但不抛出，返回false表示验证失败
            return false;
        }
    }

    /**
     * 是否为管理员
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    /**
     * 验证用户是否具备某权限
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermi(String permission)
    {
        return hasPermi(getLoginUser().getPermissions(), permission);
    }

    /**
     * 判断是否包含权限
     * 
     * @param authorities 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermi(Collection<String> authorities, String permission)
    {
        return authorities.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.ALL_PERMISSION.equals(x) || PatternMatchUtils.simpleMatch(x, permission));
    }

    /**
     * 验证用户是否拥有某个角色
     * 
     * @param role 角色标识
     * @return 用户是否具备某角色
     */
    public static boolean hasRole(String role)
    {
        List<SysRole> roleList = getLoginUser().getUser().getRoles();
        Collection<String> roles = roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toSet());
        return hasRole(roles, role);
    }

    /**
     * 判断是否包含角色
     * 
     * @param roles 角色列表
     * @param role 角色
     * @return 用户是否具备某角色权限
     */
    public static boolean hasRole(Collection<String> roles, String role)
    {
        return roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.SUPER_ADMIN.equals(x) || PatternMatchUtils.simpleMatch(x, role));
    }

}
