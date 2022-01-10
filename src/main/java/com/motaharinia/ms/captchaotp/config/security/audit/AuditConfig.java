package com.motaharinia.ms.captchaotp.config.security.audit;


import com.motaharinia.ms.captchaotp.config.security.oauth2.resource.ResourceTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس تنظیمات بازرسی انتیتی ها
 */
@Configuration
public class AuditConfig {

    /**
     * کلاس مدیریت توکن ها در ResourceServer
     */
    private final ResourceTokenProvider resourceTokenProvider;

    public AuditConfig(ResourceTokenProvider resourceTokenProvider) {
        this.resourceTokenProvider = resourceTokenProvider;
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new CustomAuditAware(resourceTokenProvider);
    }
}
