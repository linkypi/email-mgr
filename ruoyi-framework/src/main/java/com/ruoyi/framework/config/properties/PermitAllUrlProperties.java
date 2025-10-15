package com.ruoyi.framework.config.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RegExUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.ruoyi.common.annotation.Anonymous;

/**
 * 设置Anonymous注解允许匿名访问的url
 * 
 * @author ruoyi
 */
@Configuration
public class PermitAllUrlProperties implements InitializingBean, ApplicationContextAware
{
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    private ApplicationContext applicationContext;

    private List<String> urls = new ArrayList<>();

    public String ASTERISK = "*";

    @Override
    public void afterPropertiesSet()
    {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        System.out.println("=== 开始扫描@Anonymous注解的URL ===");
        map.keySet().forEach(info -> {
            HandlerMethod handlerMethod = map.get(info);

            // 获取方法上边的注解 替代path variable 为 *
            Anonymous method = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), Anonymous.class);
            Optional.ofNullable(method).ifPresent(anonymous -> {
                if (info.getPatternsCondition() != null && info.getPatternsCondition().getPatterns() != null) {
                    info.getPatternsCondition().getPatterns()
                        .forEach(url -> {
                            String processedUrl = RegExUtils.replaceAll(url, PATTERN, ASTERISK);
                            urls.add(processedUrl);
                            System.out.println("发现@Anonymous方法: " + handlerMethod.getMethod().getName() + " -> " + processedUrl);
                        });
                }
            });

            // 获取类上边的注解, 替代path variable 为 *
            Anonymous controller = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Anonymous.class);
            Optional.ofNullable(controller).ifPresent(anonymous -> {
                if (info.getPatternsCondition() != null && info.getPatternsCondition().getPatterns() != null) {
                    info.getPatternsCondition().getPatterns()
                        .forEach(url -> {
                            String processedUrl = RegExUtils.replaceAll(url, PATTERN, ASTERISK);
                            urls.add(processedUrl);
                            System.out.println("发现@Anonymous类: " + handlerMethod.getBeanType().getSimpleName() + " -> " + processedUrl);
                        });
                }
            });
        });
        System.out.println("=== 扫描完成，共发现 " + urls.size() + " 个匿名访问URL ===");
        urls.forEach(url -> System.out.println("匿名URL: " + url));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException
    {
        this.applicationContext = context;
    }

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }
}
