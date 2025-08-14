package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailContactMapper;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.service.email.IEmailContactService;

/**
 * 联系人Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailContactServiceImpl implements IEmailContactService 
{
    @Autowired
    private EmailContactMapper emailContactMapper;

    /**
     * 查询联系人
     * 
     * @param contactId 联系人主键
     * @return 联系人
     */
    @Override
    public EmailContact selectEmailContactByContactId(Long contactId)
    {
        return emailContactMapper.selectEmailContactByContactId(contactId);
    }

    /**
     * 查询联系人列表
     * 
     * @param emailContact 联系人
     * @return 联系人
     */
    @Override
    public List<EmailContact> selectEmailContactList(EmailContact emailContact)
    {
        return emailContactMapper.selectEmailContactList(emailContact);
    }

    /**
     * 新增联系人
     * 
     * @param emailContact 联系人
     * @return 结果
     */
    @Override
    public int insertEmailContact(EmailContact emailContact)
    {
        return emailContactMapper.insertEmailContact(emailContact);
    }

    /**
     * 修改联系人
     * 
     * @param emailContact 联系人
     * @return 结果
     */
    @Override
    public int updateEmailContact(EmailContact emailContact)
    {
        return emailContactMapper.updateEmailContact(emailContact);
    }

    /**
     * 批量删除联系人
     * 
     * @param contactIds 需要删除的联系人主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactByContactIds(Long[] contactIds)
    {
        return emailContactMapper.deleteEmailContactByContactIds(contactIds);
    }

    /**
     * 删除联系人信息
     * 
     * @param contactId 联系人主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactByContactId(Long contactId)
    {
        return emailContactMapper.deleteEmailContactByContactId(contactId);
    }

    /**
     * 批量导入联系人
     * 
     * @param contactList 联系人列表
     * @return 结果
     */
    @Override
    public int batchInsertEmailContact(List<EmailContact> contactList)
    {
        return emailContactMapper.batchInsertEmailContact(contactList);
    }
}
