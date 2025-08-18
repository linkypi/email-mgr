package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailContact;

/**
 * 邮件联系人Mapper接口
 * 
 * @author ruoyi
 * @date 2023-12-01
 */
public interface EmailContactMapper 
{
    /**
     * 查询邮件联系人
     * 
     * @param contactId 邮件联系人主键
     * @return 邮件联系人
     */
    public EmailContact selectEmailContactByContactId(Long contactId);

    /**
     * 查询邮件联系人列表
     * 
     * @param emailContact 邮件联系人
     * @return 邮件联系人集合
     */
    public List<EmailContact> selectEmailContactList(EmailContact emailContact);

    /**
     * 新增邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    public int insertEmailContact(EmailContact emailContact);

    /**
     * 修改邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    public int updateEmailContact(EmailContact emailContact);

    /**
     * 删除邮件联系人
     * 
     * @param contactId 邮件联系人主键
     * @return 结果
     */
    public int deleteEmailContactByContactId(Long contactId);

    /**
     * 批量删除邮件联系人
     * 
     * @param contactIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailContactByContactIds(Long[] contactIds);

    /**
     * 根据邮箱地址查询联系人
     * 
     * @param email 邮箱地址
     * @return 联系人
     */
    public EmailContact selectEmailContactByEmail(String email);

    /**
     * 根据群组ID查询联系人列表
     * 
     * @param groupId 群组ID
     * @return 联系人列表
     */
    public List<EmailContact> selectEmailContactByGroupId(Long groupId);

    /**
     * 根据标签查询联系人列表
     * 
     * @param tag 标签
     * @return 联系人列表
     */
    public List<EmailContact> selectEmailContactByTag(String tag);

    /**
     * 更新联系人统计信息
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    public int updateContactStatistics(Long contactId);

    /**
     * 查询回复率最高的联系人
     * 
     * @param limit 限制数量
     * @return 联系人列表
     */
    public List<EmailContact> selectTopReplyRateContacts(int limit);
}

