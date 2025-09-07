package com.ruoyi.system.service.email.impl;

import java.util.List;
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
        return emailSalesDataMapper.selectEmailSalesDataBySalesId(salesId);
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
        return emailSalesDataMapper.selectEmailSalesDataList(emailSalesData);
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
        return emailSalesDataMapper.insertEmailSalesData(emailSalesData);
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
        return emailSalesDataMapper.updateEmailSalesData(emailSalesData);
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
        return emailSalesDataMapper.selectEmailSalesDataByUserEmail(userEmail);
    }
}