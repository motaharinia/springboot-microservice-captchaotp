package com.motaharinia.ms.captchaotp.config.security.oauth2.resource.exception;

import com.motaharinia.msutility.custom.customexception.business.BusinessException;
import org.jetbrains.annotations.NotNull;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس خطا توکن
 */
public class TokenException extends BusinessException {

    public TokenException(@NotNull String exceptionClassId, @NotNull String securityUserExceptionEnum, @NotNull String exceptionDescription) {
        super(TokenException.class, exceptionClassId, securityUserExceptionEnum, exceptionDescription);
    }
}
