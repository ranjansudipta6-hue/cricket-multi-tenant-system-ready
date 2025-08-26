package com.example.multitenant.datasource;

import com.example.multitenant.repository.TenantRepository;
import com.example.multitenant.util.TenantContext;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.SQLException;

public class MultiTenantDataSource extends AbstractDataSource {

    private final TenantRepository tenantRepository;
    private final Map<String, DataSource> cache = new ConcurrentHashMap<>();

    public MultiTenantDataSource(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    private DataSource currentTenantDataSource() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("No tenant selected in context");
        }
        return cache.computeIfAbsent(tenantId, id -> tenantRepository.resolveDataSource(id));
    }

    @Override
    public Connection getConnection() throws SQLException {
        return currentTenantDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return currentTenantDataSource().getConnection(username, password);
    }
}
