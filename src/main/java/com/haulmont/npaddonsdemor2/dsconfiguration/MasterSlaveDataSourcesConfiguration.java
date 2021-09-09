package com.haulmont.npaddonsdemor2.dsconfiguration;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class MasterSlaveDataSourcesConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "datasource.master")
    public DataSourceProperties masterDataSource() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.slave")
    public DataSourceProperties slaveDataSource() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource routingDataSource() {
        return new MasterReplicaRoutingDataSource(
                dataSourceFromProperties(masterDataSource()),
                dataSourceFromProperties(slaveDataSource())
        );
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(routingDataSource())
                .packages("com.haulmont.npaddonsdemor2")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(@Qualifier("jpaTxManager") PlatformTransactionManager wrapped) {
        return new ReplicaAwareTransactionManager(wrapped);
    }

    @Bean(name = "jpaTxManager")
    public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    protected DataSource dataSourceFromProperties(DataSourceProperties properties) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(properties.getUrl());
        dataSource.setUser(properties.getUsername());
        dataSource.setPassword(properties.getPassword());
        return dataSource;
    }
}
