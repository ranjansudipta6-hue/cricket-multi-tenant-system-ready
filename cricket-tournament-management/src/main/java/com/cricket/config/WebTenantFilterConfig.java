package com.cricket.config;

import com.cricket.filter.WebTenantFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebTenantFilterConfig {

    @Bean
    public FilterRegistrationBean<WebTenantFilter> webTenantFilter() {
        FilterRegistrationBean<WebTenantFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new WebTenantFilter());
        registrationBean.addUrlPatterns("/*"); // Apply to all URLs
        registrationBean.setOrder(0); // Run before core TenantFilter (order 1)
        return registrationBean;
    }
}
