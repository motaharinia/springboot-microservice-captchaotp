package com.motaharinia.ms.captchaotp.config.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.motaharinia.msutility.custom.customjson.CustomObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * @author eng.motahari@gmail.com<br>
 *کلاس تنظیمات وب و عمومی
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(MvcConfiguration.class);

    /**
     * تنظیمات چندزبانی سامانه
     * @return خروجی: منبع ترجمه برای چندزبانی با تنظیمات سفارشی شده
     */
    @Bean(name = "messageSource")
    public MessageSource configureMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:lang/exception","classpath:lang/businessexception","classpath:lang/customvalidation","classpath:lang/usermessage","classpath:lang/comboitem","classpath:lang/notification");
        messageSource.setCacheSeconds(5);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new CustomObjectMapper( configureMessageSource());
    }


    /**
     * تنظیمات جکسون برای سریالایز و دیسریالایز سامانه
     * @param converters مبدل کاستوم مپر جکسون با تنظیمات سفارشی شده
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        logger.info("configureMessageConverters");
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper());
        converters.add(mappingJackson2HttpMessageConverter);
    }

    /**
     * تنظیم لوکیل پیش فرض سامانه
     * @return خروجی: ریزالور لوکال با لوکیل پیش فرض فارسی که بعد از اجرای نرم افزار به صورت پیش فرض روی فارسی تنظیم هست
     */
    @Bean
    @DependsOn("messageSource")
    public AcceptHeaderLocaleResolver localeResolver() {
        final AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(new Locale("fa", "IR"));
        return localeResolver;
    }
}
