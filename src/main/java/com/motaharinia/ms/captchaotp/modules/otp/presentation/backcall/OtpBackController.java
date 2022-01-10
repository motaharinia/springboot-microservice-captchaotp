package com.motaharinia.ms.captchaotp.modules.otp.presentation.backcall;


import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.ms.captchaotp.modules.otp.business.service.OtpService;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
import com.motaharinia.ms.captchaotp.modules.otp.presentation.OtpDto;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * کلاس کنترلر رمز یکبار مصرف
 */
@RestController
@RequestMapping("/api/v1.0/back/otp")
public class OtpBackController {

    private final OtpService otpService;

    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    @Autowired
    public OtpBackController(OtpService otpService) {
        this.otpService = otpService;
    }

    /**
     * متد تولیدکننده رمز یکبار مصرف بر اساس نام سرویس درخواست دهنده و کلید رمز دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید رمز یکبار مصرف
     * @return خروجی: مدل رمز یکبار مصرف
     */
    @GetMapping(value = "/create/{sourceProjectEnum}/{key}/{otpLength}/{otpTtl}/")
    public ClientResponseDto<OtpDto> create(@PathVariable("sourceProjectEnum") SourceProjectEnum sourceProjectEnum, @PathVariable("key") String key, @PathVariable("otpLength") Integer otpLength, @PathVariable(value = "otpTtl") Long otpTtl) {

        //در صورتی که سرویس کد نفرستاده است کد رندوم ایجاد میگردد
        if (ObjectUtils.isEmpty(key)) {
            key = StringTools.generateRandomString(RandomGenerationTypeEnum.NUMBER, otpLength, false);
        } else {
            key = URLDecoder.decode(key, StandardCharsets.UTF_8);
        }

        return new ClientResponseDto<>(otpService.create(sourceProjectEnum, key, otpLength,otpTtl), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد بررسی رمز یکبار مصرف
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید رمز یکبار مصرف
     * @param value             رمز یکبار مصرف
     * @return خروجی: رمز یکبار مصرف
     */
    @GetMapping(value = "/check/{sourceProjectEnum}/{key}/{value}/{methodName}/{username}/{tryCount}/{tryTtlInMinutes}/{banTtlInMinutes}/")
    public ClientResponseDto<Void> check(@PathVariable("sourceProjectEnum") SourceProjectEnum sourceProjectEnum, @PathVariable("key") String key, @PathVariable("value") String value,
                                           @PathVariable("methodName") String methodName,
                                           @PathVariable("username") String username,
                                           @PathVariable("tryCount") Integer tryCount,
                                           @PathVariable("tryTtlInMinutes") Integer tryTtlInMinutes,
                                           @PathVariable("banTtlInMinutes") Integer banTtlInMinutes) {
        //بررسی رمز یکبار مصرف
        otpService.check(sourceProjectEnum, key, value, methodName, username, tryCount, tryTtlInMinutes, banTtlInMinutes);
        return new ClientResponseDto<>(null, FORM_SUBMIT_SUCCESS);
    }

}
