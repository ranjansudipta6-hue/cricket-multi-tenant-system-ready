package com.example.multitenant.filter;

import com.example.multitenant.config.MultiTenantProperties;
import com.example.multitenant.util.TenantContext;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class TenantFilter implements Filter {

    private final MultiTenantProperties props;

    public TenantFilter(MultiTenantProperties props) {
        this.props = props;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            String tenant = req.getHeader(props.getHeader());
            if (tenant != null && !tenant.isBlank()) {
                TenantContext.setTenantId(tenant.trim());
            }
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
