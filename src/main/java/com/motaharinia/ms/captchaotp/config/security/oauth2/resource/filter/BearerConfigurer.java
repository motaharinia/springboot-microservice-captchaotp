package com.motaharinia.ms.captchaotp.config.security.oauth2.resource.filter;

import com.motaharinia.ms.captchaotp.config.security.oauth2.resource.ResourceTokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class BearerConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final ResourceTokenProvider resourceTokenProvider;

    public BearerConfigurer(final ResourceTokenProvider resourceTokenProvider) {
        this.resourceTokenProvider = resourceTokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new BearerFilter(resourceTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }
}