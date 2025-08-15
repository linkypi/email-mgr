package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailSalesDataMapper;
import com.ruoyi.system.domain.email.EmailSalesData;
import com.ruoyi.system.service.email.IEmailSalesDataService;

/**
 * 销售数据Service业务层处理
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
     * 查询销售数据
     * 
     * @param salesId 销售数据主键
     * @return 销售数据
     */
    @Override
    public EmailSalesData selectEmailSalesDataBySalesId(Long salesId)
    {
        return emailSalesDataMapper.selectEmailSalesDataBySalesId(salesId);
    }

    /**
     * 查询销售数据列表
     * 
     * @param emailSalesData 销售数据
     * @return 销售数据
     */
    @Override
    public List<EmailSalesData> selectEmailSalesDataList(EmailSalesData emailSalesData)
    {
        return emailSalesDataMapper.selectEmailSalesDataList(emailSalesData);
    }

    /**
     * 新增销售数据
     * 
     * @param emailSalesData 销售数据
     * @return 结果
     */
    @Override
    public int insertEmailSalesData(EmailSalesData emailSalesData)
    {
        return emailSalesDataMapper.insertEmailSalesData(emailSalesData);
    }

    /**
     * 修改销售数据
     * 
     * @param emailSalesData 销售数据
     * @return 结果
     */
    @Override
    public int updateEmailSalesData(EmailSalesData emailSalesData)
    {
        return emailSalesDataMapper.updateEmailSalesData(emailSalesData);
    }

    /**
     * 批量删除销售数据
     * 
     * @param salesIds 需要删除的销售数据主键
     * @return 结果
     */
    @Override
    public int deleteEmailSalesDataBySalesIds(Long[] salesIds)
    {
        return emailSalesDataMapper.deleteEmailSalesDataBySalesIds(salesIds);
    }

    /**
     * 删除销售数据信息
     * 
     * @param salesId 销售数据主键
     * @return 结果
     */
    @Override
    public int deleteEmailSalesDataBySalesId(Long salesId)
    {
        return emailSalesDataMapper.deleteEmailSalesDataBySalesId(salesId);
    }

    /**
     * 根据联系人ID查询销售数据
     * 
     * @param contactId 联系人ID
     * @return 销售数据列表
     */
    @Override
    public List<EmailSalesData> selectEmailSalesDataByContactId(Long contactId)
    {
        return emailSalesDataMapper.selectEmailSalesDataByContactId(contactId);
    }

    /**
     * 批量插入销售数据
     * 
     * @param salesDataList 销售数据列表
     * @return 结果
     */
    @Override
    public int batchInsertEmailSalesData(List<EmailSalesData> salesDataList)
    {
        return emailSalesDataMapper.batchInsertEmailSalesData(salesDataList);
    }
}

