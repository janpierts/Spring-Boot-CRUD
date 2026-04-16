package com.rj.MONOLIT.COMMON.utils.settings;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class JDBCConfig {
    @Value("${spring.datasource.url}")
    private String defaultUrl;
    @Value("${spring.datasource.username}")
    private String defaultUser;
    @Value("${spring.datasource.password}")
    private String defaultPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String defaultDriver;

    public DataSource createDataSource(String url, String user, String pass, String driver){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url != null ? url : defaultUrl);
        config.setUsername(user != null ? user : defaultUser);
        config.setPassword(pass != null ? pass : defaultPassword);
        config.setDriverClassName(driver != null ? driver : defaultDriver);
        config.setMaximumPoolSize(15);
        return new HikariDataSource(config);
    }
    @Bean
    @Primary
    public DataSource dataSource() {
        return createDataSource(null, null, null, null);
    }
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
