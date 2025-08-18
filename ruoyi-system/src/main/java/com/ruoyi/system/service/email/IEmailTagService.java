package com.ruoyi.system.service.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailTag;

/**
 * 邮件标签Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailTagService 
{
    /**
     * 查询邮件标签
     * 
     * @param tagId 邮件标签主键
     * @return 邮件标签
     */
    public EmailTag selectEmailTagByTagId(Long tagId);

    /**
     * 查询邮件标签列表
     * 
     * @param emailTag 邮件标签
     * @return 邮件标签集合
     */
    public List<EmailTag> selectEmailTagList(EmailTag emailTag);

    /**
     * 新增邮件标签
     * 
     * @param emailTag 邮件标签
     * @return 结果
     */
    public int insertEmailTag(EmailTag emailTag);

    /**
     * 修改邮件标签
     * 
     * @param emailTag 邮件标签
     * @return 结果
     */
    public int updateEmailTag(EmailTag emailTag);

    /**
     * 批量删除邮件标签
     * 
     * @param tagIds 需要删除的邮件标签主键集合
     * @return 结果
     */
    public int deleteEmailTagByTagIds(Long[] tagIds);

    /**
     * 删除邮件标签信息
     * 
     * @param tagId 邮件标签主键
     * @return 结果
     */
    public int deleteEmailTagByTagId(Long tagId);

    /**
     * 更新标签联系人数量
     * 
     * @param tagId 标签ID
     * @return 结果
     */
    public int updateTagContactCount(Long tagId);
}
