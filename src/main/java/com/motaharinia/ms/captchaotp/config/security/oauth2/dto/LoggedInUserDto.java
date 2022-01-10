package com.motaharinia.ms.captchaotp.config.security.oauth2.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author eng.motahari@gmail.com<br>
 * کلاس مدل کاربر لاگین شده
 */
@Data
@NoArgsConstructor
public class LoggedInUserDto extends SecurityUserDto implements Serializable {
    /**
     * کاربر فرانت برنامه
     */
    AppUserDto appUserDto;
    /**
     * کاربر بک برنامه
     */
    BackUserDto backUserDto;
    /**
     * لیست نقش های کاربری
     */
    Set<String> securityRoleSet = new HashSet<>();
    /**
     * لیست دسترسی ها
     */
    Set<String> securityPermissionSet = new HashSet<>();

    /**
     * متد سازنده لاگین فرانت
     *
     * @param securityUserDto مدل کاربر امنیت
     * @param appUserDto      مدل کاربر فرانت برنامه
     * @param securityRoleSet       لیست نقش های کاربری
     * @param securityPermissionSet       لیست دسترسی ها
     */
    public LoggedInUserDto(SecurityUserDto securityUserDto, Set<String> securityRoleSet, Set<String> securityPermissionSet, AppUserDto appUserDto) {
        this.setSecurityUserDto(securityUserDto);
        this.appUserDto = appUserDto;
        this.securityRoleSet = securityRoleSet;
        this.securityPermissionSet = securityPermissionSet;
    }

    /**
     * متد سازنده لاگین بک
     *
     * @param securityUserDto مدل کاربر امنیت
     * @param backUserDto     مدل کاربر بک برنامه
     * @param securityRoleSet       لیست نقش های کاربری
     * @param securityPermissionSet       لیست دسترسی ها
     */
    public LoggedInUserDto(SecurityUserDto securityUserDto, Set<String> securityRoleSet, Set<String> securityPermissionSet, BackUserDto backUserDto) {
        this.setSecurityUserDto(securityUserDto);
        this.backUserDto = backUserDto;
        this.securityRoleSet = securityRoleSet;
        this.securityPermissionSet = securityPermissionSet;
    }

    /**
     * متد پر کننده فیلدها از روی مدل کاربر امنیت
     *
     * @param securityUserDto مدل کاربر امنیت
     */
    private void setSecurityUserDto(SecurityUserDto securityUserDto) {
        this.setId(securityUserDto.getId());
        this.setAppUserId(securityUserDto.getAppUserId());
        this.setBackUserId(securityUserDto.getBackUserId());
        this.setUsername(securityUserDto.getUsername());
        this.setMobileNo(securityUserDto.getMobileNo());
        this.setEmailAddress(securityUserDto.getEmailAddress());
        this.setAccountExpired(securityUserDto.getAccountExpired());
        this.setAccountLocked(securityUserDto.getAccountLocked());
        this.setCredentialExpired(securityUserDto.getCredentialExpired());
        this.setEnabled(securityUserDto.getEnabled());
    }


}
