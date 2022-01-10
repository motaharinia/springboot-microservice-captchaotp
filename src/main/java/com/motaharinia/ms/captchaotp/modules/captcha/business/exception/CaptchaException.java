package com.motaharinia.ms.captchaotp.modules.captcha.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا کپچا
 */
public class CaptchaException extends BusinessException {

    public CaptchaException(@NotNull String exceptionClassId, @NotNull String exceptionEnumString, @NotNull String exceptionDescription) {
        super(CaptchaException.class, exceptionClassId, exceptionEnumString, exceptionDescription);
    }
}
