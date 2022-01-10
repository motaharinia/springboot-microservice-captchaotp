package com.motaharinia.ms.captchaotp.modules.captcha.business.service;


import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.ms.captchaotp.modules.captcha.presentation.CaptchaDto;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.io.IOException;

/**
 * @author eng.motahari@gmail.com<br>
 * اینترفیس کپچا
 */
public interface CaptchaService {

    /**
     * متد تولیدکننده کلید و مقدار کد کپچا (به همراه تصویر) بر اساس نام پروژه درخواست دهنده و کلید کپچا دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key  کلید کد کپچا
     * @param captchaLength   طول کپچا
     * @param captchaTtl   طول عمر کپچا
     * @return خروجی: مدل کد کپچا
     * @throws IOException         خطا
     * @throws FontFormatException خطا
     */
    CaptchaDto create(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer captchaLength, @NotNull Long captchaTtl) throws IOException, FontFormatException;

    /**
     * متد بررسی کپچا
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param value             مقدار کد کپچا
     * @param methodName   نام متد
     * @param username   نام کاربری
     * @param tryCount    تعداد تلاش
     * @param tryTtlInMinutes    مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    void check(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes);

}
