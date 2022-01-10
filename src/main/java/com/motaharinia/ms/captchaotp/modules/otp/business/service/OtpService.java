package com.motaharinia.ms.captchaotp.modules.otp.business.service;


import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.ms.captchaotp.modules.otp.presentation.OtpDto;

import javax.validation.constraints.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * اینترفیس رمز یکبار مصرف
 */
public interface OtpService {

    /**
     * متد تولیدکننده رمز یکبار مصرف بر اساس نام سرویس درخواست دهنده و کلید رمز دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key  کلید کد otp
     * @param otpLength   طول otp
     * @param otpTtl   طول عمر otp
     * @return خروجی: مدل رمز یکبار مصرف
     */
    OtpDto create(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer otpLength, @NotNull Long otpTtl);

    /**
     *  متد بررسی رمز یکبار مصرف
     *
     * @param sourceProjectEnum     پروژه درخواست دهنده
     * @param key     کلید رمز یکبار مصرف
     * @param value   رمز یکبار مصرف
     * @param methodName   نام متد
     * @param username   نام کاربری
     * @param tryCount    تعداد تلاش
     * @param tryTtlInMinutes    مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     * @return خروجی: مدل رمز یکبار مصرف
     */
    void check(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes);

}
