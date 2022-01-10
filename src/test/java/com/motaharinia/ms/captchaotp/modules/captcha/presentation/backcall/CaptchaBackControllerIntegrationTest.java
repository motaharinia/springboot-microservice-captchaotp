package com.motaharinia.ms.captchaotp.modules.captcha.presentation.backcall;

import com.motaharinia.ms.captchaotp.config.sourceproject.SourceProjectEnum;
import com.motaharinia.msutility.custom.customdto.ClientResponseDto;
import com.motaharinia.msutility.tools.string.RandomGenerationTypeEnum;
import com.motaharinia.msutility.tools.string.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class CaptchaBackControllerIntegrationTest {

    @LocalServerPort
    private Integer PORT;

    private String MODULE_API;

    private HttpHeaders headers;

    @Autowired
    private TestRestTemplate testRestTemplate;
    /**
     * طول کپچا
     */
    private final Integer captchaLength = 6;
    /**
     * طول عمر کپچا
     */
    private final Long captchaTtl = 600L;
    /**
     * نام متد
     */
    private final String methodName = "create";
    /**
     * نام کاربری
     */
    private final String username = "0083419004";
    /**
     * تعداد تلاش
     */
    private final Integer tryCount = 3;
    /**
     * مدت زمان فاصله ی بین هر تلاش برای فراخوانی هر متد
     */
    private final Integer tryTtlInMinutes = 5;
    /**
     * مدت زمان محدود شدن کاربر بلاک شده روی متد
     */
    private final Integer banTtlInMinutes = 5;

    @BeforeAll
    void beforeAll() {
        //تنظیم زبان لوکیل پروژه روی پارسی
        Locale.setDefault(new Locale("fa", "IR"));
        //ساخت هدر درخواست
        this.headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        this.MODULE_API = "http://localhost:" + PORT + "/api/v1.0/back/captcha";

    }


    /**
     * این متد مقادیر پیش فرض قبل از هر تست این کلاس تست را مقداردهی اولیه میکند
     */
    @BeforeEach
    void beforeEach() {
    }


    /**
     * تست تولید و بررسی کپچا
     */
    @Test
    @Order(1)
    void createAndCheckTest() {
        String randomKey = StringTools.generateRandomString(RandomGenerationTypeEnum.NUMBER, 6, false);
        ResponseEntity<ClientResponseDto<byte[]>> responseCreate = this.testRestTemplate.exchange(this.MODULE_API + "/create/" + SourceProjectEnum.MS_IAM.getValue() + "/" + randomKey + "/" + captchaLength + "/" + captchaTtl + "/", HttpMethod.GET, new HttpEntity<>(this.headers), new ParameterizedTypeReference<>() {
        });
        //بررسی های تست
        assertThat(responseCreate).isNotNull();
        assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseCreate.getBody()).isNotNull();
        System.out.println(responseCreate.getBody());

        ResponseEntity<ClientResponseDto<Void>> responseCheck = this.testRestTemplate.exchange(this.MODULE_API + "/check/" + SourceProjectEnum.MS_IAM.getValue() + "/" + randomKey + "/invalidvalue/" + methodName + "/" + username + "/" + tryCount + "/" + tryTtlInMinutes + "/" + banTtlInMinutes + "/", HttpMethod.GET, new HttpEntity<>(this.headers), new ParameterizedTypeReference<>() {
        });

        //بررسی های تست غلط بودن کپچا
        assertThat(responseCheck.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     * تست تولید و بررسی کپچا
     */
    @Test
    @Order(2)
    void createAndCheckTryCountTest() {
        String randomKey = StringTools.generateRandomString(RandomGenerationTypeEnum.NUMBER, 6, false);
        ResponseEntity<ClientResponseDto<byte[]>> responseCreate = this.testRestTemplate.exchange(this.MODULE_API + "/create/" + SourceProjectEnum.MS_IAM.getValue() + "/" + randomKey + "/" + captchaLength + "/" + captchaTtl + "/", HttpMethod.GET, new HttpEntity<>(this.headers), new ParameterizedTypeReference<>() {});
        //بررسی های تست
        assertThat(responseCreate).isNotNull();
        assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseCreate.getBody()).isNotNull();
        System.out.println(responseCreate.getBody());

        ResponseEntity<ClientResponseDto<Void>> responseCheck = null;
        for (int i = 0; i <= tryCount; i++) {
            responseCheck = this.testRestTemplate.exchange(this.MODULE_API + "/check/" + SourceProjectEnum.MS_IAM.getValue() + "/" + randomKey + "/invalidvalue/" + methodName + "/" + username + "/" + tryCount + "/" + tryTtlInMinutes + "/" + banTtlInMinutes + "/", HttpMethod.GET, new HttpEntity<>(this.headers), new ParameterizedTypeReference<>() {
            });
        }

        //بررسی های تست غلط بودن کپچا
        assertThat(responseCheck).isNotNull();
        assertThat(responseCheck.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
