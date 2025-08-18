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
import com.ruoyi.system.domain.email.EmailTag;
import com.ruoyi.system.service.email.IEmailTagService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮件标签Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/tag")
public class EmailTagController extends BaseController
{
    @Autowired
    private IEmailTagService emailTagService;

    /**
     * 查询邮件标签列表
     */
    @PreAuthorize("@ss.hasPermi('email:tag:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailTag emailTag)
    {
        startPage();
        List<EmailTag> list = emailTagService.selectEmailTagList(emailTag);
        return getDataTable(list);
    }

    /**
     * 导出邮件标签列表
     */
    @PreAuthorize("@ss.hasPermi('email:tag:export')")
    @Log(title = "邮件标签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EmailTag emailTag)
    {
        List<EmailTag> list = emailTagService.selectEmailTagList(emailTag);
        ExcelUtil<EmailTag> util = new ExcelUtil<EmailTag>(EmailTag.class);
        util.exportExcel(response, list, "邮件标签数据");
    }

    /**
     * 获取邮件标签详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:tag:query')")
    @GetMapping(value = "/{tagId}")
    public AjaxResult getInfo(@PathVariable("tagId") Long tagId)
    {
        return success(emailTagService.selectEmailTagByTagId(tagId));
    }

    /**
     * 新增邮件标签
     */
    @PreAuthorize("@ss.hasPermi('email:tag:add')")
    @Log(title = "邮件标签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailTag emailTag)
    {
        return toAjax(emailTagService.insertEmailTag(emailTag));
    }

    /**
     * 修改邮件标签
     */
    @PreAuthorize("@ss.hasPermi('email:tag:edit')")
    @Log(title = "邮件标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailTag emailTag)
    {
        return toAjax(emailTagService.updateEmailTag(emailTag));
    }

    /**
     * 删除邮件标签
     */
    @PreAuthorize("@ss.hasPermi('email:tag:remove')")
    @Log(title = "邮件标签", businessType = BusinessType.DELETE)
	@DeleteMapping("/{tagIds}")
    public AjaxResult remove(@PathVariable Long[] tagIds)
    {
        return toAjax(emailTagService.deleteEmailTagByTagIds(tagIds));
    }

    /**
     * 获取所有标签列表（用于下拉选择）
     */
    @GetMapping("/all")
    public AjaxResult getAllTags()
    {
        List<EmailTag> list = emailTagService.selectEmailTagList(new EmailTag());
        return success(list);
    }
}
