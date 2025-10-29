package com.ruoyi.system.service.email.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.system.mapper.email.EmailContactMapper;
import com.ruoyi.system.domain.email.EmailContact;
import com.ruoyi.system.service.email.IEmailContactService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 邮件联系人Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@Service
public class EmailContactServiceImpl implements IEmailContactService 
{
    @Autowired
    private EmailContactMapper emailContactMapper;

    /**
     * 查询邮件联系人
     * 
     * @param contactId 邮件联系人主键
     * @return 邮件联系人
     */
    @Override
    public EmailContact selectEmailContactByContactId(Long contactId)
    {
        return emailContactMapper.selectEmailContactByContactId(contactId);
    }

    /**
     * 查询邮件联系人列表
     * 
     * @param emailContact 邮件联系人
     * @return 邮件联系人
     */
    @Override
    @DataScope(userAlias = "user_id")
    public List<EmailContact> selectEmailContactList(EmailContact emailContact)
    {
        return emailContactMapper.selectEmailContactList(emailContact);
    }

    /**
     * 新增邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    @Override
    @Transactional
    public int insertEmailContact(EmailContact emailContact)
    {
        // 检查邮箱是否已存在（只检查未删除的记录）
        if (StringUtils.isNotEmpty(emailContact.getEmail())) {
            System.out.println("=== 检查邮箱重复性 ===");
            System.out.println("要检查的邮箱: " + emailContact.getEmail());
            
            EmailContact existContact = emailContactMapper.selectEmailContactByEmail(emailContact.getEmail());
            System.out.println("查询结果: " + (existContact != null ? "找到重复邮箱" : "未找到重复邮箱"));
            
            if (StringUtils.isNotNull(existContact)) {
                System.out.println("重复邮箱详情: " + existContact.toString());
                throw new RuntimeException("邮箱地址 " + emailContact.getEmail() + " 已存在，请使用其他邮箱地址");
            }
            System.out.println("邮箱检查通过，可以插入");
        }
        
        emailContact.setCreateTime(DateUtils.getNowDate());
        System.out.println("=== 准备调用 Mapper 插入 ===");
        System.out.println("最终插入的数据: " + emailContact.toString());
        int result = emailContactMapper.insertEmailContact(emailContact);
        System.out.println("=== Mapper 插入完成 ===");
        System.out.println("插入结果: " + result);
        System.out.println("插入后的 contactId: " + emailContact.getContactId());
        return result;
    }

    /**
     * 修改邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    @Override
    @Transactional
    public int updateEmailContact(EmailContact emailContact)
    {
        // 检查邮箱是否已存在（排除自己）
        if (StringUtils.isNotEmpty(emailContact.getEmail())) {
            EmailContact existContact = emailContactMapper.selectEmailContactByEmailIncludeDeleted(emailContact.getEmail());
            if (StringUtils.isNotNull(existContact) && !existContact.getContactId().equals(emailContact.getContactId())) {
                throw new RuntimeException("邮箱地址 " + emailContact.getEmail() + " 已存在，请使用其他邮箱地址");
            }
        }
        
        emailContact.setUpdateTime(DateUtils.getNowDate());
        System.out.println("=== 准备调用 Mapper 更新 ===");
        System.out.println("最终更新的数据: " + emailContact.toString());
        int result = emailContactMapper.updateEmailContact(emailContact);
        System.out.println("=== Mapper 更新完成 ===");
        System.out.println("更新结果: " + result);
        return result;
    }

    /**
     * 批量删除邮件联系人
     * 
     * @param contactIds 需要删除的邮件联系人主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactByContactIds(Long[] contactIds)
    {
        return emailContactMapper.deleteEmailContactByContactIds(contactIds);
    }

    /**
     * 删除邮件联系人信息
     * 
     * @param contactId 邮件联系人主键
     * @return 结果
     */
    @Override
    public int deleteEmailContactByContactId(Long contactId)
    {
        return emailContactMapper.deleteEmailContactByContactId(contactId);
    }

    /**
     * 导入联系人数据
     * 
     * @param file 上传文件
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    @Transactional
    public String importContact(MultipartFile file, Boolean isUpdateSupport, String operName) throws Exception
    {
        System.out.println("=== 导入联系人调试信息 ===");
        System.out.println("file: " + file);
        System.out.println("file == null: " + (file == null));
        if (file != null) {
            System.out.println("file.isEmpty(): " + file.isEmpty());
            System.out.println("file.getOriginalFilename(): " + file.getOriginalFilename());
            System.out.println("file.getSize(): " + file.getSize());
        }
        System.out.println("isUpdateSupport: " + isUpdateSupport);
        System.out.println("operName: " + operName);
        
        if (file == null || file.isEmpty())
        {
            throw new RuntimeException("导入文件不能为空！");
        }
        
        ExcelUtil<EmailContact> util = new ExcelUtil<EmailContact>(EmailContact.class);
        List<EmailContact> contactList = util.importExcel(file.getInputStream());
        
        if (StringUtils.isNull(contactList) || contactList.size() == 0)
        {
            throw new RuntimeException("导入联系人数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (EmailContact contact : contactList)
        {
            try
            {
                // 验证是否存在这个联系人（包括已删除的）
                EmailContact existContact = emailContactMapper.selectEmailContactByEmailIncludeDeleted(contact.getEmail());
                if (StringUtils.isNull(existContact))
                {
                    // 邮箱不存在，直接插入
                    // 设置默认值
                    if (StringUtils.isEmpty(contact.getStatus())) {
                        contact.setStatus("0"); // 默认正常状态
                    }
                    if (contact.getSendCount() == null) {
                        contact.setSendCount(0);
                    }
                    if (contact.getReplyCount() == null) {
                        contact.setReplyCount(0);
                    }
                    if (contact.getOpenCount() == null) {
                        contact.setOpenCount(0);
                    }
                    if (contact.getReplyRate() == null) {
                        contact.setReplyRate(0.0);
                    }
                    
                    contact.setCreateBy(operName);
                    System.out.println("=== 准备插入新联系人 ===");
                    System.out.println("联系人信息: " + contact.toString());
                    int insertResult = this.insertEmailContact(contact);
                    System.out.println("插入结果: " + insertResult);
                    successNum++;
                    successMsg.append("<br>" + successNum + "、邮箱 " + contact.getEmail() + " 导入成功");
                }
                else if ("2".equals(existContact.getDeleted()))
                {
                    // 邮箱存在但已删除，恢复该记录
                    // 设置默认值
                    if (StringUtils.isEmpty(contact.getStatus())) {
                        contact.setStatus("0"); // 默认正常状态
                    }
                    
                    contact.setContactId(existContact.getContactId());
                    contact.setDeleted("0"); // 恢复为未删除状态
                    contact.setUpdateBy(operName);
                    System.out.println("=== 准备恢复已删除联系人 ===");
                    System.out.println("联系人信息: " + contact.toString());
                    int updateResult = this.updateEmailContact(contact);
                    System.out.println("更新结果: " + updateResult);
                    successNum++;
                    successMsg.append("<br>" + successNum + "、邮箱 " + contact.getEmail() + " 恢复成功");
                }
                else if (isUpdateSupport)
                {
                    // 邮箱存在且未删除，根据isUpdateSupport决定是否更新
                    // 设置默认值
                    if (StringUtils.isEmpty(contact.getStatus())) {
                        contact.setStatus("0"); // 默认正常状态
                    }
                    
                    contact.setContactId(existContact.getContactId());
                    contact.setUpdateBy(operName);
                    System.out.println("=== 准备更新现有联系人 ===");
                    System.out.println("联系人信息: " + contact.toString());
                    int updateResult = this.updateEmailContact(contact);
                    System.out.println("更新结果: " + updateResult);
                    successNum++;
                    successMsg.append("<br>" + successNum + "、邮箱 " + contact.getEmail() + " 更新成功");
                }
                else
                {
                    // 邮箱存在且未删除，且不允许更新
                    failureNum++;
                    failureMsg.append("<br>" + failureNum + "、邮箱 " + contact.getEmail() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br>" + failureNum + "、邮箱 " + contact.getEmail() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    /**
     * 根据群组ID查询联系人列表
     * 
     * @param groupId 群组ID
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectEmailContactByGroupId(Long groupId)
    {
        EmailContact emailContact = new EmailContact();
        emailContact.setGroupId(groupId);
        return emailContactMapper.selectEmailContactList(emailContact);
    }

    /**
     * 根据标签查询联系人列表
     * 
     * @param tag 标签
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectEmailContactByTag(String tag)
    {
        EmailContact emailContact = new EmailContact();
        emailContact.setTags(tag);
        return emailContactMapper.selectEmailContactList(emailContact);
    }

    /**
     * 查询回复率最高的联系人
     * 
     * @param limit 限制数量
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectTopReplyRateContacts(int limit)
    {
        return emailContactMapper.selectTopReplyRateContacts(limit);
    }

    /**
     * 根据群组ID列表查询联系人
     * 
     * @param groupIds 群组ID列表
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectContactsByGroupIds(List<String> groupIds)
    {
        if (groupIds == null || groupIds.isEmpty()) {
            return new ArrayList<>();
        }
        return emailContactMapper.selectContactsByGroupIds(groupIds);
    }

    /**
     * 根据标签ID列表查询联系人
     * 
     * @param tagIds 标签ID列表
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectContactsByTagIds(List<String> tagIds)
    {
        if (tagIds == null || tagIds.isEmpty()) {
            return new ArrayList<>();
        }
        return emailContactMapper.selectContactsByTagIds(tagIds);
    }

    /**
     * 根据联系人ID列表查询联系人
     * 
     * @param contactIds 联系人ID列表
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> selectContactsByIds(List<String> contactIds)
    {
        if (contactIds == null || contactIds.isEmpty()) {
            return new ArrayList<>();
        }
        return emailContactMapper.selectContactsByIds(contactIds);
    }

    /**
     * 根据邮箱地址查询联系人
     * 
     * @param email 邮箱地址
     * @return 联系人
     */
    @Override
    public EmailContact selectEmailContactByEmail(String email)
    {
        return emailContactMapper.selectEmailContactByEmail(email);
    }

    /**
     * 批量导入联系人
     * 
     * @param contacts 联系人列表
     * @return 结果信息
     */
    @Override
    public String batchImportContacts(List<EmailContact> contacts)
    {
        if (contacts == null || contacts.isEmpty()) {
            return "导入数据不能为空";
        }
        
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        
        for (EmailContact contact : contacts) {
            try {
                // 验证邮箱地址
                if (StringUtils.isEmpty(contact.getEmail())) {
                    failureNum++;
                    failureMsg.append("<br>" + failureNum + "、邮箱地址不能为空");
                    continue;
                }
                
                // 验证是否存在这个联系人
                EmailContact existContact = emailContactMapper.selectEmailContactByEmail(contact.getEmail());
                if (StringUtils.isNull(existContact)) {
                    contact.setCreateTime(DateUtils.getNowDate());
                    this.insertEmailContact(contact);
                    successNum++;
                    successMsg.append("<br>" + successNum + "、邮箱 " + contact.getEmail() + " 导入成功");
                } else {
                    failureNum++;
                    failureMsg.append("<br>" + failureNum + "、邮箱 " + contact.getEmail() + " 已存在");
                }
            } catch (Exception e) {
                failureNum++;
                String msg = "<br>" + failureNum + "、邮箱 " + contact.getEmail() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }
        
        if (failureNum > 0) {
            failureMsg.insert(0, "导入完成！成功 " + successNum + " 条，失败 " + failureNum + " 条，错误如下：");
            return failureMsg.toString();
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
            return successMsg.toString();
        }
    }

    /**
     * 更新联系人统计信息
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    @Override
    public int updateContactStatistics(Long contactId)
    {
        return emailContactMapper.updateContactStatistics(contactId);
    }

    /**
     * 批量更新联系人统计信息
     * 
     * @param contactIds 联系人ID列表
     * @return 更新的数量
     */
    @Override
    public int batchUpdateContactStatistics(List<Long> contactIds)
    {
        if (contactIds == null || contactIds.isEmpty()) {
            return 0;
        }
        
        int updateCount = 0;
        for (Long contactId : contactIds) {
            try {
                if (emailContactMapper.updateContactStatistics(contactId) > 0) {
                    updateCount++;
                }
            } catch (Exception e) {
                // 记录日志但不中断批量操作
                System.err.println("更新联系人统计信息失败，联系人ID: " + contactId + ", 错误: " + e.getMessage());
            }
        }
        return updateCount;
    }

    /**
     * 搜索联系人（支持多条件搜索）
     * 
     * @param searchParams 搜索参数
     * @return 联系人列表
     */
    @Override
    public List<EmailContact> searchContacts(EmailContact searchParams)
    {
        // 使用优化的分页查询方法，避免复杂子查询导致分页计数问题
        return emailContactMapper.selectEmailContactListForPage(searchParams);
    }

    /**
     * 获取联系人统计信息
     * 
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getContactStatistics()
    {
        Map<String, Object> statistics = new HashMap<>();
        
        // 获取所有联系人
        List<EmailContact> allContacts = emailContactMapper.selectEmailContactList(new EmailContact());
        
        int totalCount = allContacts.size();
        int activeCount = 0;
        int inactiveCount = 0;
        int highLevelCount = 0;
        int mediumLevelCount = 0;
        int lowLevelCount = 0;
        double totalReplyRate = 0.0;
        int totalSendCount = 0;
        int totalReplyCount = 0;
        int totalOpenCount = 0;
        
        for (EmailContact contact : allContacts) {
            // 统计状态
            if ("0".equals(contact.getStatus())) {
                activeCount++;
            } else {
                inactiveCount++;
            }
            
            // 统计等级
            if ("1".equals(contact.getLevel())) {
                highLevelCount++;
            } else if ("2".equals(contact.getLevel())) {
                mediumLevelCount++;
            } else {
                lowLevelCount++;
            }
            
            // 统计发送和回复数据
            if (contact.getSendCount() != null) {
                totalSendCount += contact.getSendCount();
            }
            if (contact.getReplyCount() != null) {
                totalReplyCount += contact.getReplyCount();
            }
            if (contact.getOpenCount() != null) {
                totalOpenCount += contact.getOpenCount();
            }
            if (contact.getReplyRate() != null) {
                totalReplyRate += contact.getReplyRate();
            }
        }
        
        // 计算平均回复率
        double avgReplyRate = totalCount > 0 ? totalReplyRate / totalCount : 0.0;
        
        statistics.put("totalCount", totalCount);
        statistics.put("activeCount", activeCount);
        statistics.put("inactiveCount", inactiveCount);
        statistics.put("highLevelCount", highLevelCount);
        statistics.put("mediumLevelCount", mediumLevelCount);
        statistics.put("lowLevelCount", lowLevelCount);
        statistics.put("totalSendCount", totalSendCount);
        statistics.put("totalReplyCount", totalReplyCount);
        statistics.put("totalOpenCount", totalOpenCount);
        statistics.put("avgReplyRate", Math.round(avgReplyRate * 100.0) / 100.0);
        
        return statistics;
    }

    /**
     * 验证邮箱地址是否已存在
     * 
     * @param email 邮箱地址
     * @return 是否存在
     */
    @Override
    public boolean isEmailExists(String email)
    {
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        EmailContact contact = emailContactMapper.selectEmailContactByEmail(email);
        return contact != null;
    }

    /**
     * 批量删除联系人（软删除）
     * 
     * @param contactIds 联系人ID列表
     * @return 结果
     */
    @Override
    public int batchDeleteContacts(List<Long> contactIds)
    {
        if (contactIds == null || contactIds.isEmpty()) {
            return 0;
        }
        
        Long[] ids = contactIds.toArray(new Long[0]);
        return emailContactMapper.deleteEmailContactByContactIds(ids);
    }

    /**
     * 恢复已删除的联系人
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    @Override
    public int restoreContact(Long contactId)
    {
        // 这里需要在 Mapper 中添加恢复方法
        // 暂时使用更新方法将 deleted 字段设置为 '0'
        EmailContact contact = new EmailContact();
        contact.setContactId(contactId);
        contact.setDeleted("0");
        contact.setUpdateTime(DateUtils.getNowDate());
        return emailContactMapper.updateEmailContact(contact);
    }

    /**
     * 批量恢复已删除的联系人
     * 
     * @param contactIds 联系人ID列表
     * @return 恢复的数量
     */
    @Override
    public int batchRestoreContacts(List<Long> contactIds)
    {
        if (contactIds == null || contactIds.isEmpty()) {
            return 0;
        }
        
        int restoreCount = 0;
        for (Long contactId : contactIds) {
            try {
                if (this.restoreContact(contactId) > 0) {
                    restoreCount++;
                }
            } catch (Exception e) {
                // 记录日志但不中断批量操作
                System.err.println("恢复联系人失败，联系人ID: " + contactId + ", 错误: " + e.getMessage());
            }
        }
        return restoreCount;
    }

    @Override
    public long countTotalContacts()
    {
        return emailContactMapper.countTotalContacts();
    }

    @Override
    public long countContactsBySearch(EmailContact searchParams)
    {
        return emailContactMapper.countContactsBySearch(searchParams);
    }
}
