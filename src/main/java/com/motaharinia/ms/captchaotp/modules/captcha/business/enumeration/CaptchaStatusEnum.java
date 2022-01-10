package com.motaharinia.ms.captchaotp.modules.captcha.business.enumeration;

/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت وضعیت کپچا
 */
public enum CaptchaStatusEnum {

    /**
     *مقدار کد کپچا وارد شده معتبر است
     */
    VALID("VALID"),
    /**
     *مقدار کد کپچا وارد شده نامعتبر است
     */
    INVALID("INVALID"),
    /**
     * مقدار کد کپچا وارد شده به دلیل تلاش زیاد در نظر گرفته نمیشود
     */
    IGNORED("IGNORED"),

    ;

    private final String value;

    CaptchaStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}