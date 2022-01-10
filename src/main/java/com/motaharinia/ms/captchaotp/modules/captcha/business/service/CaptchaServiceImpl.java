package com.motaharinia.ms.captchaotp.modules.captcha.business.service;


import com.motaharinia.ms.captchaotp.config.caching.CachingConfiguration;
import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.ms.captchaotp.modules.captcha.business.exception.CaptchaException;
import com.motaharinia.msutility.tools.captcha.CaptchaConfigDto;
import com.motaharinia.msutility.tools.captcha.CaptchaTools;
import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
import com.motaharinia.ms.captchaotp.modules.captcha.presentation.CaptchaDto;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی اینترفیس کپچا
 */
@Component
@Service
public class CaptchaServiceImpl implements CaptchaService {


    @Value("${app.captcha-code.allow-failed-retry}")
    private Integer allowFailedRetry;
    @Value("${app.captcha-code.ttl-in-seconds}")
    private Long ttlInSeconds;


    private final RedissonClient redissonClient;

    public CaptchaServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * نام سرویس کپچا کد خالی است
     */
    private static final String BUSINESS_EXCEPTION_SOURCE_PROJECT_IS_EMPTY = "BUSINESS_EXCEPTION.SOURCE_PROJECT_IS_EMPTY";
    /**
     * کلید کد کپچا خالی است
     */
    private static final String BUSINESS_EXCEPTION_KEY_IS_EMPTY = "BUSINESS_EXCEPTION.KEY_IS_EMPTY";
    /**
     * مقدار کد کپچا خالی است
     */
    private static final String BUSINESS_EXCEPTION_VALUE_IS_EMPTY = "BUSINESS_EXCEPTION.VALUE_IS_EMPTY";
    /**
     * مقدار کد کپچا نامعتبر است
     */
    private static final String BUSINESS_EXCEPTION_VALUE_IS_INVALID = "BUSINESS_EXCEPTION.CAPTCHA_VALUE_IS_INVALID";
    /**
     * کلید کد کپچا به دلیل تلاش بیش از حد اشتباه در نظر گرفته نمیشود
     */
    private static final String BUSINESS_EXCEPTION_KEY_IS_IGNORED = "BUSINESS_EXCEPTION.KEY_IS_IGNORED";
    /**
     * کلید کد کپچا برای بررسی وجود ندارد
     */
    private static final String BUSINESS_EXCEPTION_KEY_NOT_FOUND = "BUSINESS_EXCEPTION.CAPTCHA_KEY_NOT_FOUND";
    /**
     * درخواست های شما بیش از حد مجاز است دقایقی بعد مجدد تلاش نمایید
     */
    private static final String BUSINESS_EXCEPTION_RATE_LIMIT_EXCEPTION_BAN = "BUSINESS_EXCEPTION.RATE_LIMIT_EXCEPTION_BAN";


    /**
     * متد تولیدکننده کلید و مقدار کد کپچا (به همراه تصویر) بر اساس نام پروژه درخواست دهنده و کلید کپچا دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param captchaLength     طول کپچا
     * @param captchaTtl        طول عمر کپچا
     * @return خروجی: مدل کد کپچا
     * @throws IOException         خطا
     * @throws FontFormatException خطا
     */
//    @Cacheable(cacheManager = "CACHE_MANAGER" ,value = CachingConfiguration.REDIS_VALUE_CAPTCHA_CODE, key = "#key")
    @Override
    public CaptchaDto create(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer captchaLength, @NotNull Long captchaTtl) throws IOException, FontFormatException {
        if (ObjectUtils.isEmpty(sourceProjectEnum)) {
            throw new CaptchaException(key, BUSINESS_EXCEPTION_SOURCE_PROJECT_IS_EMPTY, "");
        }
        if (key.isEmpty()) {
            throw new CaptchaException(key, BUSINESS_EXCEPTION_KEY_IS_EMPTY, "");
        }

        CaptchaDto captchaDto = new CaptchaDto();
        //ساخت کلید و مقدار کپچا
        captchaDto.setSourceProjectEnum(sourceProjectEnum);
        captchaDto.setCaptchaKey(key);
        captchaDto.setCaptchaValue(StringTools.generateRandomString(RandomGenerationTypeEnum.LATIN_CHARACTERS_NUMBERS, captchaLength, false));
        captchaDto.setCaptchaCreateAt(Instant.now().toEpochMilli());

        //ذخیره در کشینگ
        RBucket<CaptchaDto> captchaCodeModelRBucket = redissonClient.getBucket(CachingConfiguration.REDIS_VALUE_CAPTCHA_CODE + "_" + sourceProjectEnum + "_" + key);
        captchaCodeModelRBucket.set(captchaDto);
        captchaCodeModelRBucket.expire(captchaTtl, TimeUnit.SECONDS);

        this.fillCaptchaImage(captchaDto);

        return captchaDto;
    }


    /**
     * این متد با دریافت مدل تصویر کپچا را تولید و در مدل ست میکند
     *
     * @param captchaDto مدل کد کپچا
     * @throws IOException         خطا
     * @throws FontFormatException خطا
     */
    private void fillCaptchaImage(@NotNull CaptchaDto captchaDto) throws IOException, FontFormatException {
        if (ObjectUtils.isEmpty(captchaDto.getCaptchaValue())) {
            throw new CaptchaException(captchaDto.getCaptchaKey(), BUSINESS_EXCEPTION_KEY_IS_EMPTY, "captchaCodeModel.getKey():" + captchaDto.getCaptchaKey());
        }
        //ساخت تصویر کپچا
        BufferedImage bufferedImage = CaptchaTools.generateCaptcha(new CaptchaConfigDto(), captchaDto.getCaptchaValue(), BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "JPG", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] captchaImageByteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        captchaDto.setCaptchaImageByteArray(captchaImageByteArray);
    }

    /**
     * متد بررسی کپچا
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد کپچا
     * @param value             مقدار کد کپچا
     * @param methodName        نام متد
     * @param username          نام کاربری
     * @param tryCount          تعداد تلاش
     * @param tryTtlInMinutes   مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    @Override
    public void check(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes) {
        if (ObjectUtils.isEmpty(sourceProjectEnum)) {
            throw new CaptchaException(key, BUSINESS_EXCEPTION_SOURCE_PROJECT_IS_EMPTY, "");
        }
        if (key.isEmpty()) {
            throw new CaptchaException(key, BUSINESS_EXCEPTION_KEY_IS_EMPTY, "");
        }
        if (value.isEmpty()) {
            throw new CaptchaException(key, BUSINESS_EXCEPTION_VALUE_IS_EMPTY, "");
        }

        //خواندن از کشینگ
        RBucket<CaptchaDto> captchaCodeModelRBucket = redissonClient.getBucket(CachingConfiguration.REDIS_VALUE_CAPTCHA_CODE + "_" + sourceProjectEnum + "_" + key);
        CaptchaDto captchaDto = captchaCodeModelRBucket.get();
        if (ObjectUtils.isEmpty(captchaDto)) {
            throw new CaptchaException(key, BUSINESS_EXCEPTION_KEY_NOT_FOUND , "");
        }

        String banKey = CachingConfiguration.REDIS_VALUE_CAPTCHA_CODE + "_" + methodName + "_" + username + "_BAN";
        String tryKey = CachingConfiguration.REDIS_VALUE_CAPTCHA_CODE + "_" + methodName + "_" + username + "_TRY";


        // اگر کاربر قبلا محدود شده است
        if (redissonClient.getKeys().countExists(banKey) > 0)
            throw new CaptchaException(username, BUSINESS_EXCEPTION_RATE_LIMIT_EXCEPTION_BAN, "");


        //بررسی مقدار کپچا
        if (captchaDto.getCaptchaValue().equalsIgnoreCase(value)) {
            //مقدار کد کپچا وارد شده معتبر است
            captchaCodeModelRBucket.delete();
            if (redissonClient.getKeys().countExists(tryKey) > 0) {
                //اگر کلید تری وجود دارد باید حذف شود
                redissonClient.getBucket(tryKey).delete();
            }
            return;
        }


        RBucket<Integer> tryBucket = redissonClient.getBucket(tryKey);
        // اگر برای اولین بار کپچا را اشتباه وارد کرده است
        if (redissonClient.getKeys().countExists(tryKey) == 0) {
            tryBucket.set(1, TimeUnit.MINUTES.toSeconds(tryTtlInMinutes) - 5, TimeUnit.SECONDS);
            throw new CaptchaException(value, BUSINESS_EXCEPTION_VALUE_IS_INVALID , "");
        }

        //بررسی تعداد دفعات مجاز
        int numberOfTries = tryBucket.get() + 1;
        if (numberOfTries > tryCount) {
            RBucket<Boolean> banBucket = redissonClient.getBucket(banKey);
            //در صورتی که بیش از تعداد دفعات مجاز باشد ، کاربر را محدود میکنیم
            banBucket.set(true, TimeUnit.MINUTES.toSeconds(banTtlInMinutes) - 5, TimeUnit.SECONDS);
            throw new CaptchaException(username, BUSINESS_EXCEPTION_RATE_LIMIT_EXCEPTION_BAN, "");
        } else {
            //افزایش تعداد دفعات  فراخوانی متد
            tryBucket.set(numberOfTries, redissonClient.getKeys().remainTimeToLive(tryKey), TimeUnit.MILLISECONDS);
        }

        //در نهایت در صورت درست نبودن مقدار باید اکسپشن داده شود
        throw new CaptchaException(value, BUSINESS_EXCEPTION_VALUE_IS_INVALID , "");

    }

}
