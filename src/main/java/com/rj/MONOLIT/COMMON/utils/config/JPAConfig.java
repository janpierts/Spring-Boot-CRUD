package com.rj.MONOLIT.COMMON.utils.config;

import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManager;

@Configuration
public class JPAConfig {
    @Value("${spring.datasource.url}")
    private String defaultUrl;
    @Value("${spring.datasource.username}")
    private String defaultUser;
    @Value("${spring.datasource.password}")
    private String defaultPassword;
    @Value("${spring.datasource.driver-class-name}")
    private String defaultDriver;

    public EntityManager buildEntityManager(String url, String user, String pass, String driver,List<String> packagesToScan){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url != null ? url : defaultUrl);
        config.setUsername(user != null ? user : defaultUser);
        config.setPassword(pass != null ? pass : defaultPassword);
        config.setDriverClassName(driver != null ? driver : defaultDriver);

        DataSource dataSource = new HikariDataSource(config);
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        List<String> finalPackages = (packagesToScan == null || packagesToScan.isEmpty()) 
                                     ? List.of("com.rj.MONOLIT") 
                                     : packagesToScan;
        emfb.setDataSource(dataSource);
        emfb.setPackagesToScan(finalPackages.toArray(new String[0]));
        emfb.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        emfb.setJpaProperties(props);
        emfb.afterPropertiesSet();

        return emfb.getObject().createEntityManager();
    }
    @Bean
    @Primary
    public EntityManager entityManager() {
        return buildEntityManager(null, null, null, null, List.of("com.rj.MONOLIT"));
    }
}
