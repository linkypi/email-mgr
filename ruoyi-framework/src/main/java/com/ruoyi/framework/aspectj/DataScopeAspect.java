package com.ruoyi.framework.aspectj;

import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserRole;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 数据权限过滤处理
 * 
 * @author ruoyi
 */
@Aspect
@Component
public class DataScopeAspect
{
    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 访客数据权限（只能看到自己的数据）
     */
    public static final String DATA_SCOPE_GUEST = "6";

    @Before("@annotation(com.ruoyi.common.annotation.DataScope)")
    public void doBefore(JoinPoint point) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point);
    }

    protected void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
        {
            BaseEntity baseEntity = (BaseEntity) params;
            baseEntity.getParams().put("dataScope", "");
        }
    }

    protected void handleDataScope(final JoinPoint joinPoint)
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (StringUtils.isNotNull(loginUser))
        {
            SysUser currentUser = loginUser.getUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
            {
                dataScopeFilter(joinPoint, currentUser);
            }
        }
    }

    /**
     * 数据范围过滤
     * 
     * @param joinPoint 切点
     * @param user 用户
     */
    public static void dataScopeFilter(JoinPoint joinPoint, SysUser user)
    {
        StringBuilder dataScope = new StringBuilder();

        // 根据用户角色确定数据权限
        UserRole userRole = UserRole.getByCode(getUserRoleCode(user));
        
        // 获取方法名，判断是哪个服务
        String methodName = joinPoint.getSignature().getName();
        String tableAlias = getTableAlias(methodName);
        
        if (userRole.isGuest())
        {
            // 访客账号：只能看到自己的数据
            dataScope.append(tableAlias).append(".create_by = '").append(user.getUserName()).append("'");
        }
        else if (userRole.isRegular())
        {
            // 普通账号：可以看到所有数据（不添加任何限制条件）
            // 不添加任何限制条件，允许查看所有联系人
        }
        else if (userRole.isAdmin())
        {
            // 管理员：可以看到所有数据
            // 不添加任何限制条件
        }
        else
        {
            // 默认情况：只能看到自己的数据
            dataScope.append(tableAlias).append(".create_by = '").append(user.getUserName()).append("'");
        }

        if (StringUtils.isNotBlank(dataScope.toString()))
        {
            Object params = joinPoint.getArgs()[0];
            if (StringUtils.isNotNull(params) && params instanceof BaseEntity)
            {
                BaseEntity baseEntity = (BaseEntity) params;
                baseEntity.getParams().put("dataScope", dataScope.toString());
            }
        }
    }

    /**
     * 根据方法名获取表别名
     */
    private static String getTableAlias(String methodName)
    {
        if (methodName.contains("Contact"))
        {
            return "c"; // email_contact 表别名
        }
        else if (methodName.contains("Track") || methodName.contains("Sent") || methodName.contains("Inbox"))
        {
            return "etr"; // email_track_record 表别名
        }
        else
        {
            return "c"; // 默认别名
        }
    }

    /**
     * 获取用户角色代码
     */
    private static String getUserRoleCode(SysUser user)
    {
        List<SysRole> roles = user.getRoles();
        if (roles != null && !roles.isEmpty())
        {
            // 获取第一个角色的role_key
            return roles.get(0).getRoleKey();
        }
        return "guest"; // 默认访客角色
    }
}
