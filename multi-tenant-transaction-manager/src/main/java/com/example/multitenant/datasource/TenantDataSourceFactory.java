package com.example.multitenant.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.sql.DataSource;

public class TenantDataSourceFactory {

    public static DataSource hikari(String url, String user, String pass) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        if (url != null) {
            if (url.startsWith("jdbc:postgresql:")) ds.setDriverClassName("org.postgresql.Driver");
            else if (url.startsWith("jdbc:mysql:")) ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            else if (url.startsWith("jdbc:oracle:")) ds.setDriverClassName("oracle.jdbc.OracleDriver");
        }
        ds.setMaximumPoolSize(10);
        return ds;
    }

    public static DataSource hikari(String url, String user, String pass, String driverClass) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        if (driverClass != null && !driverClass.isBlank()) {
            ds.setDriverClassName(driverClass);
        } else if (url != null) {
            if (url.startsWith("jdbc:postgresql:")) ds.setDriverClassName("org.postgresql.Driver");
            else if (url.startsWith("jdbc:mysql:")) ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
            else if (url.startsWith("jdbc:oracle:")) ds.setDriverClassName("oracle.jdbc.OracleDriver");
        }
        ds.setMaximumPoolSize(10);
        return ds;
    }

    public static DataSource jndi(String jndiName) throws Exception {
        JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
        jndi.setJndiName(jndiName);
        jndi.setProxyInterface(DataSource.class);
        jndi.afterPropertiesSet();
        return (DataSource) jndi.getObject();
    }
}
