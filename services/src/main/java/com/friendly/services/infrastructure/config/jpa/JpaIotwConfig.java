package com.friendly.services.infrastructure.config.jpa;

import com.friendly.commons.models.settings.DatabaseType;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;

@DependsOn("dbConfig")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@PropertySource({"file:${server.path}db.properties"})
@EnableJpaRepositories(
        basePackages = {"com.friendly.services.*.orm.iotw.repository"},
        entityManagerFactoryRef = "iotwEntityManager",
        transactionManagerRef = "iotwTransactionManager"
)
@Slf4j
@RequiredArgsConstructor
public class JpaIotwConfig {
    @Value("${spring.liquibase.change-log}")
    private String CHANGELOG_PATH;

    @NonNull
    private final Environment env;

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.iotw")
    public HikariDataSource iotwDataSource() {
        return new HikariDataSource();
    }

    @Primary
    @Bean
    public FactoryBean<EntityManagerFactory> iotwEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(iotwDataSource());
        em.setPackagesToScan("com.friendly.services.orm.model.iotw");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", DbConfig.getDbType().equals(DatabaseType.Oracle)
                ? env.getProperty("oracle.dialect") : env.getProperty("mysql.dialect"));
        properties.put("hibernate.enable_lazy_load_no_trans", env.getProperty("spring.jpa.properties.hibernate.enable_lazy_load_no_trans"));
        properties.put("jpa.open-in-view", env.getProperty("spring.jpa.open-in-view"));
        em.setJpaPropertyMap(properties);
        log.info("IOTW DB properties: " + properties);
        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager iotwTransactionManager(
            @Qualifier("iotwEntityManager") EntityManagerFactory entityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager);
        return transactionManager;
    }

    @Bean(name = "liquibaseIotw")
    public SpringLiquibase liquibaseIotw() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(CHANGELOG_PATH + "/ui/db.changelog-master.yaml");
        liquibase.setDataSource(iotwDataSource());
        return liquibase;
    }
}