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
import javax.sql.DataSource;
import java.util.HashMap;

@DependsOn("dbConfig")
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@PropertySource({"file:${server.path}db.properties"})
@EnableJpaRepositories(
        basePackages = {"com.friendly.services.*.orm.acs.repository"},
        entityManagerFactoryRef = "acsEntityManager",
        transactionManagerRef = "acsTransactionManager"
)
@RequiredArgsConstructor
public class JpaAcsConfig {
    @Value("${spring.liquibase.change-log}")
    private String CHANGELOG_PATH;

    @NonNull
    private final Environment env;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.acs")
    public DataSource acsDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public FactoryBean<EntityManagerFactory> acsEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(acsDataSource());
        em.setPackagesToScan("com.friendly.services.orm.model.acs");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", DbConfig.getDbType().equals(DatabaseType.Oracle)
                ? env.getProperty("oracle.dialect") : env.getProperty("mysql.dialect"));
        properties.put("hibernate.enable_lazy_load_no_trans", env.getProperty("spring.jpa.properties.hibernate.enable_lazy_load_no_trans"));
        properties.put("jpa.open-in-view", env.getProperty("spring.jpa.open-in-view"));
        log.info("FTACS DB properties: " + properties);
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager acsTransactionManager(
            @Qualifier("acsEntityManager") EntityManagerFactory entityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager);
        return transactionManager;
    }

    @Bean(name = "liquibaseAcs")
    public SpringLiquibase liquibaseAcs() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(CHANGELOG_PATH + "/acs/db.changelog-master.yaml");
        liquibase.setDataSource(acsDataSource());
        return liquibase;
    }

}