package com.example.multitenant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "multitenant")
public class MultiTenantProperties {
    private String header = "X-Tenant-Id";
    private String masterJndi;
    private String masterJdbcUrl;
    private String masterUsername;
    private String masterPassword;
    private int cacheSeconds = 300;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMasterJndi() {
        return masterJndi;
    }

    public void setMasterJndi(String masterJndi) {
        this.masterJndi = masterJndi;
    }

    public String getMasterJdbcUrl() {
        return masterJdbcUrl;
    }

    public void setMasterJdbcUrl(String masterJdbcUrl) {
        this.masterJdbcUrl = masterJdbcUrl;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public int getCacheSeconds() {
        return cacheSeconds;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheSeconds = cacheSeconds;
    }
}
