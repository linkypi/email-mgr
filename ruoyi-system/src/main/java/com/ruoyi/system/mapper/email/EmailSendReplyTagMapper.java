package com.ruoyi.system.mapper.email;

import java.util.List;
import com.ruoyi.system.domain.email.EmailSendReplyTag;

/**
 * 发送回复标签Mapper接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface EmailSendReplyTagMapper 
{
    /**
     * 查询发送回复标签
     * 
     * @param tagId 发送回复标签主键
     * @return 发送回复标签
     */
    public EmailSendReplyTag selectEmailSendReplyTagByTagId(Long tagId);

    /**
     * 查询发送回复标签列表
     * 
     * @param emailSendReplyTag 发送回复标签
     * @return 发送回复标签集合
     */
    public List<EmailSendReplyTag> selectEmailSendReplyTagList(EmailSendReplyTag emailSendReplyTag);

    /**
     * 新增发送回复标签
     * 
     * @param emailSendReplyTag 发送回复标签
     * @return 结果
     */
    public int insertEmailSendReplyTag(EmailSendReplyTag emailSendReplyTag);

    /**
     * 修改发送回复标签
     * 
     * @param emailSendReplyTag 发送回复标签
     * @return 结果
     */
    public int updateEmailSendReplyTag(EmailSendReplyTag emailSendReplyTag);

    /**
     * 删除发送回复标签
     * 
     * @param tagId 发送回复标签主键
     * @return 结果
     */
    public int deleteEmailSendReplyTagByTagId(Long tagId);

    /**
     * 批量删除发送回复标签
     * 
     * @param tagIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteEmailSendReplyTagByTagIds(Long[] tagIds);

    /**
     * 根据联系人ID查询发送回复标签
     * 
     * @param contactId 联系人ID
     * @return 发送回复标签列表
     */
    public List<EmailSendReplyTag> selectEmailSendReplyTagByContactId(Long contactId);

    /**
     * 更新发送统计
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    public int updateSendStatistics(Long contactId);

    /**
     * 更新回复统计
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    public int updateReplyStatistics(Long contactId);

    /**
     * 更新打开统计
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    public int updateOpenStatistics(Long contactId);
}
