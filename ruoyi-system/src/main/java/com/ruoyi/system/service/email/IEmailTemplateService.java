package com.ruoyi.system.service.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailTemplate;

/**
 * 邮件模板Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailTemplateService 
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
     * 批量删除邮件模板
     * 
     * @param templateIds 需要删除的邮件模板主键集合
     * @return 结果
     */
    public int deleteEmailTemplateByTemplateIds(Long[] templateIds);

    /**
     * 删除邮件模板信息
     * 
     * @param templateId 邮件模板主键
     * @return 结果
     */
    public int deleteEmailTemplateByTemplateId(Long templateId);

    /**
     * 增加模板使用次数
     * 
     * @param templateId 模板ID
     * @return 结果
     */
    public int increaseTemplateUseCount(Long templateId);

    /**
     * 预览邮件模板
     * 
     * @param templateId 模板ID
     * @param variables 变量参数
     * @return 预览内容
     */
    public String previewTemplate(Long templateId, String variables);
}



