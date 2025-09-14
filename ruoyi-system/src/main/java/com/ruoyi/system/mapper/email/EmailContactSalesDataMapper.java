package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailContactSalesData;

/**
 * 联系人销售数据Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
public interface EmailContactSalesDataMapper 
{
    /**
     * 查询联系人销售数据
     * 
     * @param salesId 联系人销售数据主键
     * @return 联系人销售数据
     */
    public EmailContactSalesData selectEmailContactSalesDataBySalesId(Long salesId);

    /**
     * 查询联系人销售数据列表
     * 
     * @param emailContactSalesData 联系人销售数据
     * @return 联系人销售数据集合
     */
    public List<EmailContactSalesData> selectEmailContactSalesDataList(EmailContactSalesData emailContactSalesData);

    /**
     * 新增联系人销售数据
     * 
     * @param emailContactSalesData 联系人销售数据
     * @return 结果
     */
    public int insertEmailContactSalesData(EmailContactSalesData emailContactSalesData);

    /**
     * 修改联系人销售数据
     * 
     * @param emailContactSalesData 联系人销售数据
     * @return 结果
     */
    public int updateEmailContactSalesData(EmailContactSalesData emailContactSalesData);

    /**
     * 删除联系人销售数据
     * 
     * @param salesId 联系人销售数据主键
     * @return 结果
     */
    public int deleteEmailContactSalesDataBySalesId(Long salesId);

    /**
     * 批量删除联系人销售数据
     * 
     * @param salesIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailContactSalesDataBySalesIds(Long[] salesIds);

    /**
     * 批量插入联系人销售数据
     * 
     * @param salesDataList 销售数据列表
     * @return 结果
     */
    public int batchInsertEmailContactSalesData(List<EmailContactSalesData> salesDataList);

    /**
     * 插入销售数据与收件人关联关系
     * 
     * @param salesId 销售数据ID
     * @param contactIds 收件人ID列表
     * @return 结果
     */
    public int insertSalesDataContactRelations(Long salesId, List<Long> contactIds);

    /**
     * 删除销售数据与收件人关联关系
     * 
     * @param salesId 销售数据ID
     * @return 结果
     */
    public int deleteSalesDataContactRelations(Long salesId);
}
