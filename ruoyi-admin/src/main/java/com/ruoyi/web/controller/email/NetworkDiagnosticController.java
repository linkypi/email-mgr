package com.ruoyi.web.controller.email;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.utils.NetworkDiagnostic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 网络诊断控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/email/diagnostic")
public class NetworkDiagnosticController extends BaseController {
    
    @Autowired
    private NetworkDiagnostic networkDiagnostic;
    
    /**
     * 诊断邮件服务器连接
     */
    @PreAuthorize("@ss.hasPermi('email:diagnostic:test')")
    @Log(title = "网络诊断", businessType = BusinessType.OTHER)
    @PostMapping("/test")
    public AjaxResult testEmailServer(@RequestParam("host") String host, 
                                     @RequestParam("port") int port) {
        try {
            NetworkDiagnostic.NetworkDiagnosticResult result = 
                networkDiagnostic.diagnoseEmailServer(host, port);
            
            return success(result);
        } catch (Exception e) {
            return error("诊断失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取推荐的邮件服务器配置
     */
    @PreAuthorize("@ss.hasPermi('email:diagnostic:list')")
    @GetMapping("/recommended")
    public AjaxResult getRecommendedServers() {
        try {
            List<NetworkDiagnostic.EmailServerConfig> configs = 
                networkDiagnostic.getRecommendedEmailServers();
            
            return success(configs);
        } catch (Exception e) {
            return error("获取推荐配置失败：" + e.getMessage());
        }
    }
    
    /**
     * 快速诊断Gmail连接
     */
    @PreAuthorize("@ss.hasPermi('email:diagnostic:test')")
    @PostMapping("/gmail")
    public AjaxResult testGmailConnection() {
        try {
            // 测试Gmail的常用端口
            NetworkDiagnostic.NetworkDiagnosticResult result587 = 
                networkDiagnostic.diagnoseEmailServer("smtp.gmail.com", 587);
            
            NetworkDiagnostic.NetworkDiagnosticResult result465 = 
                networkDiagnostic.diagnoseEmailServer("smtp.gmail.com", 465);
            
            return success(new Object[]{
                new Object[]{"Gmail SMTP 587", result587},
                new Object[]{"Gmail SMTP 465", result465}
            });
        } catch (Exception e) {
            return error("Gmail连接诊断失败：" + e.getMessage());
        }
    }
}
