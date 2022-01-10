package com.motaharinia.ms.captchaotp.config.security.oauth2.enumeration;
/**
 * مقادیر ثابت جنسیت کاربر برنامه
 */
public enum GenderEnum {
    MALE("MALE"),
    FEMALE("FEMALE");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
