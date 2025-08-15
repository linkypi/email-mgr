package com.ruoyi.system.service.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailContactGroup;

/**
 * 联系人群组Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailContactGroupService 
{
    /**
     * 查询联系人群组
     * 
     * @param groupId 联系人群组主键
     * @return 联系人群组
     */
    public EmailContactGroup selectEmailContactGroupByGroupId(Long groupId);

    /**
     * 查询联系人群组列表
     * 
     * @param emailContactGroup 联系人群组
     * @return 联系人群组集合
     */
    public List<EmailContactGroup> selectEmailContactGroupList(EmailContactGroup emailContactGroup);

    /**
     * 新增联系人群组
     * 
     * @param emailContactGroup 联系人群组
     * @return 结果
     */
    public int insertEmailContactGroup(EmailContactGroup emailContactGroup);

    /**
     * 修改联系人群组
     * 
     * @param emailContactGroup 联系人群组
     * @return 结果
     */
    public int updateEmailContactGroup(EmailContactGroup emailContactGroup);

    /**
     * 批量删除联系人群组
     * 
     * @param groupIds 需要删除的联系人群组主键集合
     * @return 结果
     */
    public int deleteEmailContactGroupByGroupIds(Long[] groupIds);

    /**
     * 删除联系人群组信息
     * 
     * @param groupId 联系人群组主键
     * @return 结果
     */
    public int deleteEmailContactGroupByGroupId(Long groupId);

    /**
     * 查询所有可用的群组
     * 
     * @return 群组列表
     */
    public List<EmailContactGroup> selectAllEnabledGroups();
}
