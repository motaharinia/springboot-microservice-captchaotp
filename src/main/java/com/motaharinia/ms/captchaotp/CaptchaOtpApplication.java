package com.motaharinia.ms.captchaotp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس اصلی اجرای اپلیکیشن
 */
@SpringBootApplication(scanBasePackages = { "com.motaharinia"})
@ConfigurationPropertiesScan("com.motaharinia")
@EnableEurekaClient
@EnableFeignClients
public class CaptchaOtpApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptchaOtpApplication.class, args);
    }

}
