package com.ruoyi.system.service.email.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.email.EmailContactGroupMapper;
import com.ruoyi.system.domain.email.EmailContactGroup;
import com.ruoyi.system.service.email.IEmailContactGroupService;

/**
 * 联系人群组Service业务层处理
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
     * 查询联系人群组
     * 
     * @param groupId 联系人群组主键
     * @return 联系人群组
     */
    @Override
    public EmailContactGroup selectEmailContactGroupByGroupId(Long groupId)
    {
        return emailContactGroupMapper.selectEmailContactGroupByGroupId(groupId);
    }

    /**
     * 查询联系人群组列表
     * 
     * @param emailContactGroup 联系人群组
     * @return 联系人群组
     */
    @Override
    public List<EmailContactGroup> selectEmailContactGroupList(EmailContactGroup emailContactGroup)
    {
        return emailContactGroupMapper.selectEmailContactGroupList(emailContactGroup);
    }

    /**
     * 新增联系人群组
     * 
     * @param emailContactGroup 联系人群组
     * @return 结果
     */
    @Override
    public int insertEmailContactGroup(EmailContactGroup emailContactGroup)
    {
        return emailContactGroupMapper.insertEmailContactGroup(emailContactGroup);
    }

    /**
     * 修改联系人群组
     * 
     * @param emailContactGroup 联系人群组
     * @return 结果
     */
    @Override
    public int updateEmailContactGroup(EmailContactGroup emailContactGroup)
    {
        return emailContactGroupMapper.updateEmailContactGroup(emailContactGroup);
    }

    /**
     * 批量删除联系人群组
     * 
     * @param groupIds 需要删除的联系人群组主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactGroupByGroupIds(Long[] groupIds)
    {
        return emailContactGroupMapper.deleteEmailContactGroupByGroupIds(groupIds);
    }

    /**
     * 删除联系人群组信息
     * 
     * @param groupId 联系人群组主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactGroupByGroupId(Long groupId)
    {
        return emailContactGroupMapper.deleteEmailContactGroupByGroupId(groupId);
    }

    /**
     * 查询所有可用的群组
     * 
     * @return 群组列表
     */
    @Override
    public List<EmailContactGroup> selectAllEnabledGroups()
    {
        return emailContactGroupMapper.selectAllEnabledGroups();
    }
}
