package com.motaharinia.ms.captchaotp.modules.captcha.presentation;

import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.ms.captchaotp.modules.captcha.business.enumeration.CaptchaStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل کد کپچا
 */

@Data
@NoArgsConstructor
public class CaptchaDto implements Serializable {

    /**
     * سرویس درخواست دهنده
     */
    private SourceProjectEnum sourceProjectEnum;
    /**
     * کلید کپچا
     */
    private String captchaKey;
    /**
     * مقدار کپچا
     */
    private String captchaValue;
    /**
     * تاریخ تولید
     */
    private Long captchaCreateAt;

    /**
     * تصویر کپچا
     */
    private byte[] captchaImageByteArray;
    /**
     * تعداد دفعات ورود اشتباه توسط کاربر
     */
    //private Integer failedRetryCount = 0;
    /**
     * تاریخ آخرین تلاش اشتباه
     */
    //private Long failedRetryLastDate;
    /**
     * وضعیت بررسی
     */
    private CaptchaStatusEnum statusEnum;


}
