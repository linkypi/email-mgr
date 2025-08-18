package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailTagMapper;
import com.ruoyi.system.domain.email.EmailTag;
import com.ruoyi.system.service.email.IEmailTagService;

/**
 * 邮件标签Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailTagServiceImpl implements IEmailTagService 
{
    @Autowired
    private EmailTagMapper emailTagMapper;

    /**
     * 查询邮件标签
     * 
     * @param tagId 邮件标签主键
     * @return 邮件标签
     */
    @Override
    public EmailTag selectEmailTagByTagId(Long tagId)
    {
        return emailTagMapper.selectEmailTagByTagId(tagId);
    }

    /**
     * 查询邮件标签列表
     * 
     * @param emailTag 邮件标签
     * @return 邮件标签
     */
    @Override
    public List<EmailTag> selectEmailTagList(EmailTag emailTag)
    {
        return emailTagMapper.selectEmailTagList(emailTag);
    }

    /**
     * 新增邮件标签
     * 
     * @param emailTag 邮件标签
     * @return 结果
     */
    @Override
    public int insertEmailTag(EmailTag emailTag)
    {
        return emailTagMapper.insertEmailTag(emailTag);
    }

    /**
     * 修改邮件标签
     * 
     * @param emailTag 邮件标签
     * @return 结果
     */
    @Override
    public int updateEmailTag(EmailTag emailTag)
    {
        return emailTagMapper.updateEmailTag(emailTag);
    }

    /**
     * 批量删除邮件标签
     * 
     * @param tagIds 需要删除的邮件标签主键
     * @return 结果
     */
    @Override
    public int deleteEmailTagByTagIds(Long[] tagIds)
    {
        return emailTagMapper.deleteEmailTagByTagIds(tagIds);
    }

    /**
     * 删除邮件标签信息
     * 
     * @param tagId 邮件标签主键
     * @return 结果
     */
    @Override
    public int deleteEmailTagByTagId(Long tagId)
    {
        return emailTagMapper.deleteEmailTagByTagId(tagId);
    }

    /**
     * 更新标签联系人数量
     * 
     * @param tagId 标签ID
     * @return 结果
     */
    @Override
    public int updateTagContactCount(Long tagId)
    {
        return emailTagMapper.updateTagContactCount(tagId);
    }
}
