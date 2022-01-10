package com.motaharinia.ms.captchaotp.config.security.oauth2.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل کاربر امنیت
 */
@Data
public class SecurityUserDto implements Serializable {
    /**
     * شناسه
     */
    private Long id;
    /**
     * شناسه کاربر برنامه فرانت
     */
    private Long appUserId;
    /**
     * شناسه کاربر برنامه بک
     */
    private Long backUserId;
    /**
     * کلمه کاربری
     */
    private String username;
    /**
     * شماره تلفن همراه جهت بازیابی کلمه عبور
     */
    private String mobileNo;
    /**
     * نشانی پست الکترونیک جهت بازیابی کلمه عبور
     */
    private String emailAddress;
    /**
     * حساب کاربری منقضی شده است؟
     */
    private Boolean accountExpired = false;
    /**
     * حساب کاربری قفل شده است؟
     */
    private Boolean accountLocked = false;
    /**
     * اطلاعات لاگین منقضی شده است؟
     */
    private Boolean credentialExpired = false;
    /**
     * کاربر فعال است؟
     */
    private Boolean enabled = true;
}
