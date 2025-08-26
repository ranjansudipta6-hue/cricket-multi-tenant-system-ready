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
        DataSource ds = cache.computeIfAbsent(tenantId, id -> {
            System.out.println("[MultiTenantDataSource] Resolving DataSource for tenant: " + id);
            return tenantRepository.resolveDataSource(id);
        });
        return ds;
    }

    @Override
    public Connection getConnection() throws SQLException {
        System.out.println("[MultiTenantDataSource] getConnection for tenant: " + TenantContext.getTenantId());
        return currentTenantDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        System.out.println("[MultiTenantDataSource] getConnection(u,p) for tenant: " + TenantContext.getTenantId());
        return currentTenantDataSource().getConnection(username, password);
    }
}
