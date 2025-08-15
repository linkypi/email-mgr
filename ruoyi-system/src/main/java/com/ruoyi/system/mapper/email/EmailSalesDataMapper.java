package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailSalesData;

/**
 * 销售数据Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailSalesDataMapper 
{
    /**
     * 查询销售数据
     * 
     * @param salesId 销售数据主键
     * @return 销售数据
     */
    public EmailSalesData selectEmailSalesDataBySalesId(Long salesId);

    /**
     * 查询销售数据列表
     * 
     * @param emailSalesData 销售数据
     * @return 销售数据集合
     */
    public List<EmailSalesData> selectEmailSalesDataList(EmailSalesData emailSalesData);

    /**
     * 新增销售数据
     * 
     * @param emailSalesData 销售数据
     * @return 结果
     */
    public int insertEmailSalesData(EmailSalesData emailSalesData);

    /**
     * 修改销售数据
     * 
     * @param emailSalesData 销售数据
     * @return 结果
     */
    public int updateEmailSalesData(EmailSalesData emailSalesData);

    /**
     * 删除销售数据
     * 
     * @param salesId 销售数据主键
     * @return 结果
     */
    public int deleteEmailSalesDataBySalesId(Long salesId);

    /**
     * 批量删除销售数据
     * 
     * @param salesIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailSalesDataBySalesIds(Long[] salesIds);

    /**
     * 根据联系人ID查询销售数据
     * 
     * @param contactId 联系人ID
     * @return 销售数据列表
     */
    public List<EmailSalesData> selectEmailSalesDataByContactId(Long contactId);

    /**
     * 批量插入销售数据
     * 
     * @param salesDataList 销售数据列表
     * @return 结果
     */
    public int batchInsertEmailSalesData(List<EmailSalesData> salesDataList);
}
