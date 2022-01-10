package com.motaharinia.ms.captchaotp.modules.otp.business.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا رمز یکبار مصرف
 */
public class OtpException extends BusinessException {

    public OtpException(@NotNull String exceptionClassId, @NotNull String exceptionEnumString, @NotNull String exceptionDescription) {
        super(OtpException.class, exceptionClassId, exceptionEnumString, exceptionDescription);
    }
}
