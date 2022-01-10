package com.motaharinia.ms.captchaotp.config.caching;


import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Slf4j
public class CachingConfiguration {

    @Value("${spring.application.name}")
    private String springApplicationName;
    @Value("${spring.redis.host}")
    private String springRedisHost;
    @Value("${spring.redis.port}")
    private String springRedisPort;
    @Value("${spring.redis.password}")
    private String springRedisPassword;

    public static final String CACHE_MANAGER ="CACHE_MANAGER";
    public static final String REDIS_VALUE_CAPTCHA_CODE="CAPTCHA";
    public static final String REDIS_VALUE_OTP="OTP";

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        //مبدل تبدیل باینری به جیسون
        Config config = new Config();
        final Codec codec = new JsonJacksonCodec(new RedisObjectMapper());
        //تنظیمات ردیسون
        if (ObjectUtils.isEmpty(springRedisPassword)) {
            config.setCodec(codec).useSingleServer()
                    .setClientName(springApplicationName)
                    .setAddress("redis://" + springRedisHost + ":" + springRedisPort);
        } else {
            config.setCodec(codec).useSingleServer()
                    .setClientName(springApplicationName)
                    .setAddress("redis://" + springRedisHost + ":" + springRedisPort)
                    .setPassword(springRedisPassword);
        }
        //ایجاد یک شی از ردیسون
        return Redisson.create(config);
    }

    @Bean
    public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
        return new RedissonConnectionFactory(redisson);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(new RedisObjectMapper());

        return RedisCacheConfiguration
                .defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(600))
//                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()) )
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer) )
                ;
    }


    @Bean(CACHE_MANAGER)
    public RedisCacheManager cacheManager(RedissonConnectionFactory redissonConnectionFactory) {
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        //تنظیم یک روز برای انقضای مقادیر ردیس
        configMap.put(REDIS_VALUE_CAPTCHA_CODE, cacheConfiguration().entryTtl(Duration.ofSeconds(TimeUnit.DAYS.toSeconds(1l))));
        configMap.put(REDIS_VALUE_OTP, cacheConfiguration().entryTtl(Duration.ofSeconds(TimeUnit.DAYS.toSeconds(1l))));
        //تنظیم مدیر کشینگ ردیس

        return RedisCacheManager
                .builder(redissonConnectionFactory)
                .cacheDefaults(cacheConfiguration())
                .transactionAware()
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}
