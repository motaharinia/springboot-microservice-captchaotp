package com.motaharinia.ms.captchaotp.config.sourceproject;

/**
 * @author eng.motahari@gmail.com<br>
 * مقادیر ثابت پروژه درخواست دهنده
 */
public enum SourceProjectEnum {
    /**
     * باشگاه مشتریان iam پروژه
     */
    MS_IAM("MS_IAM"),
    /**
     * پروژه دسته بندی و جوایز باشگاه مشتریان
     */
    MS_CATALOG("MS_CATALOG"),
    /**
     * پروژه مدیریت امتیازات باشگاه مشتریان
     */
    MS_POINT_TRACKER("MS_POINT_TRACKER"),
    /**
     * پروژه مدیریت کاربران باشگاه مشتریان
     */
    MS_USER_PANEL("MS_USER_PANEL"),
    ;

    private final String value;

    SourceProjectEnum(String value) {
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