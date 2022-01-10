package com.motaharinia.ms.captchaotp.modules.captcha.presentation.backcall;


import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
import com.motaharinia.ms.captchaotp.modules.captcha.business.service.CaptchaService;
import com.motaharinia.ms.captchaotp.modules.captcha.presentation.CaptchaDto;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1.0/back/captcha")
public class CaptchaBackController {

    private final CaptchaService captchaService;

    /**
     * پیام موفقیت فرم
     */
    private static final String FORM_SUBMIT_SUCCESS = "USER_MESSAGE.FORM_SUBMIT_SUCCESS";

    @Autowired
    public CaptchaBackController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * متد تولیدکننده کلید و مقدار کد کپچا (به همراه تصویر) بر اساس نام پروژه درخواست دهنده و کلید کپچا دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کپچا دلخواه
     * @param response          شیی پاسخ وب
     * @return خروجی: تصویر کپچا
     * @throws IOException         خطا
     * @throws FontFormatException خطا
     */
    @GetMapping(value = "/create/{sourceProjectEnum}/{key}/{captchaLength}/{captchaTtl}/")
    public ClientResponseDto<byte[]> create(@PathVariable("sourceProjectEnum") SourceProjectEnum sourceProjectEnum, @PathVariable("key") String key, @PathVariable("captchaLength") Integer captchaLength, @PathVariable(value = "captchaTtl") Long captchaTtl, HttpServletResponse response) throws IOException, FontFormatException {

        //در صورتی که سرویس کد کپچا نفرستاده است کد کپچا رندوم ایجاد میگردد
        if (ObjectUtils.isEmpty(key)) {
            key = StringTools.generateRandomString(RandomGenerationTypeEnum.NUMBER, 6, false);
        } else {
            key = URLDecoder.decode(key, StandardCharsets.UTF_8);
        }

        //تولید و کش کردن کد و مقدار کپچا
        CaptchaDto captchaDto = captchaService.create(sourceProjectEnum, key, captchaLength, captchaTtl);

        //برگرداندن شیی پاسخ وب با داده تصویر کد کپچا
        return new ClientResponseDto<>(captchaDto.getCaptchaImageByteArray(), FORM_SUBMIT_SUCCESS);
    }

    /**
     * متد بررسی کپچا
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param value             مقدار کد کپچا
     * @return Void
     */
    @GetMapping(value = "/check/{sourceProjectEnum}/{key}/{value}/{methodName}/{username}/{tryCount}/{tryTtlInMinutes}/{banTtlInMinutes}/")
    public ClientResponseDto<Void> check(@PathVariable("sourceProjectEnum") SourceProjectEnum sourceProjectEnum, @PathVariable("key") String key, @PathVariable("value") String value,
                                         @PathVariable("methodName") String methodName,
                                         @PathVariable("username") String username,
                                         @PathVariable("tryCount") Integer tryCount,
                                         @PathVariable("tryTtlInMinutes") Integer tryTtlInMinutes,
                                         @PathVariable("banTtlInMinutes") Integer banTtlInMinutes) {
        //بررسی مقدار کپچا
        captchaService.check(sourceProjectEnum, key, value, methodName, username, tryCount, tryTtlInMinutes, banTtlInMinutes);
        return new ClientResponseDto<>(null, FORM_SUBMIT_SUCCESS);
    }

}
