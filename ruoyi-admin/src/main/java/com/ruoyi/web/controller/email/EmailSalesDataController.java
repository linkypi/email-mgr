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
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.email.EmailSalesData;
import com.ruoyi.system.service.email.IEmailSalesDataService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮件销售数据Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/sales")
public class EmailSalesDataController extends BaseController
{
    @Autowired
    private IEmailSalesDataService emailSalesDataService;

    /**
     * 查询邮件销售数据列表
     */
    @PreAuthorize("@ss.hasPermi('email:sales:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailSalesData emailSalesData, String contactName, Long contactId)
    {
        // 优先使用 contactId 进行精确过滤（从联系人页面跳转时使用）
        if (contactId != null) {
            emailSalesData.setContactId(contactId);
            // 当使用 contactId 时，不使用 contactName 进行模糊查询
        } else if (contactName != null && !contactName.isEmpty()) {
            // 只有在没有 contactId 时，才使用 contactName 进行模糊查询
            emailSalesData.setUserName(contactName);
        }
        startPage();
        List<EmailSalesData> list = emailSalesDataService.selectEmailSalesDataList(emailSalesData);
        return getDataTable(list);
    }

    /**
     * 导出邮件销售数据列表
     */
    @PreAuthorize("@ss.hasPermi('email:sales:export')")
    @Log(title = "邮件销售数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailSalesData emailSalesData)
    {
        List<EmailSalesData> list = emailSalesDataService.selectEmailSalesDataList(emailSalesData);
        ExcelUtil<EmailSalesData> util = new ExcelUtil<EmailSalesData>(EmailSalesData.class);
        util.exportExcel(response, list, "邮件销售数据数据");
    }

    /**
     * 获取邮件销售数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:sales:query')")
    @GetMapping(value = "/{salesId}")
    public AjaxResult getInfo(@PathVariable("salesId") Long salesId)
    {
        return success(emailSalesDataService.selectEmailSalesDataBySalesId(salesId));
    }

    /**
     * 新增邮件销售数据
     */
    @PreAuthorize("@ss.hasPermi('email:sales:add')")
    @Log(title = "邮件销售数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailSalesData emailSalesData)
    {
        return toAjax(emailSalesDataService.insertEmailSalesData(emailSalesData));
    }

    /**
     * 修改邮件销售数据
     */
    @PreAuthorize("@ss.hasPermi('email:sales:edit')")
    @Log(title = "邮件销售数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailSalesData emailSalesData)
    {
        return toAjax(emailSalesDataService.updateEmailSalesData(emailSalesData));
    }

    /**
     * 删除邮件销售数据
     */
    @PreAuthorize("@ss.hasPermi('email:sales:remove')")
    @Log(title = "邮件销售数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{salesIds}")
    public AjaxResult remove(@PathVariable Long[] salesIds)
    {
        return toAjax(emailSalesDataService.deleteEmailSalesDataBySalesIds(salesIds));
    }

    /**
     * 根据用户邮箱查询销售数据
     */
    @GetMapping("/user/{userEmail}")
    public AjaxResult getSalesDataByUserEmail(@PathVariable("userEmail") String userEmail)
    {
        List<EmailSalesData> list = emailSalesDataService.selectEmailSalesDataByUserEmail(userEmail);
        return success(list);
    }
}