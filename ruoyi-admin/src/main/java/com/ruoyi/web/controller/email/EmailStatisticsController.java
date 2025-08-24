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
import com.ruoyi.system.domain.email.EmailStatistics;
import com.ruoyi.system.service.email.IEmailStatisticsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 邮件统计Controller
 * 
 * @author ruoyi
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/email/statistics")
public class EmailStatisticsController extends BaseController
{
    @Autowired
    private IEmailStatisticsService emailStatisticsService;

    /**
     * 查询邮件统计列表
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:list')")
    @GetMapping("/list")
    public TableDataInfo list(EmailStatistics emailStatistics)
    {
        startPage();
        List<EmailStatistics> list = emailStatisticsService.selectEmailStatisticsList(emailStatistics);
        return getDataTable(list);
    }

    /**
     * 导出邮件统计列表
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:export')")
    @Log(title = "邮件统计", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(EmailStatistics emailStatistics)
    {
        List<EmailStatistics> list = emailStatisticsService.selectEmailStatisticsList(emailStatistics);
        ExcelUtil<EmailStatistics> util = new ExcelUtil<EmailStatistics>(EmailStatistics.class);
        return util.exportExcel(list, "邮件统计数据");
    }

    /**
     * 获取邮件统计详细信息
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:query')")
    @GetMapping(value = "/{statisticsId}")
    public AjaxResult getInfo(@PathVariable("statisticsId") Long statisticsId)
    {
        return success(emailStatisticsService.selectEmailStatisticsByStatId(statisticsId));
    }

    /**
     * 根据任务ID查询邮件统计列表
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:query')")
    @GetMapping("/task/{taskId}")
    public AjaxResult getStatisticsByTaskId(@PathVariable("taskId") Long taskId)
    {
        // 根据任务ID查询统计信息，这里需要根据实际需求实现
        // 目前暂时返回空列表，后续可以根据需要扩展
        return success("根据任务ID查询统计功能待实现");
    }

    /**
     * 新增邮件统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:add')")
    @Log(title = "邮件统计", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EmailStatistics emailStatistics)
    {
        return toAjax(emailStatisticsService.insertEmailStatistics(emailStatistics));
    }

    /**
     * 修改邮件统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:edit')")
    @Log(title = "邮件统计", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EmailStatistics emailStatistics)
    {
        return toAjax(emailStatisticsService.updateEmailStatistics(emailStatistics));
    }

    /**
     * 删除邮件统计
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:remove')")
    @Log(title = "邮件统计", businessType = BusinessType.DELETE)
	@DeleteMapping("/{statisticsIds}")
    public AjaxResult remove(@PathVariable Long[] statisticsIds)
    {
        return toAjax(emailStatisticsService.deleteEmailStatisticsByStatIds(statisticsIds));
    }

    /**
     * 手动同步邮件统计数据
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:sync')")
    @Log(title = "同步邮件统计数据", businessType = BusinessType.UPDATE)
    @PostMapping("/sync")
    public AjaxResult syncStatistics()
    {
        try
        {
            // 手动同步功能待实现，目前通过定时任务自动同步
            return success("邮件统计数据同步功能待实现");
        }
        catch (Exception e)
        {
            return error("邮件统计数据同步失败: " + e.getMessage());
        }
    }

    /**
     * 同步指定任务的邮件统计数据
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:sync')")
    @Log(title = "同步任务邮件统计数据", businessType = BusinessType.UPDATE)
    @PostMapping("/sync/{taskId}")
    public AjaxResult syncStatisticsByTaskId(@PathVariable("taskId") Long taskId)
    {
        try
        {
            // 根据任务ID同步统计功能待实现
            return success("根据任务ID同步统计功能待实现");
        }
        catch (Exception e)
        {
            return error("任务邮件统计数据同步失败: " + e.getMessage());
        }
    }

    /**
     * 获取邮件统计概览数据
     */
    @PreAuthorize("@ss.hasPermi('email:statistics:query')")
    @GetMapping("/overview")
    public AjaxResult getOverview()
    {
        try
        {
            // 这里可以添加统计概览的逻辑
            // 例如：总发送数、总送达数、总打开数、总回复数等
            return success("邮件统计概览数据");
        }
        catch (Exception e)
        {
            return error("获取邮件统计概览失败: " + e.getMessage());
        }
    }
}
