package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailContact;

/**
 * 联系人Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailContactMapper 
{
    /**
     * 查询联系人
     * 
     * @param contactId 联系人主键
     * @return 联系人
     */
    public EmailContact selectEmailContactByContactId(Long contactId);

    /**
     * 查询联系人列表
     * 
     * @param emailContact 联系人
     * @return 联系人集合
     */
    public List<EmailContact> selectEmailContactList(EmailContact emailContact);

    /**
     * 新增联系人
     * 
     * @param emailContact 联系人
     * @return 结果
     */
    public int insertEmailContact(EmailContact emailContact);

    /**
     * 修改联系人
     * 
     * @param emailContact 联系人
     * @return 结果
     */
    public int updateEmailContact(EmailContact emailContact);

    /**
     * 删除联系人
     * 
     * @param contactId 联系人主键
     * @return 结果
     */
    public int deleteEmailContactByContactId(Long contactId);

    /**
     * 批量删除联系人
     * 
     * @param contactIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailContactByContactIds(Long[] contactIds);

    /**
     * 批量导入联系人
     * 
     * @param contactList 联系人列表
     * @return 结果
     */
    public int batchInsertEmailContact(List<EmailContact> contactList);
}

