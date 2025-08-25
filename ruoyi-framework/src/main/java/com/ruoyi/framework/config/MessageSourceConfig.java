package com.ruoyi.framework.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import com.ruoyi.common.constant.Constants;

import java.util.Locale;

/**
 * 国际化消息源配置
 * 
 * @author ruoyi
 */
@Configuration
public class MessageSourceConfig
{
    /**
     * 配置国际化消息源
     */
    @Bean
    public MessageSource messageSource()
    {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // 设置国际化资源文件的基础名称
        messageSource.setBasename("i18n/messages");
        // 设置默认编码
        messageSource.setDefaultEncoding("UTF-8");
        // 设置缓存时间（秒）
        messageSource.setCacheSeconds(3600);
        // 设置是否使用消息代码作为默认消息
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * 配置语言解析器
     */
    @Bean
    public LocaleResolver localeResolver()
    {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        // 设置默认语言
        localeResolver.setDefaultLocale(Constants.DEFAULT_LOCALE);
        return localeResolver;
    }
}

