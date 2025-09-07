package com.ruoyi.system.service.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailSalesData;

/**
 * 邮件销售数据Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailSalesDataService 
{
    /**
     * 查询邮件销售数据
     * 
     * @param salesId 邮件销售数据主键
     * @return 邮件销售数据
     */
    public EmailSalesData selectEmailSalesDataBySalesId(Long salesId);

    /**
     * 查询邮件销售数据列表
     * 
     * @param emailSalesData 邮件销售数据
     * @return 邮件销售数据集合
     */
    public List<EmailSalesData> selectEmailSalesDataList(EmailSalesData emailSalesData);

    /**
     * 新增邮件销售数据
     * 
     * @param emailSalesData 邮件销售数据
     * @return 结果
     */
    public int insertEmailSalesData(EmailSalesData emailSalesData);

    /**
     * 修改邮件销售数据
     * 
     * @param emailSalesData 邮件销售数据
     * @return 结果
     */
    public int updateEmailSalesData(EmailSalesData emailSalesData);

    /**
     * 批量删除邮件销售数据
     * 
     * @param salesIds 需要删除的邮件销售数据主键集合
     * @return 结果
     */
    public int deleteEmailSalesDataBySalesIds(Long[] salesIds);

    /**
     * 删除邮件销售数据信息
     * 
     * @param salesId 邮件销售数据主键
     * @return 结果
     */
    public int deleteEmailSalesDataBySalesId(Long salesId);

    /**
     * 根据用户邮箱查询销售数据
     * 
     * @param userEmail 用户邮箱
     * @return 销售数据列表
     */
    public List<EmailSalesData> selectEmailSalesDataByUserEmail(String userEmail);
}