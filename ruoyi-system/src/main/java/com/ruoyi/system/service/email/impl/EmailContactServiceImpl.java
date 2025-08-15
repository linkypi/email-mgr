package com.ruoyi.system.service.email.impl;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailContactMapper;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.service.email.IEmailContactService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;

/**
 * 邮件联系人Service业务层处理
 * 
 * @author ruoyi
 * @date 2023-01-01
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
     * 查询邮件联系人统计列表
     * 
     * @param emailContact 邮件联系人
     * @return 邮件联系人
     */
    @Override
    public List<EmailContact> selectEmailContactStatisticsList(EmailContact emailContact)
    {
        return emailContactMapper.selectEmailContactStatisticsList(emailContact);
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
        emailContact.setCreateBy(SecurityUtils.getUsername());
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
        emailContact.setUpdateBy(SecurityUtils.getUsername());
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
     * 根据邮箱地址查询联系人
     * 
     * @param email 邮箱地址
     * @return 邮件联系人
     */
    @Override
    public EmailContact selectEmailContactByEmail(String email)
    {
        return emailContactMapper.selectEmailContactByEmail(email);
    }

    /**
     * 根据群组ID查询联系人列表
     * 
     * @param groupId 群组ID
     * @return 邮件联系人集合
     */
    @Override
    public List<EmailContact> selectEmailContactByGroupId(Long groupId)
    {
        return emailContactMapper.selectEmailContactByGroupId(groupId);
    }

    /**
     * 根据标签查询联系人列表
     * 
     * @param tag 标签
     * @return 邮件联系人集合
     */
    @Override
    public List<EmailContact> selectEmailContactByTag(String tag)
    {
        return emailContactMapper.selectEmailContactByTag(tag);
    }

    /**
     * 更新联系人发送统计
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    @Override
    public int updateEmailContactSendCount(Long contactId)
    {
        return emailContactMapper.updateEmailContactSendCount(contactId);
    }

    /**
     * 更新联系人回复统计
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    @Override
    public int updateEmailContactReplyCount(Long contactId)
    {
        return emailContactMapper.updateEmailContactReplyCount(contactId);
    }

    /**
     * 更新联系人打开统计
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    @Override
    public int updateEmailContactOpenCount(Long contactId)
    {
        return emailContactMapper.updateEmailContactOpenCount(contactId);
    }

    /**
     * 导入联系人数据
     * 
     * @param emailContactList 联系人数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importEmailContact(List<EmailContact> emailContactList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(emailContactList) || emailContactList.size() == 0)
        {
            throw new ServiceException("导入联系人数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (EmailContact emailContact : emailContactList)
        {
            try
            {
                // 验证是否存在这个联系人
                EmailContact existContact = emailContactMapper.selectEmailContactByEmail(emailContact.getEmail());
                if (StringUtils.isNull(existContact))
                {
                    emailContact.setCreateBy(operName);
                    emailContact.setCreateTime(DateUtils.getNowDate());
                    this.insertEmailContact(emailContact);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、邮箱 " + emailContact.getEmail() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    emailContact.setContactId(existContact.getContactId());
                    emailContact.setUpdateBy(operName);
                    emailContact.setUpdateTime(DateUtils.getNowDate());
                    this.updateEmailContact(emailContact);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、邮箱 " + emailContact.getEmail() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、邮箱 " + emailContact.getEmail() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、邮箱 " + emailContact.getEmail() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new ServiceException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }
}
