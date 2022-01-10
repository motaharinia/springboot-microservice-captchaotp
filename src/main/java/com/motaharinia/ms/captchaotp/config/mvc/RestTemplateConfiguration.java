package com.motaharinia.ms.captchaotp.config.mvc;


import com.motaharinia.msutility.tools.security.SecurityTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس تنظیمات RestTemplate برای ارسال درخواست Rest به سرویسها
 */
@Slf4j
@Configuration
public class RestTemplateConfiguration {

    /**
     * حداکثر تعداد کانکشن های مجاز
     * The maximum number of connections allowed across all routes.
     */
    @Value("${app.external-call.max-total-connections}")
    private Integer maxTotalConnection;

    /**
     * زمان انتظار برای برقراری ارتباط به میلی ثانیه
     * Default value is = -1 It means system default
     */
    @Value("${app.external-call.connect-timeout}")
    private Integer connectTimeout;

    /**
     * زمان انتظار درخواست ارتباط به میلی ثانیه
     */
    @Value("${app.external-call.request-timeout}")
    private Integer requestTimeout;

    /**
     * زمان انتظار برای دریافت داده به میلی ثانیه
     * timeout for waiting for data or, put differently,
     * a maximum period inactivity between two consecutive data packets
     */
    @Value("${app.external-call.socket-timeout}")
    @NotNull
    private Integer socketTimeout;

    /**
     * تولید شیی test rest template برای استفاده در متدهای تست
     * @return خروجی: شیی test rest template برای استفاده در متدهای تست
     */
    @Bean
    public TestRestTemplate getTestRestTemplateForHttps() throws NoSuchAlgorithmException, KeyManagementException {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        HttpComponentsClientHttpRequestFactory requestFactory=getClientHttpRequestFactory();
        restTemplateBuilder.requestFactory(() -> requestFactory);
        return new TestRestTemplate(restTemplateBuilder);
    }

    /**
     * متد ساخت rest template با قابلیت skip کردن مسیرهای ssl برای فراخوانی بک تو بک مایکروسرویسهای داخلی
     *
     * @return خروجی: شیی resttemplate
     * @throws KeyManagementException   خطا
     * @throws NoSuchAlgorithmException خطا
     */
    @Primary
    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplateForMicroserviceCall() throws KeyManagementException, NoSuchAlgorithmException {
        return getRestTemplate();
    }

    /**
     * متد ساخت rest template با قابلیت skip کردن مسیرهای ssl
     *
     * @return خروجی: شیی resttemplate
     * @throws NoSuchAlgorithmException خطا
     * @throws KeyManagementException   خطا
     */

    @Bean("external")
    public RestTemplate getRestTemplateForExternalCall() throws NoSuchAlgorithmException, KeyManagementException {
        return getRestTemplate();
    }


    /**
     * نا متد ایجاد شیی RestTemplate با تنظیمات و بدون بروز خطای ssl نامعتبر
     * @return خروجی: شیی RestTemplate با تنظیمات و بدون بروز خطای ssl نامعتبر
     * @throws KeyManagementException خطا
     * @throws NoSuchAlgorithmException خطا
     */
    @NotNull
    private RestTemplate getRestTemplate() throws KeyManagementException, NoSuchAlgorithmException {
        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        return restTemplate;
    }

    /**
     * متد ایجاد شیی کارخانه ClientHttpRequest
     * @return خروجی: شیی کارخانه ClientHttpRequest
     * @throws NoSuchAlgorithmException خطا
     * @throws KeyManagementException خطا
     */
    @NotNull
    private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() throws NoSuchAlgorithmException, KeyManagementException {
        /*
         * Create an HttpClient that uses the custom SSLContext and do not verify cert hostname
         */
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(getRequestConfig())
                .setConnectionManager(getConnectionPoolManager())
                .setSSLContext(SecurityTools.getUntrustedSSLContext("TLSv1.2"))
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();


        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setBufferRequestBody(false); // When sending large amounts of data via POST or PUT, it is recommended to change this property to false, so as not to run out of memory.
        requestFactory.setReadTimeout(socketTimeout);
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

    /**
     * متد ایجاد شیی تنظیمات درخواست وب
     * @return خروجی: شیی تنظیمات درخواست وب
     */
    @NotNull
    public RequestConfig getRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(requestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout).build();
    }

    /**
     * متد سازنده شیی نگهدارنده استخر کانکشن ها
     * maintains a pool of HttpClientConnections .
     *
     * @return خروجی: شیی نگهدارنده استخر کانکشن ها
     */
    @Bean
    public PoolingHttpClientConnectionManager getConnectionPoolManager() {
        var builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Connection Pool Manager Initialisation failure because of " + e.getMessage(), e);
        }
        SSLConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }
        assert sslsf != null;
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        var poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(maxTotalConnection);
        return poolingConnectionManager;
    }
}
