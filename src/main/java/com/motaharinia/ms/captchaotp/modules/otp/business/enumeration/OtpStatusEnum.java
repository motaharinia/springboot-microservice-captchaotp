package com.motaharinia.ms.captchaotp.modules.otp.business.enumeration;

/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت وضعیت رمز یکبار مصرف
 */
public enum OtpStatusEnum {

    /**
     *رمز یکبار مصرف وارد شده معتبر است
     */
    VALID("VALID"),
    /**
     *رمز یکبار مصرف وارد شده نامعتبر است
     */
    INVALID("INVALID"),
    /**
     * رمز یکبار مصرف وارد شده به دلیل تلاش زیاد در نظر گرفته نمیشود
     */
    IGNORED("IGNORED"),

    ;

    private final String value;

    OtpStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}