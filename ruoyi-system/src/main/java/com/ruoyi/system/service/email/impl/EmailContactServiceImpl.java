package com.ruoyi.system.service.email.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailContactMapper;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.service.email.IEmailContactService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 邮件联系人Service业务层处理
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
     * 查询邮件联系人
     * 
     * @param contactId 邮件联系人主键
     * @return 邮件联系人
     */
    @Override
    public EmailContact selectEmailContactByContactId(Long contactId)
    {
        return emailContactMapper.selectEmailContactByContactId(contactId);
    }

    /**
     * 查询邮件联系人列表
     * 
     * @param emailContact 邮件联系人
     * @return 邮件联系人
     */
    @Override
    public List<EmailContact> selectEmailContactList(EmailContact emailContact)
    {
        return emailContactMapper.selectEmailContactList(emailContact);
    }

    /**
     * 新增邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    @Override
    public int insertEmailContact(EmailContact emailContact)
    {
        emailContact.setCreateTime(DateUtils.getNowDate());
        return emailContactMapper.insertEmailContact(emailContact);
    }

    /**
     * 修改邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    @Override
    public int updateEmailContact(EmailContact emailContact)
    {
        emailContact.setUpdateTime(DateUtils.getNowDate());
        return emailContactMapper.updateEmailContact(emailContact);
    }

    /**
     * 批量删除邮件联系人
     * 
     * @param contactIds 需要删除的邮件联系人主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactByContactIds(Long[] contactIds)
    {
        return emailContactMapper.deleteEmailContactByContactIds(contactIds);
    }

    /**
     * 删除邮件联系人信息
     * 
     * @param contactId 邮件联系人主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactByContactId(Long contactId)
    {
        return emailContactMapper.deleteEmailContactByContactId(contactId);
    }

    /**
     * 导入联系人数据
     * 
     * @param file 上传文件
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importContact(MultipartFile file, Boolean isUpdateSupport, String operName) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            throw new RuntimeException("导入文件不能为空！");
        }
        
        ExcelUtil<EmailContact> util = new ExcelUtil<EmailContact>(EmailContact.class);
        List<EmailContact> contactList = util.importExcel(file.getInputStream());
        
        if (StringUtils.isNull(contactList) || contactList.size() == 0)
        {
            throw new RuntimeException("导入联系人数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (EmailContact contact : contactList)
        {
            try
            {
                // 验证是否存在这个联系人
                EmailContact existContact = emailContactMapper.selectEmailContactByEmail(contact.getEmail());
                if (StringUtils.isNull(existContact))
                {
                    contact.setCreateBy(operName);
                    this.insertEmailContact(contact);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、邮箱 " + contact.getEmail() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    contact.setContactId(existContact.getContactId());
                    contact.setUpdateBy(operName);
                    this.updateEmailContact(contact);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、邮箱 " + contact.getEmail() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、邮箱 " + contact.getEmail() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、邮箱 " + contact.getEmail() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 根据群组ID查询联系人列表
     * 
     * @param groupId 群组ID
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectEmailContactByGroupId(Long groupId)
    {
        EmailContact emailContact = new EmailContact();
        emailContact.setGroupId(groupId);
        return emailContactMapper.selectEmailContactList(emailContact);
    }

    /**
     * 根据标签查询联系人列表
     * 
     * @param tag 标签
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectEmailContactByTag(String tag)
    {
        EmailContact emailContact = new EmailContact();
        emailContact.setTags(tag);
        return emailContactMapper.selectEmailContactList(emailContact);
    }

    /**
     * 查询回复率最高的联系人
     * 
     * @param limit 限制数量
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectTopReplyRateContacts(int limit)
    {
        return emailContactMapper.selectTopReplyRateContacts(limit);
    }

    /**
     * 根据群组ID列表查询联系人
     * 
     * @param groupIds 群组ID列表
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectContactsByGroupIds(List<String> groupIds)
    {
        if (groupIds == null || groupIds.isEmpty()) {
            return new ArrayList<>();
        }
        return emailContactMapper.selectContactsByGroupIds(groupIds);
    }

    /**
     * 根据标签ID列表查询联系人
     * 
     * @param tagIds 标签ID列表
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectContactsByTagIds(List<String> tagIds)
    {
        if (tagIds == null || tagIds.isEmpty()) {
            return new ArrayList<>();
        }
        return emailContactMapper.selectContactsByTagIds(tagIds);
    }

    /**
     * 根据联系人ID列表查询联系人
     * 
     * @param contactIds 联系人ID列表
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectContactsByIds(List<String> contactIds)
    {
        if (contactIds == null || contactIds.isEmpty()) {
            return new ArrayList<>();
        }
        return emailContactMapper.selectContactsByIds(contactIds);
    }
}
