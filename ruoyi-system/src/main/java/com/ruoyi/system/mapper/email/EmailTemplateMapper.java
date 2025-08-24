package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailTemplate;

/**
 * 邮件模板Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailTemplateMapper 
{
    /**
     * 查询邮件模板
     * 
     * @param templateId 邮件模板主键
     * @return 邮件模板
     */
    public EmailTemplate selectEmailTemplateByTemplateId(Long templateId);

    /**
     * 查询邮件模板列表
     * 
     * @param emailTemplate 邮件模板
     * @return 邮件模板集合
     */
    public List<EmailTemplate> selectEmailTemplateList(EmailTemplate emailTemplate);

    /**
     * 新增邮件模板
     * 
     * @param emailTemplate 邮件模板
     * @return 结果
     */
    public int insertEmailTemplate(EmailTemplate emailTemplate);

    /**
     * 修改邮件模板
     * 
     * @param emailTemplate 邮件模板
     * @return 结果
     */
    public int updateEmailTemplate(EmailTemplate emailTemplate);

    /**
     * 删除邮件模板
     * 
     * @param templateId 邮件模板主键
     * @return 结果
     */
    public int deleteEmailTemplateByTemplateId(Long templateId);

    /**
     * 批量删除邮件模板
     * 
     * @param templateIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailTemplateByTemplateIds(Long[] templateIds);

    /**
     * 增加模板使用次数
     * 
     * @param templateId 模板ID
     * @return 结果
     */
    public int increaseTemplateUseCount(Long templateId);
}


