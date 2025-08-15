package com.ruoyi.web.controller.email;

import java.util.List;
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
import com.ruoyi.system.service.email.IEmailAccountService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮箱账号Controller
 * 
 * @author ruoyi
 * @date 2023-01-01
 */
@RestController
@RequestMapping("/email/account")
public class EmailAccountController extends BaseController
{
    @Autowired
    private IEmailAccountService emailAccountService;

    /**
     * 查询邮箱账号列表
     */
    @PreAuthorize("@ss.hasPermi('email:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailAccount emailAccount)
    {
        startPage();
        List<EmailAccount> list = emailAccountService.selectEmailAccountList(emailAccount);
        return getDataTable(list);
    }

    /**
     * 导出邮箱账号列表
     */
    @PreAuthorize("@ss.hasPermi('email:account:export')")
    @Log(title = "邮箱账号", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(EmailAccount emailAccount)
    {
        List<EmailAccount> list = emailAccountService.selectEmailAccountList(emailAccount);
        ExcelUtil<EmailAccount> util = new ExcelUtil<EmailAccount>(EmailAccount.class);
        return util.exportExcel(list, "邮箱账号数据");
    }

    /**
     * 获取邮箱账号详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:account:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable("accountId") Long accountId)
    {
        return success(emailAccountService.selectEmailAccountByAccountId(accountId));
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
     * 测试邮箱账号连接
     */
    @PreAuthorize("@ss.hasPermi('email:account:test')")
    @Log(title = "测试邮箱连接", businessType = BusinessType.OTHER)
    @PostMapping("/test")
    public AjaxResult testConnection(@RequestBody EmailAccount emailAccount)
    {
        try
        {
            boolean result = emailAccountService.testEmailAccount(emailAccount);
            return success(result ? "连接测试成功" : "连接测试失败");
        }
        catch (Exception e)
        {
            return error("连接测试失败：" + e.getMessage());
        }
    }

    /**
     * 获取可用邮箱账号列表
     */
    @GetMapping("/available")
    public AjaxResult getAvailableAccounts()
    {
        List<EmailAccount> list = emailAccountService.getAvailableAccounts();
        return success(list);
    }
}
