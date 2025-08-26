package com.cricket.filter;

import com.example.multitenant.util.TenantContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class WebTenantFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        
        try {
            // Skip filter for static resources
            if (requestURI.startsWith("/css/") || requestURI.startsWith("/js/") || 
                requestURI.startsWith("/images/") || requestURI.startsWith("/webjars/")) {
                chain.doFilter(request, response);
                return;
            }
            
            String tenantId = null;
            
            if (requestURI.startsWith("/api/")) {
                // API: prefer header-based tenant, do NOT create session
                tenantId = httpRequest.getHeader("X-Tenant-ID");
                if (tenantId == null || tenantId.isBlank()) {
                    // Fallback: try session only if it exists
                    HttpSession existing = httpRequest.getSession(false);
                    if (existing != null) {
                        tenantId = (String) existing.getAttribute("currentTenant");
                    }
                }
                if (tenantId == null || tenantId.isBlank()) {
                    tenantId = "tenant1"; // safe default
                }
            } else {
                // Web pages: use session to persist selection
                HttpSession session = httpRequest.getSession(true);
                tenantId = (String) session.getAttribute("currentTenant");
                if (tenantId == null || tenantId.isBlank()) {
                    tenantId = "tenant1";
                    session.setAttribute("currentTenant", tenantId);
                }
            }

            // Set tenant context for this request
            TenantContext.setTenantId(tenantId);
            System.out.println("WebTenantFilter: Set tenant context to '" + tenantId + "' for request: " + requestURI);
            
            // Continue with the request
            chain.doFilter(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in WebTenantFilter: " + e.getMessage());
            e.printStackTrace();
            throw new ServletException("Error processing tenant context", e);
        } finally {
            // Clear tenant context after request
            TenantContext.clear();
        }
    }
}
