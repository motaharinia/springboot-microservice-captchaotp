package com.motaharinia.ms.captchaotp.config.security.oauth2.resource;

import com.motaharinia.ms.captchaotp.config.security.oauth2.resource.deserializer.CustomJacksonDeserializer;
import com.motaharinia.ms.captchaotp.config.security.oauth2.resource.exception.TokenException;
import com.motaharinia.msutility.custom.customjson.CustomObjectMapper;
import com.motaharinia.msutility.tools.string.StringTools;
import io.jsonwebtoken.*;
import io.jsonwebtoken.lang.Maps;
import com.motaharinia.ms.captchaotp.config.security.oauth2.dto.LoggedInUserDto;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * کلاس مدیریت توکن ها در ResourceServer
 * <p>
 * Reserved Claims:
 * iss – Issuer,
 * sub – Subject,
 * aud – Audience,
 * exp – Expiration,
 * nbf – Not Before,
 * iat – Issued At,
 * jti – JWT ID
 * <p>
 * https://svlada.com/jwt-token-authentication-with-spring-boot/
 * https://www.appsdeveloperblog.com/add-and-validate-custom-claims-in-jwt/
 * https://github.com/jwtk/jjwt/issues/131
 */
@Component
@Slf4j
public class ResourceTokenProvider {
    /**
     * فیلد کاستوم توکن : مدل کاربر امنیت
     */
    public static final String CUSTOM_CLAIM_LOGGED_IN_USER = "logged_in_user";

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_PARAM = "access_token";

    /**
     * رشته کلید عمومی که با دستور keytool از فایل jks قبلا دریافت شده است
     */
    @Value("${app.security.public-key}")
    private String securityPublicKey;
    /**
     * کلید عمومی که با آن توکنها رمز گشایی میشوند
     */
    private static PublicKey publicKey;



    private static final String SECURITY_EXCEPTION_TOKEN_INVALID_SIGNATURE = "SECURITY_EXCEPTION.TOKEN_INVALID_SIGNATURE";
    private static final String SECURITY_EXCEPTION_TOKEN_EXPIRED = "SECURITY_EXCEPTION.TOKEN_EXPIRED";
    private static final String SECURITY_EXCEPTION_TOKEN_UNSUPPORTED = "SECURITY_EXCEPTION.TOKEN_UNSUPPORTED";
    private static final String SECURITY_EXCEPTION_TOKEN_COMPACT_OF_HANDLER_INVALID = "SECURITY_EXCEPTION.TOKEN_COMPACT_OF_HANDLER_INVALID";
    private static final String SECURITY_EXCEPTION_TOKEN_DECODING_FAILED = "SECURITY_EXCEPTION.TOKEN_DECODING_FAILED";
    private static final String SECURITY_EXCEPTION_TOKEN_UNKNOWN_ERROR = "SECURITY_EXCEPTION.TOKEN_UNKNOWN_ERROR";


    private final MessageSource messageSource;

    public ResourceTokenProvider(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    /**
     * رشته کلید عمومی را از propertice میخواند و شی publicKey را تولید میکند
     *
     * @return
     */
    private PublicKey getPublicKey() {
        //اگر کلید عمومی از قبل ایجاد نشده باشد آن را یکبار ایجاد میکنیم
        if (publicKey == null) {
            try {
                String pem = securityPublicKey.replaceAll("-----BEGIN PUBLIC KEY-----", "");
                pem = pem.replaceAll("-----END PUBLIC KEY-----", "");
                pem = pem.replaceAll("\r\n", "");
                pem = pem.replaceAll("\n", "");
                pem = pem.trim();
                byte[] der = Base64.getDecoder().decode(pem);
                X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(spec);
            } catch (Exception exception) {
                log.error("Exception: {}", exception);
            }
            return publicKey;
        } else {
            return publicKey;
        }
    }


    /**
     * این متد توکن را به شیی احراز هویت تبدیل میکند
     *
     * @param token توکن
     * @return خروجی: شیی احراز هویت
     */
    public Authentication getAuthentication(String token) {
        //به دست آوردن Claim های توکن
        Claims claims = getClaims(token, true);

        Optional<LoggedInUserDto> loggedInUserDtoOptional = Optional.ofNullable(claims.get(CUSTOM_CLAIM_LOGGED_IN_USER, LoggedInUserDto.class));
        if(loggedInUserDtoOptional.isEmpty()){
            throw new RuntimeException("LoggedInUserDto is null.");
        }

        LoggedInUserDto loggedInUserDto = loggedInUserDtoOptional.get();

        Set<String> authoritySet = new HashSet<>();
        authoritySet.addAll(loggedInUserDto.getSecurityRoleSet());
        authoritySet.addAll(loggedInUserDto.getSecurityPermissionSet());


        Collection<? extends GrantedAuthority> authorities = new HashSet<>();
        if(!CollectionUtils.isEmpty(authoritySet)){
            authorities = authoritySet.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        }
        return new UsernamePasswordAuthenticationToken(loggedInUserDto, token, authorities);
    }


    /**
     * متد بررسی صحت توکن
     *
     * @param token توکن
     * @return خروجی: پاسخ صحت بررسی
     */
    public boolean isValidToken(String token) {
        try {
            Claims claims = getClaims(token, true);
            //بررسی وجود توکن در دیتابیس که فعلا بعلت بالا بردن پرفورمنس و عدم بکارگیری دیتابیس این کد کامنت شد
//          final Object tokenId = claims.get(CUSTOM_CLAIM_TOKEN_ID);
//          return accessTokenService.getByToken((String)tokenId).isPresent();
            log.info("getIssuedAt : {}  getExpiration: {}",claims.getIssuedAt(),claims.getExpiration());
            return true;
        } catch (Exception exception) {
            log.error("isValidToken exception: {}", exception);
            return false;
        }
    }


    /**
     * متد استخراج فیلدهای کاستوم توکن
     *
     * @param accessToken توکن دسترسی
     * @return خروجی: فیلدهای کاستوم توکن
     */
    public Claims getClaims(String accessToken, boolean checkExpiration) {
        try {

            //به دست آوردن Claim های توکن
            Claims claims;
            if (checkExpiration)
                   claims = Jwts.parserBuilder()
                    //تنظیم مبدل جیسون
//                    .deserializeJsonWith(new JacksonDeserializer(Maps.of(CUSTOM_CLAIM_LOGGED_IN_USER, LoggedInUserDto.class).build()))
//                    .deserializeJsonWith(new JacksonDeserializer(new CustomObjectMapper))
                    .deserializeJsonWith(new CustomJacksonDeserializer(Maps.of(CUSTOM_CLAIM_LOGGED_IN_USER, LoggedInUserDto.class).build(),new CustomObjectMapper()))
                    //تنظیم نحوه رمزگشایی با کلید خصوصی
                    .setSigningKey(getPublicKey())
                    //ساخت پارسر
                    .build()
                    //پارس کردن توکن
                    .parseClaimsJws(accessToken)
                    //دریافت بدنه فیلدهای خاص توکن
                    .getBody();
            else
                claims = Jwts.parserBuilder()
                        //تنظیم مبدل جیسون
//                    .deserializeJsonWith(new JacksonDeserializer(Maps.of(CUSTOM_CLAIM_LOGGED_IN_USER, LoggedInUserDto.class).build()))
//                    .deserializeJsonWith(new JacksonDeserializer(new CustomObjectMapper))
                        .deserializeJsonWith(new CustomJacksonDeserializer(Maps.of(CUSTOM_CLAIM_LOGGED_IN_USER, LoggedInUserDto.class).build(),new CustomObjectMapper()))
                        //تنظیم نحوه رمزگشایی با کلید خصوصی
                        .setSigningKey(getPublicKey())
                        .setAllowedClockSkewSeconds(TimeUnit.DAYS.toSeconds(365L))
                        //ساخت پارسر
                        .build()
                        //پارس کردن توکن
                        .parseClaimsJws(accessToken)
                        //دریافت بدنه فیلدهای خاص توکن
                        .getBody();
            return claims;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("", e);
            throw new TokenException(accessToken, StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_TOKEN_INVALID_SIGNATURE) + ":" + e.getMessage(), e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new TokenException(accessToken, StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_TOKEN_EXPIRED), e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new TokenException(accessToken, StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_TOKEN_UNSUPPORTED), e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new TokenException(accessToken, StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_TOKEN_COMPACT_OF_HANDLER_INVALID), e.getMessage());
        } catch (io.jsonwebtoken.io.DecodingException e) {
            throw new TokenException(accessToken, StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_TOKEN_DECODING_FAILED), e.getMessage());
        } catch (Exception e) {
            throw new TokenException(accessToken, StringTools.translateCustomMessage(messageSource,SECURITY_EXCEPTION_TOKEN_UNKNOWN_ERROR), e.getMessage());
        }
    }


    /**
     * متد به دست آورنده مدل کاربر امنیت از کانتکست امنیت
     *
     * @return خروجی: مدل کاربر لاگین شده
     */
    @NotNull
    public Optional<LoggedInUserDto> getLoggedInDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getPrincipal() != null) {
            LoggedInUserDto loggedInUserUserDto = (LoggedInUserDto) authentication.getPrincipal();
            return Optional.of(loggedInUserUserDto);
        } else {
            return Optional.empty();
        }
    }

    /**
     * متد به دست آورنده دسترسی ها و نقش های کاربری کاربر امنیت از کانتکست امنیت
     *
     * @return خروجی: ست دسترسی های کاربر لاگین شده
     */
    @NotNull
    public Optional<Set<String>> getLoggedInAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.getAuthorities() != null) {
            return Optional.of(authentication.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority()).collect(Collectors.toSet()));
        } else {
            return Optional.empty();
        }
    }


    public String resolveAccessToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        String accessToken = httpServletRequest.getParameter(AUTHORIZATION_PARAM);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        if (!ObjectUtils.isEmpty(accessToken))
            return accessToken;
        return null;
    }

    public String resolveBasic(HttpServletRequest httpServletRequest){
        String basic = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(basic) && basic.startsWith("Basic ")) {
            return new String(Base64.getDecoder().decode(basic.substring(6)), StandardCharsets.UTF_8);
        }
        return null;
    }
}
