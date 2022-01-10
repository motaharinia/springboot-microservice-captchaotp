package com.motaharinia.ms.captchaotp.config.log;

import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.calendar.CalendarTools;
import com.motaharinia.msutility.tools.exception.ExceptionTools;
import com.motaharinia.msutility.tools.string.StringTools;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.motaharinia.ms.captchaotp.config.security.oauth2.dto.LoggedInUserDto;
import com.motaharinia.ms.captchaotp.config.security.oauth2.resource.ResourceTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 *کلاس مدیریت یکپارچه خطای دریافتی در rest یا graphql
 */
@Component
@Slf4j
public class ExceptionLogger {
    /**
     * شییی Environment
     */
    private final Environment environment;
    /**
     * مترجم پیامها
     */
    private final MessageSource messageSource;
    /**
     * کلاس مدیریت توکن ها در ResourceServer
     */
    private final ResourceTokenProvider resourceTokenProvider;


    private static final String SECURITY_EXCEPTION_401 = "SECURITY_EXCEPTION.401";
    private static final String SECURITY_EXCEPTION_403 = "SECURITY_EXCEPTION.403";

    public ExceptionLogger(Environment environment, MessageSource messageSource, ResourceTokenProvider resourceTokenProvider) {
        this.environment = environment;
        this.messageSource = messageSource;
        this.resourceTokenProvider = resourceTokenProvider;
    }

    /**
     * این متد خطای دریافتی در rest یا graphql را یکپارچه مدیریت میکند
     * @param exception خطا
     * @param httpServletRequest شیی درخواست وب
     * @param httpServletResponse شیی پاسخ وب
     * @return خروجی: پاسخ فرانت
     */
    public ClientResponseDto<String> handle(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        //مدیریت خطای 403 برای متدهای preAuthorize که به متد accessDeniedHandler تنظیمات ریسورس نمیرود
        if (exception instanceof AccessDeniedException) {
            securityException403(httpServletRequest,httpServletResponse,messageSource);
            return null;
        }

        //در صورتی که security فعال باشد تلاش میکنیم شناسه و کلمه کاربری شخص لاگین شده را در مدل ثبت کنیم
        Long userId = null;
        String username = null;
        Optional<LoggedInUserDto> loggedInUserDtoOptional = resourceTokenProvider.getLoggedInDto();
        if (loggedInUserDtoOptional.isPresent()) {
            userId = loggedInUserDtoOptional.get().getId();
            username = loggedInUserDtoOptional.get().getUsername();
        }

        //دریافت مدل خروجی خطا
        ClientResponseDto<String> clientResponseDto = ExceptionTools.doException(exception, httpServletRequest, httpServletResponse, environment.getProperty("spring.application.name"), Integer.parseInt(environment.getProperty("server.port")), messageSource, userId, username);

        //لاگ کردن خطا در elk
        log.error(clientResponseDto.getException().getMessageDtoList().get(0).getMessage(), kv("exceptionDto", clientResponseDto.getException()));

        //لاگ کرد شرح خطا در کنسول
        log.error("ResponseException: {} StackTrace:{}", clientResponseDto.getException().getMessage(), clientResponseDto.getException().getMessageDtoList().get(0).getStackTrace());

        //اگر خطای استانداری از ماکرو سرویس های دیگر داشتیم آن را به جای پیام خطای اصلی جایگزین میکنیم
        if(!ObjectUtils.isEmpty(clientResponseDto.getException().getExternalMessage())){
            clientResponseDto.setMessage(clientResponseDto.getException().getExternalMessage());
        }

        if (environment.getActiveProfiles().length > 0 && Arrays.stream(environment.getActiveProfiles()).anyMatch("prod"::equals)) {
            clientResponseDto.getException().getMessageDtoList().stream().forEach(item -> {
                item.setStackTrace("here is production");
                item.setStackTraceLine("here is production");
            });
        }

        return clientResponseDto;
    }


    /**
     * متد بررسی خطای 401 عدم احراز هویت امنیت
     * @param httpServletRequest شیی درخواست وب
     * @param httpServletResponse شیی پاسخ وب
     */
    public static void securityException401(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,MessageSource messageSource){
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", CalendarTools.getCurrentJalaliDateTimeString("/"));
        jsonObject.put("api_url", getFullURL(httpServletRequest));
        jsonObject.put("api_method", httpServletRequest.getMethod());
        jsonObject.put("message", StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_401));
        try {
            httpServletResponse.getWriter().write(jsonObject.toString());
        } catch (Exception getWriterException) {
            throw new RuntimeException(getWriterException.getMessage());
        }
    }

    /**
     * متد بررسی خطای 403 عدم دسترسی امنیت
     * @param httpServletRequest شیی درخواست وب
     * @param httpServletResponse شیی پاسخ وب
     */
    public static void securityException403(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse,MessageSource messageSource){
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("timestamp", CalendarTools.getCurrentJalaliDateTimeString("/"));
        jsonObject.put("api_url", getFullURL(httpServletRequest));
        jsonObject.put("api_method", httpServletRequest.getMethod());
        jsonObject.put("message", StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_403) );
        try {
            httpServletResponse.getWriter().write(jsonObject.toString());
        } catch (Exception getWriterException) {
            throw new RuntimeException(getWriterException.getMessage());
        }
    }

    /**
     * متد به دست آورنده url از روی شیی درخواست وب
     * @param httpServletRequest شیی درخواست وب
     * @return خروجی: مسیر
     */
    private static String getFullURL(HttpServletRequest httpServletRequest) {
        StringBuilder requestURL = new StringBuilder(httpServletRequest.getRequestURL().toString());
        String queryString = httpServletRequest.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }
}
