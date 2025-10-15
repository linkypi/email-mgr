package com.ruoyi.system.service.email;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.email.EmailContact;
import org.springframework.web.multipart.MultipartFile;

/**
 * 邮件联系人Service接口
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
public interface IEmailContactService 
{
    /**
     * 查询邮件联系人
     * 
     * @param contactId 邮件联系人主键
     * @return 邮件联系人
     */
    public EmailContact selectEmailContactByContactId(Long contactId);

    /**
     * 查询邮件联系人列表
     * 
     * @param emailContact 邮件联系人
     * @return 邮件联系人集合
     */
    public List<EmailContact> selectEmailContactList(EmailContact emailContact);

    /**
     * 新增邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    public int insertEmailContact(EmailContact emailContact);

    /**
     * 修改邮件联系人
     * 
     * @param emailContact 邮件联系人
     * @return 结果
     */
    public int updateEmailContact(EmailContact emailContact);

    /**
     * 批量删除邮件联系人
     * 
     * @param contactIds 需要删除的邮件联系人主键集合
     * @return 结果
     */
    public int deleteEmailContactByContactIds(Long[] contactIds);

    /**
     * 删除邮件联系人信息
     * 
     * @param contactId 邮件联系人主键
     * @return 结果
     */
    public int deleteEmailContactByContactId(Long contactId);

    /**
     * 导入联系人数据
     * 
     * @param file 上传文件
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importContact(MultipartFile file, Boolean isUpdateSupport, String operName) throws Exception;

    /**
     * 根据群组ID查询联系人列表
     * 
     * @param groupId 群组ID
     * @return 联系人列表
     */
    public List<EmailContact> selectEmailContactByGroupId(Long groupId);

    /**
     * 根据标签查询联系人列表
     * 
     * @param tag 标签
     * @return 联系人列表
     */
    public List<EmailContact> selectEmailContactByTag(String tag);

    /**
     * 查询回复率最高的联系人
     * 
     * @param limit 限制数量
     * @return 联系人列表
     */
    public List<EmailContact> selectTopReplyRateContacts(int limit);

    /**
     * 根据群组ID列表查询联系人
     * 
     * @param groupIds 群组ID列表
     * @return 联系人列表
     */
    public List<EmailContact> selectContactsByGroupIds(List<String> groupIds);

    /**
     * 根据标签ID列表查询联系人
     * 
     * @param tagIds 标签ID列表
     * @return 联系人列表
     */
    public List<EmailContact> selectContactsByTagIds(List<String> tagIds);

    /**
     * 根据联系人ID列表查询联系人
     * 
     * @param contactIds 联系人ID列表
     * @return 联系人列表
     */
    public List<EmailContact> selectContactsByIds(List<String> contactIds);

    /**
     * 根据邮箱地址查询联系人
     * 
     * @param email 邮箱地址
     * @return 联系人
     */
    public EmailContact selectEmailContactByEmail(String email);

    /**
     * 批量导入联系人
     * 
     * @param contacts 联系人列表
     * @return 结果信息
     */
    public String batchImportContacts(List<EmailContact> contacts);

    /**
     * 更新联系人统计信息
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    public int updateContactStatistics(Long contactId);

    /**
     * 批量更新联系人统计信息
     * 
     * @param contactIds 联系人ID列表
     * @return 更新的数量
     */
    public int batchUpdateContactStatistics(List<Long> contactIds);

    /**
     * 搜索联系人（支持多条件搜索）
     * 
     * @param searchParams 搜索参数
     * @return 联系人列表
     */
    public List<EmailContact> searchContacts(EmailContact searchParams);

    /**
     * 获取联系人统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Object> getContactStatistics();

    /**
     * 验证邮箱地址是否已存在
     * 
     * @param email 邮箱地址
     * @return 是否存在
     */
    public boolean isEmailExists(String email);

    /**
     * 批量删除联系人（软删除）
     * 
     * @param contactIds 联系人ID列表
     * @return 结果
     */
    public int batchDeleteContacts(List<Long> contactIds);

    /**
     * 恢复已删除的联系人
     * 
     * @param contactId 联系人ID
     * @return 结果
     */
    public int restoreContact(Long contactId);

    /**
     * 批量恢复已删除的联系人
     * 
     * @param contactIds 联系人ID列表
     * @return 恢复的数量
     */
    public int batchRestoreContacts(List<Long> contactIds);

    /**
     * 统计总联系人数量
     * 
     * @return 总联系人数量
     */
    public long countTotalContacts();
}
