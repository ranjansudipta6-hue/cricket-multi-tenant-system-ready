package com.example.multitenant.repository;

import com.example.multitenant.datasource.TenantDataSourceFactory;
import com.example.multitenant.entity.Tenant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TenantRepository {

    private final JdbcTemplate jdbc;
    private final RowMapper<Tenant> mapper = new RowMapper<>() {
        @Override
        public Tenant mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tenant t = new Tenant();
            t.setId(rs.getString("tenant_id"));
            t.setJndiName(rs.getString("jndi_name"));
            t.setJdbcUrl(rs.getString("db_url"));
            t.setUsername(rs.getString("db_username"));
            t.setPassword(rs.getString("db_password"));
            try {
                t.setDriverClass(rs.getString("driver_class"));
            } catch (SQLException ignore) {
            }
            t.setActive(rs.getBoolean("active"));
            return t;
        }
    };

    public TenantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public DataSource resolveDataSource(String tenantId) {
        Tenant t = findActiveById(tenantId);
        if (t == null) throw new IllegalStateException("Unknown tenant: " + tenantId);
        try {
            if (t.getJndiName() != null && !t.getJndiName().isBlank()) {
                return TenantDataSourceFactory.jndi(t.getJndiName());
            }
        } catch (Exception e) {
            throw new RuntimeException("JNDI lookup failed for tenant " + tenantId, e);
        }
        if (t.getJdbcUrl() == null || t.getJdbcUrl().isBlank()) {
            throw new IllegalStateException("No JDBC URL configured for tenant " + tenantId);
        }
        if (t.getDriverClass() != null && !t.getDriverClass().isBlank()) {
            return TenantDataSourceFactory.hikari(t.getJdbcUrl(), t.getUsername(), t.getPassword(), t.getDriverClass());
        }
        return TenantDataSourceFactory.hikari(t.getJdbcUrl(), t.getUsername(), t.getPassword());
    }

    public Tenant findActiveById(String id) {
        List<Tenant> list = jdbc.query(
                "SELECT tenant_id, jndi_name, db_url, db_username, db_password, driver_class, active FROM tenants WHERE tenant_id = ? AND active = 1",
                mapper, id);
        return list.isEmpty() ? null : list.get(0);
    }
}
