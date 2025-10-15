package com.ruoyi.web.controller.email;

import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.IEmailPersonalTrackService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 个人邮件Controller
 * 
 * @author ruoyi
 * @date 2024-01-15
 */
@RestController
@RequestMapping("/email/personal")
public class EmailPersonalController extends BaseController
{
    @Autowired
    private IEmailPersonalTrackService emailPersonalTrackService;

    /**
     * 查询收件箱列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:inbox:list')")
    @GetMapping("/inbox/list")
    public TableDataInfo inboxList(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectInboxList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 查询收件箱列表（简化路径）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:inbox:list')")
    @GetMapping("/inbox")
    public TableDataInfo inbox(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectInboxList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 查询发件箱列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:sent:list')")
    @GetMapping("/sent/list")
    public TableDataInfo sentList(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectSentList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 查询发件箱列表（简化路径）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:sent:list')")
    @GetMapping("/sent")
    public TableDataInfo sent(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectSentList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 查询星标邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:starred:list')")
    @GetMapping("/starred/list")
    public TableDataInfo starredList(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectStarredList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 查询星标邮件列表（简化路径）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:starred:list')")
    @GetMapping("/starred")
    public TableDataInfo starred(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectStarredList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 查询已删除邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:deleted:list')")
    @GetMapping("/deleted/list")
    public TableDataInfo deletedList(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectDeletedList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 查询已删除邮件列表（简化路径）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:deleted:list')")
    @GetMapping("/deleted")
    public TableDataInfo deleted(EmailTrackRecord emailTrackRecord)
    {
        startPage();
        List<EmailTrackRecord> list = emailPersonalTrackService.selectDeletedList(emailTrackRecord);
        return getDataTable(list);
    }

    /**
     * 导出个人邮件列表
     */
    @PreAuthorize("@ss.hasPermi('email:personal:export')")
    @Log(title = "个人邮件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public AjaxResult export(EmailTrackRecord emailTrackRecord)
    {
        List<EmailTrackRecord> list = emailPersonalTrackService.selectSentList(emailTrackRecord);
        ExcelUtil<EmailTrackRecord> util = new ExcelUtil<EmailTrackRecord>(EmailTrackRecord.class);
        return util.exportExcel(list, "个人邮件数据");
    }

    /**
     * 获取个人邮件详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:personal:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(emailPersonalTrackService.selectEmailTrackRecordById(id));
    }

    /**
     * 新增个人邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:add')")
    @Log(title = "个人邮件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailTrackRecord emailTrackRecord)
    {
        return toAjax(emailPersonalTrackService.sendEmail(emailTrackRecord));
    }

    /**
     * 修改个人邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:edit')")
    @Log(title = "个人邮件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailTrackRecord emailTrackRecord)
    {
        return toAjax(emailPersonalTrackService.updateEmailTrackRecord(emailTrackRecord));
    }

    /**
     * 删除个人邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:remove')")
    @Log(title = "个人邮件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.moveToDeleted(ids));
    }

    /**
     * 标记为星标
     */
    @PreAuthorize("@ss.hasPermi('email:personal:star')")
    @Log(title = "标记星标", businessType = BusinessType.UPDATE)
    @PutMapping("/star/{ids}")
    public AjaxResult markAsStarred(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.markAsStarred(ids));
    }

    /**
     * 取消星标
     */
    @PreAuthorize("@ss.hasPermi('email:personal:unstar')")
    @Log(title = "取消星标", businessType = BusinessType.UPDATE)
    @PutMapping("/unstar/{ids}")
    public AjaxResult unmarkAsStarred(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.unmarkAsStarred(ids));
    }

    /**
     * 标记为已读
     */
    @PreAuthorize("@ss.hasPermi('email:personal:read')")
    @Log(title = "标记已读", businessType = BusinessType.UPDATE)
    @PutMapping("/read/{ids}")
    public AjaxResult markAsRead(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.markAsRead(ids));
    }

    /**
     * 标记为未读
     */
    @PreAuthorize("@ss.hasPermi('email:personal:unread')")
    @Log(title = "标记未读", businessType = BusinessType.UPDATE)
    @PutMapping("/unread/{ids}")
    public AjaxResult markAsUnread(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.markAsUnread(ids));
    }

    /**
     * 标记为重要
     */
    @PreAuthorize("@ss.hasPermi('email:personal:important')")
    @Log(title = "标记重要", businessType = BusinessType.UPDATE)
    @PutMapping("/important/{ids}")
    public AjaxResult markAsImportant(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.markAsImportant(ids));
    }

    /**
     * 取消重要标记
     */
    @PreAuthorize("@ss.hasPermi('email:personal:unimportant')")
    @Log(title = "取消重要标记", businessType = BusinessType.UPDATE)
    @PutMapping("/unimportant/{ids}")
    public AjaxResult unmarkAsImportant(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.unmarkAsImportant(ids));
    }

    /**
     * 恢复邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:restore')")
    @Log(title = "恢复邮件", businessType = BusinessType.UPDATE)
    @PutMapping("/restore/{ids}")
    public AjaxResult restoreEmail(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.restoreFromDeleted(ids));
    }

    /**
     * 彻底删除邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:delete')")
    @Log(title = "彻底删除邮件", businessType = BusinessType.DELETE)
    @DeleteMapping("/permanent/{ids}")
    public AjaxResult deletePermanently(@PathVariable Long[] ids)
    {
        return toAjax(emailPersonalTrackService.deletePermanently(ids));
    }

    /**
     * 获取未读邮件数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/unread/count")
    public AjaxResult getUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getInboxUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取收件箱未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/inbox/unread/count")
    public AjaxResult getInboxUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getInboxUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取收件箱未读数量（track版本）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/inbox/unread/count/track")
    public AjaxResult getInboxUnreadCountTrack()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getInboxUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取发件箱未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/sent/unread/count")
    public AjaxResult getSentUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getSentUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取发件箱未读数量（track版本）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/sent/unread/count/track")
    public AjaxResult getSentUnreadCountTrack()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getSentUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取发件箱总数量（track版本）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/sent/total/count/track")
    public AjaxResult getSentTotalCountTrack()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getSentTotalCount(userId);
        return success(count);
    }

    /**
     * 获取星标邮件数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/starred/count")
    public AjaxResult getStarredCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getStarredCount(userId);
        return success(count);
    }

    /**
     * 获取星标邮件数量（track版本）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/starred/count/track")
    public AjaxResult getStarredCountTrack()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getStarredCount(userId);
        return success(count);
    }

    /**
     * 获取已删除邮件数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/deleted/count")
    public AjaxResult getDeletedCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getDeletedCount(userId);
        return success(count);
    }

    /**
     * 获取已删除邮件数量（track版本）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/deleted/count/track")
    public AjaxResult getDeletedCountTrack()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getDeletedCount(userId);
        return success(count);
    }

    /**
     * 获取已删除邮件未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/deleted/unread/count")
    public AjaxResult getDeletedUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getDeletedUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取星标邮件未读数量
     */
    @PreAuthorize("@ss.hasPermi('email:personal:count')")
    @GetMapping("/starred/unread/count")
    public AjaxResult getStarredUnreadCount()
    {
        Long userId = SecurityUtils.getUserId();
        int count = emailPersonalTrackService.getStarredUnreadCount(userId);
        return success(count);
    }

    /**
     * 获取邮件统计信息
     */
    @PreAuthorize("@ss.hasPermi('email:personal:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getEmailStatistics()
    {
        Long userId = SecurityUtils.getUserId();
        Map<String, Object> statistics = emailPersonalTrackService.getEmailStatistics(userId);
        return success(statistics);
    }

    /**
     * 获取个人邮件跟踪统计信息
     */
    @PreAuthorize("@ss.hasPermi('email:personal:statistics')")
    @GetMapping("/statistics/track")
    public AjaxResult getEmailTrackStatistics()
    {
        Long userId = SecurityUtils.getUserId();
        Map<String, Object> statistics = emailPersonalTrackService.getEmailStatistics(userId);
        return success(statistics);
    }

    /**
     * 获取邮件详情（用于回复和转发）
     */
    @PreAuthorize("@ss.hasPermi('email:personal:query')")
    @GetMapping("/detail/{emailId}")
    public AjaxResult getEmailDetail(@PathVariable("emailId") Long emailId)
    {
        try {
            EmailTrackRecord emailDetail = emailPersonalTrackService.getEmailDetail(emailId);
            if (emailDetail == null) {
                return error("邮件不存在");
            }
            return success(emailDetail);
        } catch (Exception e) {
            logger.error("获取邮件详情失败", e);
            return error("获取邮件详情失败: " + e.getMessage());
        }
    }

    /**
     * 发送回复邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:reply')")
    @Log(title = "发送回复邮件", businessType = BusinessType.INSERT)
    @PostMapping("/reply")
    public AjaxResult sendReply(@RequestBody EmailTrackRecord replyEmail)
    {
        try {
            int result = emailPersonalTrackService.sendReply(replyEmail);
            if (result > 0) {
                return success("回复邮件发送成功");
            } else {
                return error("回复邮件发送失败");
            }
        } catch (Exception e) {
            logger.error("发送回复邮件失败", e);
            return error("发送回复邮件失败: " + e.getMessage());
        }
    }

    /**
     * 发送转发邮件
     */
    @PreAuthorize("@ss.hasPermi('email:personal:forward')")
    @Log(title = "发送转发邮件", businessType = BusinessType.INSERT)
    @PostMapping("/forward")
    public AjaxResult sendForward(@RequestBody EmailTrackRecord forwardEmail)
    {
        try {
            int result = emailPersonalTrackService.sendForward(forwardEmail);
            if (result > 0) {
                return success("转发邮件发送成功");
            } else {
                return error("转发邮件发送失败");
            }
        } catch (Exception e) {
            logger.error("发送转发邮件失败", e);
            return error("发送转发邮件失败: " + e.getMessage());
        }
    }
}