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
import com.ruoyi.system.domain.email.EmailContactGroup;
import com.ruoyi.system.service.email.IEmailContactGroupService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 联系人群组Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/group")
public class EmailContactGroupController extends BaseController
{
    @Autowired
    private IEmailContactGroupService emailContactGroupService;

    /**
     * 查询联系人群组列表
     */
    @PreAuthorize("@ss.hasPermi('email:group:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailContactGroup emailContactGroup)
    {
        startPage();
        List<EmailContactGroup> list = emailContactGroupService.selectEmailContactGroupList(emailContactGroup);
        return getDataTable(list);
    }

    /**
     * 导出联系人群组列表
     */
    @PreAuthorize("@ss.hasPermi('email:group:export')")
    @Log(title = "联系人群组", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailContactGroup emailContactGroup)
    {
        List<EmailContactGroup> list = emailContactGroupService.selectEmailContactGroupList(emailContactGroup);
        ExcelUtil<EmailContactGroup> util = new ExcelUtil<EmailContactGroup>(EmailContactGroup.class);
        util.exportExcel(response, list, "联系人群组数据");
    }

    /**
     * 获取联系人群组详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:group:query')")
    @GetMapping(value = "/{groupId}")
    public AjaxResult getInfo(@PathVariable("groupId") Long groupId)
    {
        return success(emailContactGroupService.selectEmailContactGroupByGroupId(groupId));
    }

    /**
     * 新增联系人群组
     */
    @PreAuthorize("@ss.hasPermi('email:group:add')")
    @Log(title = "联系人群组", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailContactGroup emailContactGroup)
    {
        return toAjax(emailContactGroupService.insertEmailContactGroup(emailContactGroup));
    }

    /**
     * 修改联系人群组
     */
    @PreAuthorize("@ss.hasPermi('email:group:edit')")
    @Log(title = "联系人群组", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailContactGroup emailContactGroup)
    {
        return toAjax(emailContactGroupService.updateEmailContactGroup(emailContactGroup));
    }

    /**
     * 删除联系人群组
     */
    @PreAuthorize("@ss.hasPermi('email:group:remove')")
    @Log(title = "联系人群组", businessType = BusinessType.DELETE)
	@DeleteMapping("/{groupIds}")
    public AjaxResult remove(@PathVariable Long[] groupIds)
    {
        return toAjax(emailContactGroupService.deleteEmailContactGroupByGroupIds(groupIds));
    }

    /**
     * 获取所有可用的群组
     */
    @GetMapping("/enabled")
    public AjaxResult getEnabledGroups()
    {
        List<EmailContactGroup> list = emailContactGroupService.selectAllEnabledGroups();
        return success(list);
    }
}
