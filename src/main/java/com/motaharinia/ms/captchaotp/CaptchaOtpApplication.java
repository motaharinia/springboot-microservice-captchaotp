package com.motaharinia.ms.captchaotp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اصلی اجرای اپلیکیشن
 */
@SpringBootApplication(scanBasePackages = { "com.motaharinia"})
@ConfigurationPropertiesScan("com.motaharinia")
@EnableEurekaClient
public class CaptchaOtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptchaOtpApplication.class, args);
    }

}
