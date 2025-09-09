package com.ruoyi.web.controller.email;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailAccount;
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮箱账号Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/account")
public class EmailAccountController extends BaseController
{
    private static final Logger logger = LoggerFactory.getLogger(EmailAccountController.class);
    
    @Autowired
    private IEmailAccountService emailAccountService;

    /**
     * 查询邮箱账号列表
     */
    @PreAuthorize("@ss.hasPermi('email:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailAccount emailAccount)
    {
        // 添加调试日志
        logger.info("查询邮箱账号列表，参数：senderId={}, accountName={}, emailAddress={}, status={}", 
                   emailAccount.getSenderId(), emailAccount.getAccountName(), 
                   emailAccount.getEmailAddress(), emailAccount.getStatus());
        
        startPage();
        List<EmailAccount> list = emailAccountService.selectEmailAccountList(emailAccount);
        
        // 处理密码字段，不返回明文密码
        for (EmailAccount account : list) {
            account.setPassword(null);
            account.setImapPassword(null);
        }
        
        return getDataTable(list);
    }

    /**
     * 导出邮箱账号列表
     */
    @PreAuthorize("@ss.hasPermi('email:account:export')")
    @Log(title = "邮箱账号", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailAccount emailAccount)
    {
        List<EmailAccount> list = emailAccountService.selectEmailAccountList(emailAccount);
        ExcelUtil<EmailAccount> util = new ExcelUtil<EmailAccount>(EmailAccount.class);
        util.exportExcel(response, list, "邮箱账号数据");
    }

    /**
     * 获取邮箱账号详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") Long accountId)
    {
        EmailAccount account = emailAccountService.selectEmailAccountByAccountId(accountId);
        if (account != null) {
            // 不返回密码字段
            account.setPassword(null);
            account.setImapPassword(null);
        }
        return success(account);
    }

    /**
     * 获取邮箱账号详细信息（包含解密后的密码，用于编辑）
     */
    @PreAuthorize("@ss.hasPermi('email:account:edit')")
    @GetMapping(value = "/{accountId}/edit")
    public AjaxResult getInfoForEdit(@PathVariable("accountId") Long accountId)
    {
        EmailAccount account = emailAccountService.selectEmailAccountWithDecryptedPassword(accountId);
        return success(account);
    }

    /**
     * 新增邮箱账号
     */
    @PreAuthorize("@ss.hasPermi('email:account:add')")
    @Log(title = "邮箱账号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailAccount emailAccount)
    {
        return toAjax(emailAccountService.insertEmailAccount(emailAccount));
    }

    /**
     * 修改邮箱账号
     */
    @PreAuthorize("@ss.hasPermi('email:account:edit')")
    @Log(title = "邮箱账号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailAccount emailAccount)
    {
        return toAjax(emailAccountService.updateEmailAccount(emailAccount));
    }

    /**
     * 批量更新邮箱账号状态
     */
    @PreAuthorize("@ss.hasPermi('email:account:edit')")
    @Log(title = "批量更新邮箱账号状态", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/status")
    public AjaxResult batchUpdateStatus(@RequestBody EmailAccount emailAccount)
    {
        return toAjax(emailAccountService.batchUpdateAccountStatus(emailAccount));
    }

    /**
     * 删除邮箱账号
     */
    @PreAuthorize("@ss.hasPermi('email:account:remove')")
    @Log(title = "邮箱账号", businessType = BusinessType.DELETE)
	@DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds)
    {
        return toAjax(emailAccountService.deleteEmailAccountByAccountIds(accountIds));
    }

    /**
     * 获取所有可用账号
     */
    @GetMapping("/all")
    public AjaxResult getAllAccounts()
    {
        EmailAccount emailAccount = new EmailAccount();
        emailAccount.setStatus("0"); // 只查询正常状态的账号
        List<EmailAccount> list = emailAccountService.selectEmailAccountList(emailAccount);
        
        // 处理密码字段，不返回明文密码
        for (EmailAccount account : list) {
            account.setPassword(null);
            account.setImapPassword(null);
        }
        
        return success(list);
    }

    /**
     * 导出邮箱账号模板
     */
    @PreAuthorize("@ss.hasPermi('email:account:export')")
    @PostMapping("/exportTemplate")
    public void exportTemplate(HttpServletResponse response, EmailAccount emailAccount)
    {
        List<EmailAccount> list = emailAccountService.selectEmailAccountTemplate(emailAccount);
        ExcelUtil<EmailAccount> util = new ExcelUtil<EmailAccount>(EmailAccount.class);
        util.exportExcel(response, list, "邮箱账号导入模板");
    }

    /**
     * 导入邮箱账号数据
     */
    @PreAuthorize("@ss.hasPermi('email:account:import')")
    @Log(title = "邮箱账号", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<EmailAccount> util = new ExcelUtil<EmailAccount>(EmailAccount.class);
        List<EmailAccount> accountList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = emailAccountService.importAccount(accountList, updateSupport, operName);
        return success(message);
    }
}
