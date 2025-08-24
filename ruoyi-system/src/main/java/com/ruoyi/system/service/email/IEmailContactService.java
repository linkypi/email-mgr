package com.ruoyi.system.service.email;

import java.util.List;
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
}
