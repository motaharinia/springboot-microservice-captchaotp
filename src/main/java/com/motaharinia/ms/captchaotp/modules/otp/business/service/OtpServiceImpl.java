package com.motaharinia.ms.captchaotp.modules.otp.business.service;


import com.motaharinia.ms.captchaotp.config.caching.CachingConfiguration;
import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
import com.motaharinia.ms.captchaotp.modules.otp.business.exception.OtpException;
import com.motaharinia.ms.captchaotp.modules.otp.presentation.OtpDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس پیاده سازی اینترفیس رمز یکبار مصرف
 */
@Slf4j
@Service
public class OtpServiceImpl implements OtpService {


    @Value("${app.captcha-code.allow-failed-retry}")
    private Integer allowFailedRetry;
    @Value("${app.captcha-code.ttl-in-seconds}")
    private Long ttlInSeconds;


    private final RedissonClient redissonClient;

    public OtpServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * نام سرویس خالی است
     */
    private static final String BUSINESS_EXCEPTION_SOURCE_PROJECT_IS_EMPTY = "BUSINESS_EXCEPTION.SOURCE_PROJECT_IS_EMPTY";
    /**
     * کلید خالی است
     */
    private static final String BUSINESS_EXCEPTION_KEY_IS_EMPTY = "BUSINESS_EXCEPTION.KEY_IS_EMPTY";
    /**
     * رمز یکبار مصرف خالی است
     */
    private static final String BUSINESS_EXCEPTION_VALUE_IS_EMPTY = "BUSINESS_EXCEPTION.VALUE_IS_EMPTY";
    /**
     * مقدار نامعتبر است
     */
    private static final String BUSINESS_EXCEPTION_VALUE_IS_INVALID = "BUSINESS_EXCEPTION.OTP_VALUE_IS_INVALID";
    /**
     * کلید به دلیل تلاش بیش از حد اشتباه در نظر گرفته نمیشود
     */
    private static final String BUSINESS_EXCEPTION_KEY_IS_IGNORED = "BUSINESS_EXCEPTION.KEY_IS_IGNORED";
    /**
     * کلید وجود ندارد
     */
    private static final String BUSINESS_EXCEPTION_KEY_NOT_FOUND = "BUSINESS_EXCEPTION.OTP_KEY_NOT_FOUND";
    /**
     * درخواست های شما بیش از حد مجاز است دقایقی بعد مجدد تلاش نمایید
     */
    private static final String BUSINESS_EXCEPTION_RATE_LIMIT_EXCEPTION_BAN = "BUSINESS_EXCEPTION.RATE_LIMIT_EXCEPTION_BAN";
    /**
     * کلید مورد نظر وجود دارد
     */
    private static final String BUSINESS_EXCEPTION_KEY_EXIST = "BUSINESS_EXCEPTION.KEY_EXIST";

    /**
     * متد تولیدکننده رمز یکبار مصرف بر اساس پروژه درخواست دهنده و کلید رمز دلخواه
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید کد otp
     * @param otpLength         طول otp
     * @param otpTtl            طول عمر otp
     * @return خروجی: مدل رمز یکبار مصرف
     */
//    @Cacheable(cacheManager = "CACHE_MANAGER" ,value = CachingConfiguration.REDIS_VALUE_OTP, key = "#key")
    @Override
    public OtpDto create(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull Integer otpLength, @NotNull Long otpTtl) {
        if (ObjectUtils.isEmpty(sourceProjectEnum)) {
            throw new OtpException(key, BUSINESS_EXCEPTION_SOURCE_PROJECT_IS_EMPTY, "");
        }
        if (key.isEmpty()) {
            throw new OtpException(key, BUSINESS_EXCEPTION_KEY_IS_EMPTY, "");
        }

        RBucket<OtpDto> otpModelRBucket = redissonClient.getBucket(CachingConfiguration.REDIS_VALUE_OTP + "_" + sourceProjectEnum + "_" + key);
        if (otpModelRBucket.isExists())
            throw new OtpException(key, BUSINESS_EXCEPTION_KEY_EXIST, "");

        OtpDto otpDto = new OtpDto();
        //ساخت کلید و مقدار
        otpDto.setSourceProjectEnum(sourceProjectEnum);
        otpDto.setKey(key);
        otpDto.setValue(StringTools.generateRandomString(RandomGenerationTypeEnum.NUMBER, otpLength, false));
        otpDto.setCreateAt(Instant.now().toEpochMilli());

        log.info("-----------------------------------------------------------------------------------------------------");
        log.info("create otpModel.getKey():" + otpDto.getKey() + " otpModel.getValue():" + otpDto.getValue());
        //ذخیره در کشینگ
        otpModelRBucket.set(otpDto);
        otpModelRBucket.expire(otpTtl, TimeUnit.SECONDS);

        return otpDto;
    }

    /**
     * متد بررسی رمز یکبار مصرف
     *
     * @param sourceProjectEnum پروژه درخواست دهنده
     * @param key               کلید رمز یکبار مصرف
     * @param value             رمز یکبار مصرف
     * @param methodName        نام متد
     * @param username          نام کاربری
     * @param tryCount          تعداد تلاش
     * @param tryTtlInMinutes   مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     * @param banTtlInMinutes   مدت زمان محدود شدن کاربر بلاک شده روی متد
     * @return خروجی: مدل رمز یکبار مصرف
     */
    @Override
    public void check(@NotNull SourceProjectEnum sourceProjectEnum, @NotNull String key, @NotNull String value, @NotNull String methodName, @NotNull String username, @NotNull Integer tryCount, @NotNull Integer tryTtlInMinutes, @NotNull Integer banTtlInMinutes) {
        if (ObjectUtils.isEmpty(sourceProjectEnum)) {
            throw new OtpException(key, BUSINESS_EXCEPTION_SOURCE_PROJECT_IS_EMPTY, "");
        }
        if (key.isEmpty()) {
            throw new OtpException(key, BUSINESS_EXCEPTION_KEY_IS_EMPTY, "");
        }
        if (value.isEmpty()) {
            throw new OtpException(key, BUSINESS_EXCEPTION_VALUE_IS_EMPTY, "");
        }

        //خواندن از کشینگ
        RBucket<OtpDto> otpCodeModelRBucket = redissonClient.getBucket(CachingConfiguration.REDIS_VALUE_OTP + "_" + sourceProjectEnum + "_" + key);
        OtpDto otpDto = otpCodeModelRBucket.get();
        if (ObjectUtils.isEmpty(otpDto)) {
            throw new OtpException(key, BUSINESS_EXCEPTION_KEY_NOT_FOUND , "");
        }

        String banKey = CachingConfiguration.REDIS_VALUE_OTP + "_" + methodName + "_" + username + "_BAN";
        String tryKey = CachingConfiguration.REDIS_VALUE_OTP + "_" + methodName + "_" + username + "_TRY";


        // اگر کاربر قبلا محدود شده است
        if (redissonClient.getKeys().countExists(banKey) > 0)
            throw new OtpException(username, BUSINESS_EXCEPTION_RATE_LIMIT_EXCEPTION_BAN, "");


        //بررسی مقدار رمز یکبار مصرف
        if (otpDto.getValue().equalsIgnoreCase(value)) {
            //مقدار کد رمز یکبار مصرف وارد شده معتبر است
            otpCodeModelRBucket.delete();
            if (redissonClient.getKeys().countExists(tryKey) > 0) {
                //اگر کلید تری وجود دارد باید حذف شود
                redissonClient.getBucket(tryKey).delete();
            }
            return;
        }


        RBucket<Integer> tryBucket = redissonClient.getBucket(tryKey);
        // اگر برای اولین بار رمز یکبار مصرف را اشتباه وارد کرده است
        if (redissonClient.getKeys().countExists(tryKey) == 0) {
            tryBucket.set(1, TimeUnit.MINUTES.toSeconds(tryTtlInMinutes) - 5, TimeUnit.SECONDS);
            throw new OtpException(value, BUSINESS_EXCEPTION_VALUE_IS_INVALID , "");
        }

        //بررسی تعداد دفعات مجاز
        int numberOfTries = tryBucket.get() + 1;
        if (numberOfTries > tryCount) {
            RBucket<Boolean> banBucket = redissonClient.getBucket(banKey);
            //در صورتی که بیش از تعداد دفعات مجاز باشد ، کاربر را محدود میکنیم
            banBucket.set(true, TimeUnit.MINUTES.toSeconds(banTtlInMinutes) - 5, TimeUnit.SECONDS);
            throw new OtpException(username, BUSINESS_EXCEPTION_RATE_LIMIT_EXCEPTION_BAN, "");
        } else {
            //افزایش تعداد دفعات  فراخوانی متد
            tryBucket.set(numberOfTries, redissonClient.getKeys().remainTimeToLive(tryKey), TimeUnit.MILLISECONDS);
        }

        //در نهایت در صورت درست نبودن مقدار باید اکسپشن داده شود
        throw new OtpException(value, BUSINESS_EXCEPTION_VALUE_IS_INVALID , "");


    }

}
