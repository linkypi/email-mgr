package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailContactGroup;

/**
 * 邮件联系人群组Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailContactGroupMapper 
{
    /**
     * 查询邮件联系人群组
     * 
     * @param groupId 邮件联系人群组主键
     * @return 邮件联系人群组
     */
    public EmailContactGroup selectEmailContactGroupByGroupId(Long groupId);

    /**
     * 查询邮件联系人群组列表
     * 
     * @param emailContactGroup 邮件联系人群组
     * @return 邮件联系人群组集合
     */
    public List<EmailContactGroup> selectEmailContactGroupList(EmailContactGroup emailContactGroup);

    /**
     * 新增邮件联系人群组
     * 
     * @param emailContactGroup 邮件联系人群组
     * @return 结果
     */
    public int insertEmailContactGroup(EmailContactGroup emailContactGroup);

    /**
     * 修改邮件联系人群组
     * 
     * @param emailContactGroup 邮件联系人群组
     * @return 结果
     */
    public int updateEmailContactGroup(EmailContactGroup emailContactGroup);

    /**
     * 删除邮件联系人群组
     * 
     * @param groupId 邮件联系人群组主键
     * @return 结果
     */
    public int deleteEmailContactGroupByGroupId(Long groupId);

    /**
     * 批量删除邮件联系人群组
     * 
     * @param groupIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailContactGroupByGroupIds(Long[] groupIds);

    /**
     * 更新群组联系人数量
     * 
     * @param groupId 群组ID
     * @return 结果
     */
    public int updateGroupContactCount(Long groupId);
}
