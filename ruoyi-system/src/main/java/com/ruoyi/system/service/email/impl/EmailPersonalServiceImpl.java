package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailPersonalMapper;
import com.ruoyi.system.domain.email.EmailPersonal;
import com.ruoyi.system.service.email.IEmailPersonalService;

/**
 * 个人邮件Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailPersonalServiceImpl implements IEmailPersonalService 
{
    @Autowired
    private EmailPersonalMapper emailPersonalMapper;

    /**
     * 查询个人邮件
     * 
     * @param emailId 个人邮件主键
     * @return 个人邮件
     */
    @Override
    public EmailPersonal selectEmailPersonalByEmailId(Long emailId)
    {
        return emailPersonalMapper.selectEmailPersonalByEmailId(emailId);
    }

    /**
     * 查询个人邮件列表
     * 
     * @param emailPersonal 个人邮件
     * @return 个人邮件
     */
    @Override
    public List<EmailPersonal> selectEmailPersonalList(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.selectEmailPersonalList(emailPersonal);
    }

    /**
     * 新增个人邮件
     * 
     * @param emailPersonal 个人邮件
     * @return 结果
     */
    @Override
    public int insertEmailPersonal(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.insertEmailPersonal(emailPersonal);
    }

    /**
     * 修改个人邮件
     * 
     * @param emailPersonal 个人邮件
     * @return 结果
     */
    @Override
    public int updateEmailPersonal(EmailPersonal emailPersonal)
    {
        return emailPersonalMapper.updateEmailPersonal(emailPersonal);
    }

    /**
     * 批量删除个人邮件
     * 
     * @param emailIds 需要删除的个人邮件主键
     * @return 结果
     */
    @Override
    public int deleteEmailPersonalByEmailIds(Long[] emailIds)
    {
        return emailPersonalMapper.deleteEmailPersonalByEmailIds(emailIds);
    }

    /**
     * 删除个人邮件信息
     * 
     * @param emailId 个人邮件主键
     * @return 结果
     */
    @Override
    public int deleteEmailPersonalByEmailId(Long emailId)
    {
        return emailPersonalMapper.deleteEmailPersonalByEmailId(emailId);
    }

    /**
     * 查询收件箱邮件
     * 
     * @param emailPersonal 查询条件
     * @return 邮件列表
     */
    @Override
    public List<EmailPersonal> selectInboxList(EmailPersonal emailPersonal)
    {
        emailPersonal.setEmailType("1"); // 收件
        return emailPersonalMapper.selectEmailPersonalList(emailPersonal);
    }

    /**
     * 查询发件箱邮件
     * 
     * @param emailPersonal 查询条件
     * @return 邮件列表
     */
    @Override
    public List<EmailPersonal> selectSentList(EmailPersonal emailPersonal)
    {
        emailPersonal.setEmailType("2"); // 发件
        return emailPersonalMapper.selectEmailPersonalList(emailPersonal);
    }

    /**
     * 查询星标邮件
     * 
     * @param emailPersonal 查询条件
     * @return 邮件列表
     */
    @Override
    public List<EmailPersonal> selectStarredList(EmailPersonal emailPersonal)
    {
        emailPersonal.setIsStarred("1"); // 星标
        return emailPersonalMapper.selectEmailPersonalList(emailPersonal);
    }

    /**
     * 查询已删除邮件
     * 
     * @param emailPersonal 查询条件
     * @return 邮件列表
     */
    @Override
    public List<EmailPersonal> selectDeletedList(EmailPersonal emailPersonal)
    {
        emailPersonal.setStatus("1"); // 已删除
        return emailPersonalMapper.selectEmailPersonalList(emailPersonal);
    }

    /**
     * 标记邮件为已读
     * 
     * @param emailId 邮件ID
     * @return 结果
     */
    @Override
    public int markAsRead(Long emailId)
    {
        EmailPersonal emailPersonal = new EmailPersonal();
        emailPersonal.setEmailId(emailId);
        emailPersonal.setIsRead("1");
        return emailPersonalMapper.updateEmailPersonal(emailPersonal);
    }

    /**
     * 标记邮件为星标
     * 
     * @param emailId 邮件ID
     * @param isStarred 是否星标
     * @return 结果
     */
    @Override
    public int markAsStarred(Long emailId, String isStarred)
    {
        EmailPersonal emailPersonal = new EmailPersonal();
        emailPersonal.setEmailId(emailId);
        emailPersonal.setIsStarred(isStarred);
        return emailPersonalMapper.updateEmailPersonal(emailPersonal);
    }

    /**
     * 获取未读邮件数量
     * 
     * @return 未读邮件数量
     */
    @Override
    public int getUnreadCount()
    {
        EmailPersonal emailPersonal = new EmailPersonal();
        emailPersonal.setIsRead("0"); // 未读
        List<EmailPersonal> list = emailPersonalMapper.selectEmailPersonalList(emailPersonal);
        return list != null ? list.size() : 0;
    }
}
