package com.motaharinia.ms.captchaotp.config.security.oauth2.dto;


import com.motaharinia.ms.captchaotp.config.security.oauth2.enumeration.GenderEnum;
import com.motaharinia.msutility.custom.customvalidation.mobile.Mobile;
import com.motaharinia.msutility.custom.customvalidation.required.Required;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import java.io.Serializable;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل کاربر فرانت برنامه
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;

    /**
     * نام
     */
    @Required
    private String firstName;

    /**
     * نام خانوادگی
     */
    @Required
    private String lastName;

    /**
     * شناسه یونیک کاربر (کد ملی شخص حقیقی / شناسه ملی سازمان)
     */
    @Required
    private String nationalCode;

    /**
     * جنسیت
     */
    @Required
    private GenderEnum genderEnum;

    /**
     * تلفن همراه
     * اگر کاربر سازمان باشد تلفن همراه یکی از اعضای سازمان که سازمان خواسته ست شده است
     */
    @Required
    @Mobile
    private String mobileNo;

    /**
     * پست الکترونیکی
     * اگر کاربر سازمان باشد پست الکترونیک یکی از اعضای سازمان یا خود شرکت که سازمان خواسته ست شده است
     */
    @Email
    private String emailAddress;

    /**
     *تاریخ ایجاد
     */
    private Long createAt;


}
