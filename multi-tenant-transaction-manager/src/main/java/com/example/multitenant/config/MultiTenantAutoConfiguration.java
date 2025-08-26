package com.example.multitenant.config;

import com.example.multitenant.datasource.MultiTenantDataSource;
import com.example.multitenant.datasource.TenantDataSourceFactory;
import com.example.multitenant.filter.TenantFilter;
import com.example.multitenant.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@AutoConfiguration
@EnableConfigurationProperties(MultiTenantProperties.class)
public class MultiTenantAutoConfiguration {

    @Bean(name = "masterDataSource")
    public DataSource masterDataSource(MultiTenantProperties props) throws Exception {
        if (props.getMasterJndi() != null && !props.getMasterJndi().isBlank()) {
            return TenantDataSourceFactory.jndi(props.getMasterJndi());
        }
        return TenantDataSourceFactory.hikari(props.getMasterJdbcUrl(), props.getMasterUsername(), props.getMasterPassword());
    }

    @Bean
    @Lazy
    public TenantRepository tenantRepository(@Qualifier("masterDataSource") DataSource masterDataSource) {
        return new TenantRepository(new JdbcTemplate(masterDataSource));
    }

    @Bean
    @Primary
    @Lazy
    public DataSource tenantDataSource(@Lazy TenantRepository tenantRepository) {
        return new MultiTenantDataSource(tenantRepository);
    }

    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilterRegistration(MultiTenantProperties props) {
        FilterRegistrationBean<TenantFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TenantFilter(props));
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }
}
