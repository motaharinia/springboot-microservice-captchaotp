package com.motaharinia.ms.captchaotp.config.log.rest;

import com.motaharinia.ms.captchaotp.config.log.ExceptionLogger;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس جمع کننده رویدادهای کنترلر<br>
 * این کلاس تمامی خطاهای صادر شده در سطح کنترلرها را میگیرد<br>
 * خطاها را لاگ میکند و یک مدل خروجی یونیک برای خطاها به سمت کلاینت ارسال میکند
 */

@ControllerAdvice
@Component
@Slf4j
public class RestExceptionTranslator {

    /**
     * شییی لاگ خطاها
     */
    private final ExceptionLogger exceptionLogger;

    public RestExceptionTranslator(ExceptionLogger exceptionLogger) {
        this.exceptionLogger = exceptionLogger;
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    ClientResponseDto<String> doException(Exception exception, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return exceptionLogger.handle(exception,httpServletRequest,httpServletResponse);
    }

}

