package com.motaharinia.ms.captchaotp.config.mvc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مانیتورینگ درخواستهای ارسالی از طریق RestTemplate
 */

@Slf4j
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        List<String> fixedContentTypeList = new ArrayList<>();
//        log.info("RestTemplateInterceptor.intercept [before] Content-TypeSize:{} Content-Type:{}", response.getHeaders().get("Content-Type").size(),response.getHeaders().get("Content-Type"));
        List<String> contentTypeList = response.getHeaders().get("Content-Type");
        if (contentTypeList != null) {
            boolean hasProblem = false;
            for (String contentType : contentTypeList) {
//                log.info("RestTemplateInterceptor.intercept contentType:{} ", contentType);
                if (contentType.contains(",")) {
                    hasProblem = true;
                    Arrays.stream(contentType.split(",")).forEach(item -> {
                        fixedContentTypeList.add(item.trim());
                    });
                } else {
                    fixedContentTypeList.add(contentType);
                }
            }
            if (hasProblem) {
                response.getHeaders().remove("Content-Type");
                response.getHeaders().addAll("Content-Type", fixedContentTypeList);
//                response.getHeaders().forEach((key, value) -> log.info("RestTemplateInterceptor.intercept response.getHeaders() keyValue.getKey():{} keyValue.getValue():{}", key, value));
            }
        }
//        log.info("RestTemplateInterceptor.intercept [after] Content-TypeSize:{} Content-Type:{}", response.getHeaders().get("Content-Type").size(),response.getHeaders().get("Content-Type"));
        return response;
    }
}