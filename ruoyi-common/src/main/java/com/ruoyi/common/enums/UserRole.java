package com.ruoyi.common.enums;

/**
 * 用户角色枚举
 * 
 * @author ruoyi
 * @date 2025-01-15
 */
public enum UserRole
{
    /** 管理员 */
    ADMIN("admin", "管理员"),
    
    /** 普通账号 */
    REGULAR("regular", "普通账号"),
    
    /** 访客账号 */
    GUEST("guest", "访客账号");

    private final String code;
    private final String info;

    UserRole(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }

    /**
     * 根据代码获取角色
     */
    public static UserRole getByCode(String code)
    {
        for (UserRole role : values())
        {
            if (role.getCode().equals(code))
            {
                return role;
            }
        }
        return GUEST; // 默认返回访客角色
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin()
    {
        return this == ADMIN;
    }

    /**
     * 判断是否为普通账号
     */
    public boolean isRegular()
    {
        return this == REGULAR;
    }

    /**
     * 判断是否为访客
     */
    public boolean isGuest()
    {
        return this == GUEST;
    }

    /**
     * 判断是否可以查看所有数据
     */
    public boolean canViewAllData()
    {
        return this == ADMIN || this == REGULAR;
    }

    /**
     * 判断是否可以管理其他用户
     */
    public boolean canManageUsers()
    {
        return this == ADMIN;
    }
}
