package com.ruoyi.system.service.email.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailSalesDataMapper;
import com.ruoyi.system.domain.email.EmailSalesData;
import com.ruoyi.system.service.email.IEmailSalesDataService;

/**
 * 邮件销售数据Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailSalesDataServiceImpl implements IEmailSalesDataService 
{
    @Autowired
    private EmailSalesDataMapper emailSalesDataMapper;

    /**
     * 查询邮件销售数据
     * 
     * @param salesId 邮件销售数据主键
     * @return 邮件销售数据
     */
    @Override
    public EmailSalesData selectEmailSalesDataBySalesId(Long salesId)
    {
        EmailSalesData data = emailSalesDataMapper.selectEmailSalesDataBySalesId(salesId);
        if (data != null) {
            // 处理联系人数据转换：将字符串转换为数组
            if (data.getUserName() != null && !data.getUserName().isEmpty()) {
                data.setContactNames(Arrays.asList(data.getUserName().split(",")));
            } else {
                data.setContactNames(new ArrayList<>());
            }
            if (data.getUserEmail() != null && !data.getUserEmail().isEmpty()) {
                data.setContactEmails(Arrays.asList(data.getUserEmail().split(",")));
            } else {
                data.setContactEmails(new ArrayList<>());
            }
        }
        return data;
    }

    /**
     * 查询邮件销售数据列表
     * 
     * @param emailSalesData 邮件销售数据
     * @return 邮件销售数据
     */
    @Override
    public List<EmailSalesData> selectEmailSalesDataList(EmailSalesData emailSalesData)
    {
        List<EmailSalesData> list = emailSalesDataMapper.selectEmailSalesDataList(emailSalesData);
        // 处理联系人数据转换：将字符串转换为数组
        for (EmailSalesData data : list) {
            if (data.getUserName() != null && !data.getUserName().isEmpty()) {
                data.setContactNames(Arrays.asList(data.getUserName().split(",")));
            } else {
                data.setContactNames(new ArrayList<>());
            }
            if (data.getUserEmail() != null && !data.getUserEmail().isEmpty()) {
                data.setContactEmails(Arrays.asList(data.getUserEmail().split(",")));
            } else {
                data.setContactEmails(new ArrayList<>());
            }
        }
        return list;
    }

    /**
     * 新增邮件销售数据
     * 
     * @param emailSalesData 邮件销售数据
     * @return 结果
     */
    @Override
    public int insertEmailSalesData(EmailSalesData emailSalesData)
    {
        // 确保创建时间不为空
        if (emailSalesData.getCreateTime() == null) {
            emailSalesData.setCreateTime(new java.util.Date());
        }
        // 插入销售数据
        int result = emailSalesDataMapper.insertEmailSalesData(emailSalesData);
        // 如果插入成功且有联系人ID列表，插入关联关系
        if (result > 0 && emailSalesData.getSalesId() != null && 
            emailSalesData.getContactIds() != null && !emailSalesData.getContactIds().isEmpty()) {
            emailSalesDataMapper.insertSalesDataContactRelations(
                emailSalesData.getSalesId(), emailSalesData.getContactIds());
        }
        return result;
    }

    /**
     * 修改邮件销售数据
     * 
     * @param emailSalesData 邮件销售数据
     * @return 结果
     */
    @Override
    public int updateEmailSalesData(EmailSalesData emailSalesData)
    {
        int result = emailSalesDataMapper.updateEmailSalesData(emailSalesData);
        // 如果更新成功且有联系人ID列表，先删除旧关联关系，再插入新关联关系
        if (result > 0 && emailSalesData.getSalesId() != null && 
            emailSalesData.getContactIds() != null) {
            // 删除旧的关联关系
            emailSalesDataMapper.deleteSalesDataContactRelations(emailSalesData.getSalesId());
            // 如果有新的联系人ID列表，插入新的关联关系
            if (!emailSalesData.getContactIds().isEmpty()) {
                emailSalesDataMapper.insertSalesDataContactRelations(
                    emailSalesData.getSalesId(), emailSalesData.getContactIds());
            }
        }
        return result;
    }

    /**
     * 批量删除邮件销售数据
     * 
     * @param salesIds 需要删除的邮件销售数据主键
     * @return 结果
     */
    @Override
    public int deleteEmailSalesDataBySalesIds(Long[] salesIds)
    {
        // 先删除关联关系
        for (Long salesId : salesIds) {
            emailSalesDataMapper.deleteSalesDataContactRelations(salesId);
        }
        // 再删除销售数据
        return emailSalesDataMapper.deleteEmailSalesDataBySalesIds(salesIds);
    }

    /**
     * 删除邮件销售数据信息
     * 
     * @param salesId 邮件销售数据主键
     * @return 结果
     */
    @Override
    public int deleteEmailSalesDataBySalesId(Long salesId)
    {
        // 先删除关联关系
        emailSalesDataMapper.deleteSalesDataContactRelations(salesId);
        // 再删除销售数据
        return emailSalesDataMapper.deleteEmailSalesDataBySalesId(salesId);
    }

    /**
     * 根据用户邮箱查询销售数据
     * 
     * @param userEmail 用户邮箱
     * @return 销售数据列表
     */
    @Override
    public List<EmailSalesData> selectEmailSalesDataByUserEmail(String userEmail)
    {
        List<EmailSalesData> list = emailSalesDataMapper.selectEmailSalesDataByUserEmail(userEmail);
        // 处理联系人数据转换：将字符串转换为数组
        for (EmailSalesData data : list) {
            if (data.getUserName() != null && !data.getUserName().isEmpty()) {
                data.setContactNames(Arrays.asList(data.getUserName().split(",")));
            } else {
                data.setContactNames(new ArrayList<>());
            }
            if (data.getUserEmail() != null && !data.getUserEmail().isEmpty()) {
                data.setContactEmails(Arrays.asList(data.getUserEmail().split(",")));
            } else {
                data.setContactEmails(new ArrayList<>());
            }
        }
        return list;
    }
}