package com.friendly.services.infrastructure.config.jpa;

import com.friendly.commons.models.auth.ClientType;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
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

@DependsOn("dbConfig")
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
@PropertySource({"file:${server.path}db.properties"})
@EnableJpaRepositories(
        basePackages = {"com.friendly.services.*.orm.qoe.repository"},
        entityManagerFactoryRef = "qoeEntityManager",
        transactionManagerRef = "qoeTransactionManager"
)
@RequiredArgsConstructor
public class JpaQoeConfig {
    @NonNull
    private final Environment env;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.qoe")
    public HikariDataSource qoeDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public FactoryBean<EntityManagerFactory> qoeEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(qoeDataSource());
        em.setPackagesToScan("com.friendly.services.orm.model.qoe");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", env.getProperty("mysql.dialect"));
//        properties.put("hibernate.dialect", env.getProperty("clickhouse.sc.dialect"));
        properties.put("hibernate.enable_lazy_load_no_trans", env.getProperty("spring.jpa.properties.hibernate.enable_lazy_load_no_trans"));
        properties.put("jpa.open-in-view", env.getProperty("spring.jpa.open-in-view"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager qoeTransactionManager(
            @Qualifier("qoeEntityManager") EntityManagerFactory entityManager) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager);
        return transactionManager;
    }

}