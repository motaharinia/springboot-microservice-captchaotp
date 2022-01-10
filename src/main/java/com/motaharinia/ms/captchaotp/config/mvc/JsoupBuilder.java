package com.motaharinia.ms.captchaotp.config.mvc;
import com.motaharinia.msutility.tools.security.SecurityTools;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * @author eng.motahari@gmail.com<br>
 *کلاس سازنده کانکشن JSoup برای خواندن محتوای Html صفحات وب
 */

@Component
public class JsoupBuilder {

    /**
     * زمان انتظار برای برقراری ارتباط به میلی ثانیه
     * Default value is = -1 It means system default
     */
    @Value("${app.external-call.connect-timeout}")
    private Integer connectTimeout;


    /**
     * متد تولید کننده کانکشن برای استفاده از Jsoup در پروژه
     *
     * @param url مسیر مورد نظر
     * @return خروجی: کانکشن Jsoup جهت استفاده
     * @throws KeyManagementException   خطا
     * @throws NoSuchAlgorithmException خطا
     */
    @NotNull
    public Connection getConnection(@NotNull String url) throws KeyManagementException, NoSuchAlgorithmException {
        Connection connection = Jsoup.connect(url);
        connection.userAgent("Mozilla");
        connection.timeout(connectTimeout);
        connection.sslSocketFactory(SecurityTools.getUntrustedSSLContext("").getSocketFactory());
//        connection.cookie("cookiename", "val234");
//        connection.referrer("http://google.com");
//        connection.header("headersecurity", "xyz123");
        return connection;
    }
}
