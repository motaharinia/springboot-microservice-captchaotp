package com.motaharinia.ms.captchaotp.config.security.oauth2.resource.filter;

import com.motaharinia.ms.captchaotp.config.security.oauth2.resource.ResourceTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class BearerFilter extends GenericFilterBean {

    private final ResourceTokenProvider resourceTokenProvider;

    public BearerFilter(ResourceTokenProvider resourceTokenProvider) {
        this.resourceTokenProvider = resourceTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = resourceTokenProvider.resolveAccessToken(httpServletRequest);
        if (!ObjectUtils.isEmpty(token) && this.resourceTokenProvider.isValidToken(token)) {
            Authentication authentication = this.resourceTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


}
