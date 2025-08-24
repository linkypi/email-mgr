package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailContactGroupMapper;
import com.ruoyi.system.domain.email.EmailContactGroup;
import com.ruoyi.system.service.email.IEmailContactGroupService;
import com.ruoyi.common.utils.DateUtils;

/**
 * 邮件联系人群组Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailContactGroupServiceImpl implements IEmailContactGroupService 
{
    @Autowired
    private EmailContactGroupMapper emailContactGroupMapper;

    /**
     * 查询邮件联系人群组
     * 
     * @param groupId 邮件联系人群组主键
     * @return 邮件联系人群组
     */
    @Override
    public EmailContactGroup selectEmailContactGroupByGroupId(Long groupId)
    {
        return emailContactGroupMapper.selectEmailContactGroupByGroupId(groupId);
    }

    /**
     * 查询邮件联系人群组列表
     * 
     * @param emailContactGroup 邮件联系人群组
     * @return 邮件联系人群组
     */
    @Override
    public List<EmailContactGroup> selectEmailContactGroupList(EmailContactGroup emailContactGroup)
    {
        return emailContactGroupMapper.selectEmailContactGroupList(emailContactGroup);
    }

    /**
     * 新增邮件联系人群组
     * 
     * @param emailContactGroup 邮件联系人群组
     * @return 结果
     */
    @Override
    public int insertEmailContactGroup(EmailContactGroup emailContactGroup)
    {
        emailContactGroup.setCreateTime(DateUtils.getNowDate());
        return emailContactGroupMapper.insertEmailContactGroup(emailContactGroup);
    }

    /**
     * 修改邮件联系人群组
     * 
     * @param emailContactGroup 邮件联系人群组
     * @return 结果
     */
    @Override
    public int updateEmailContactGroup(EmailContactGroup emailContactGroup)
    {
        emailContactGroup.setUpdateTime(DateUtils.getNowDate());
        return emailContactGroupMapper.updateEmailContactGroup(emailContactGroup);
    }

    /**
     * 批量删除邮件联系人群组
     * 
     * @param groupIds 需要删除的邮件联系人群组主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactGroupByGroupIds(Long[] groupIds)
    {
        return emailContactGroupMapper.deleteEmailContactGroupByGroupIds(groupIds);
    }

    /**
     * 删除邮件联系人群组信息
     * 
     * @param groupId 邮件联系人群组主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactGroupByGroupId(Long groupId)
    {
        return emailContactGroupMapper.deleteEmailContactGroupByGroupId(groupId);
    }

    /**
     * 更新群组联系人数量
     * 
     * @param groupId 群组ID
     * @return 结果
     */
    @Override
    public int updateGroupContactCount(Long groupId)
    {
        return emailContactGroupMapper.updateGroupContactCount(groupId);
    }
}
