package com.motaharinia.ms.captchaotp.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * @author eng.motahari@gmail.com<br>
 *کلاس تنظیمات مستندسازی swagger با استاندارد OpenApi3
 */
//https://stackoverflow.com/questions/59898874/enable-authorize-button-in-springdoc-openapi-ui-for-bearer-token-authentication
@Configuration
@OpenAPIDefinition(info = @Info(title = "Motaharinia MS CAPTCHAOTP API", version = "v1.0"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
