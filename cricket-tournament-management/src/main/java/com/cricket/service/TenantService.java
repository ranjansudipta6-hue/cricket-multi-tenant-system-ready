package com.cricket.service;

import com.example.multitenant.entity.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class TenantService {

    @Autowired(required = false)
    @Qualifier("masterJdbcTemplate")
    private JdbcTemplate masterJdbcTemplate;

    private final RowMapper<Tenant> tenantRowMapper = new RowMapper<Tenant>() {
        @Override
        public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tenant tenant = new Tenant();
            tenant.setId(rs.getString("tenant_id"));
            tenant.setJdbcUrl(rs.getString("db_url"));
            tenant.setUsername(rs.getString("db_username"));
            tenant.setPassword(rs.getString("db_password"));
            tenant.setDriverClass(rs.getString("driver_class"));
            tenant.setJndiName(rs.getString("jndi_name"));
            tenant.setActive(rs.getBoolean("active"));
            return tenant;
        }
    };

    public List<Tenant> getAllActiveTenants() {
        if (masterJdbcTemplate != null) {
            try {
                return masterJdbcTemplate.query(
                    "SELECT tenant_id, db_url, db_username, db_password, driver_class, jndi_name, active " +
                    "FROM tenants WHERE active = 1 ORDER BY tenant_id", 
                    tenantRowMapper
                );
            } catch (Exception e) {
                System.out.println("Failed to fetch tenants from master DB: " + e.getMessage());
            }
        }
        // Fallback to default tenants if master DB is not available
        return getDefaultTenants();
    }

    public Tenant getTenantById(String tenantId) {
        if (masterJdbcTemplate != null) {
            try {
                List<Tenant> tenants = masterJdbcTemplate.query(
                    "SELECT tenant_id, db_url, db_username, db_password, driver_class, jndi_name, active " +
                    "FROM tenants WHERE tenant_id = ? AND active = 1", 
                    tenantRowMapper, tenantId
                );
                if (!tenants.isEmpty()) {
                    return tenants.get(0);
                }
            } catch (Exception e) {
                System.out.println("Failed to fetch tenant from master DB: " + e.getMessage());
            }
        }
        // Fallback for default tenants
        return getDefaultTenants().stream()
            .filter(t -> t.getId().equals(tenantId))
            .findFirst()
            .orElse(null);
    }

    private List<Tenant> getDefaultTenants() {
        // Fallback tenants when master DB is not available
        Tenant tenant1 = new Tenant();
        tenant1.setId("tenant1");
        tenant1.setActive(true);
        
        Tenant tenant2 = new Tenant();
        tenant2.setId("tenant2");
        tenant2.setActive(true);
        
        return List.of(tenant1, tenant2);
    }
}
