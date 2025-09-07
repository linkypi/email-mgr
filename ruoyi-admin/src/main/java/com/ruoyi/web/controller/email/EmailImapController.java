package com.ruoyi.web.controller.email;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.domain.email.EmailTrackRecord;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.system.service.email.EmailListener;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.DateUtils;

/**
 * IMAP监听管理Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/imap")
public class EmailImapController extends BaseController
{
    @Autowired
    private IEmailAccountService emailAccountService;
    
    @Autowired
    private EmailListener emailListener;

    /**
     * 查询IMAP监听列表
     */
    @PreAuthorize("@ss.hasPermi('email:imap:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailAccount emailAccount)
    {
        startPage();
        List<EmailAccount> list = emailAccountService.selectEmailAccountList(emailAccount);
        
        // 处理密码字段，不返回明文密码
        for (EmailAccount account : list) {
            // 清空密码字段
            account.setPassword(null);
            account.setImapPassword(null);
            
            // 设置邮箱地址字段（用于前端显示）
            if (account.getEmailAddress() != null) {
                // 这里可以添加邮箱地址的处理逻辑
            }
        }
        
        return getDataTable(list);
    }

    /**
     * 启动IMAP监听
     */
    @PreAuthorize("@ss.hasPermi('email:imap:start')")
    @Log(title = "启动IMAP监听", businessType = BusinessType.UPDATE)
    @PostMapping("/start/{accountId}")
    public AjaxResult start(@PathVariable("accountId") Long accountId)
    {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return error("邮箱账号不存在");
            }
            
            // 检查IMAP配置是否完整
            if (account.getImapHost() == null || account.getImapPort() == null || 
                account.getImapUsername() == null || account.getImapPassword() == null) {
                return error("IMAP配置不完整，请检查IMAP服务器、端口、用户名和密码");
            }
            
            // 启动IMAP监听服务（现在由EmailListener统一管理）
            // imapEmailSyncService.startImapListener(account);
            
            // 更新最后同步时间
            account.setLastSyncTime(DateUtils.getTime());
            emailAccountService.updateEmailAccountStatistics(account);
            
            return success("启动成功");
        } catch (Exception e) {
            return error("启动失败：" + e.getMessage());
        }
    }

    /**
     * 停止IMAP监听
     */
    @PreAuthorize("@ss.hasPermi('email:imap:stop')")
    @Log(title = "停止IMAP监听", businessType = BusinessType.UPDATE)
    @PostMapping("/stop/{accountId}")
    public AjaxResult stop(@PathVariable("accountId") Long accountId)
    {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return error("邮箱账号不存在");
            }
            
            // 更新最后同步时间
            account.setLastSyncTime(DateUtils.getTime());
            emailAccountService.updateEmailAccountStatistics(account);
            
            // TODO: 这里应该调用实际的IMAP监听服务停止逻辑
            // 例如：imapListenerService.stopListener(account);
            
            return success("停止成功");
        } catch (Exception e) {
            return error("停止失败：" + e.getMessage());
        }
    }

    /**
     * 重启IMAP监听
     */
    @PreAuthorize("@ss.hasPermi('email:imap:restart')")
    @Log(title = "重启IMAP监听", businessType = BusinessType.UPDATE)
    @PostMapping("/restart/{accountId}")
    public AjaxResult restart(@PathVariable("accountId") Long accountId)
    {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return error("邮箱账号不存在");
            }
            
            // 先停止监听
            account.setLastSyncTime(DateUtils.getTime());
            emailAccountService.updateEmailAccountStatistics(account);
            
            // TODO: 这里应该调用实际的IMAP监听服务停止逻辑
            // 例如：imapListenerService.stopListener(account);
            
            // 再启动监听
            account.setLastSyncTime(DateUtils.getTime());
            emailAccountService.updateEmailAccountStatistics(account);
            
            // TODO: 这里应该调用实际的IMAP监听服务启动逻辑
            // 例如：imapListenerService.startListener(account);
            
            return success("重启成功");
        } catch (Exception e) {
            return error("重启失败：" + e.getMessage());
        }
    }

    /**
     * 同步IMAP邮件
     */
    @PreAuthorize("@ss.hasPermi('email:imap:sync')")
    @Log(title = "同步IMAP邮件", businessType = BusinessType.UPDATE)
    @PostMapping("/sync/{accountId}")
    public AjaxResult sync(@PathVariable("accountId") Long accountId)
    {
        try {
            EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (account == null) {
                return error("邮箱账号不存在");
            }
            
            // 检查IMAP配置是否完整
            if (account.getImapHost() == null || account.getImapPort() == null || 
                account.getImapUsername() == null || account.getImapPassword() == null) {
                return error("IMAP配置不完整，请检查IMAP服务器、端口、用户名和密码");
            }
            
            // 调用EmailListener同步服务
            emailListener.syncEmailStatistics(account);
            
            // 更新最后同步时间
            account.setLastSyncTime(DateUtils.getTime());
            emailAccountService.updateEmailAccountStatistics(account);
            
            return success("同步成功");
        } catch (Exception e) {
            return error("同步失败：" + e.getMessage());
        }
    }
    
    /**
     * 发送邮件
     */
    @PreAuthorize("@ss.hasPermi('email:imap:send')")
    @Log(title = "发送邮件", businessType = BusinessType.INSERT)
    @PostMapping("/send")
    public AjaxResult sendEmail(@RequestBody Map<String, String> params)
    {
        try
        {
            Long accountId = Long.parseLong(params.get("accountId"));
            String recipient = params.get("recipient");
            String subject = params.get("subject");
            String content = params.get("content");
            
            if (accountId == null || recipient == null || subject == null || content == null)
            {
                return error("参数不完整");
            }
            
            EmailAccount emailAccount = emailAccountService.selectEmailAccountByAccountId(accountId);
            if (emailAccount == null)
            {
                return error("邮箱账号不存在");
            }
            
            // 检查SMTP配置是否完整
            if (emailAccount.getSmtpHost() == null || emailAccount.getSmtpPort() == null || 
                emailAccount.getPassword() == null) {
                return error("SMTP配置不完整，请检查SMTP服务器、端口和密码");
            }
            
            // 发送邮件并跟踪
            Long taskId = params.get("taskId") != null ? Long.parseLong(params.get("taskId")) : null;
            String messageId = emailListener.sendEmailWithTracking(emailAccount, recipient, subject, content, taskId);
            
            return success("邮件发送成功");
        }
        catch (Exception e) {
            return error("发送失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取邮件跟踪状态
     */
    @PreAuthorize("@ss.hasPermi('email:imap:query')")
    @GetMapping("/tracking-status/{messageId}")
    public AjaxResult getTrackingStatus(@PathVariable("messageId") String messageId)
    {
        try
        {
            EmailTrackRecord record = emailListener.getTrackRecord(messageId);
            if (record != null)
            {
                return success(record);
            }
            else
            {
                return error("邮件跟踪记录不存在");
            }
        }
        catch (Exception e) {
            return error("获取失败：" + e.getMessage());
        }
    }
}
