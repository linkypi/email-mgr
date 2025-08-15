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
 * 销售数据Controller
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
     * 查询销售数据列表
     */
    @PreAuthorize("@ss.hasPermi('email:sales:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailSalesData emailSalesData)
    {
        startPage();
        List<EmailSalesData> list = emailSalesDataService.selectEmailSalesDataList(emailSalesData);
        return getDataTable(list);
    }

    /**
     * 导出销售数据列表
     */
    @PreAuthorize("@ss.hasPermi('email:sales:export')")
    @Log(title = "销售数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailSalesData emailSalesData)
    {
        List<EmailSalesData> list = emailSalesDataService.selectEmailSalesDataList(emailSalesData);
        ExcelUtil<EmailSalesData> util = new ExcelUtil<EmailSalesData>(EmailSalesData.class);
        util.exportExcel(response, list, "销售数据");
    }

    /**
     * 获取销售数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:sales:query')")
    @GetMapping(value = "/{salesId}")
    public AjaxResult getInfo(@PathVariable("salesId") Long salesId)
    {
        return success(emailSalesDataService.selectEmailSalesDataBySalesId(salesId));
    }

    /**
     * 新增销售数据
     */
    @PreAuthorize("@ss.hasPermi('email:sales:add')")
    @Log(title = "销售数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailSalesData emailSalesData)
    {
        return toAjax(emailSalesDataService.insertEmailSalesData(emailSalesData));
    }

    /**
     * 修改销售数据
     */
    @PreAuthorize("@ss.hasPermi('email:sales:edit')")
    @Log(title = "销售数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailSalesData emailSalesData)
    {
        return toAjax(emailSalesDataService.updateEmailSalesData(emailSalesData));
    }

    /**
     * 删除销售数据
     */
    @PreAuthorize("@ss.hasPermi('email:sales:remove')")
    @Log(title = "销售数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{salesIds}")
    public AjaxResult remove(@PathVariable Long[] salesIds)
    {
        return toAjax(emailSalesDataService.deleteEmailSalesDataBySalesIds(salesIds));
    }

    /**
     * 根据联系人ID查询销售数据
     */
    @GetMapping("/contact/{contactId}")
    public AjaxResult getSalesDataByContactId(@PathVariable("contactId") Long contactId)
    {
        List<EmailSalesData> list = emailSalesDataService.selectEmailSalesDataByContactId(contactId);
        return success(list);
    }

    /**
     * 批量导入销售数据
     */
    @PreAuthorize("@ss.hasPermi('email:sales:import')")
    @Log(title = "批量导入销售数据", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestBody List<EmailSalesData> salesDataList)
    {
        return toAjax(emailSalesDataService.batchInsertEmailSalesData(salesDataList));
    }
}

