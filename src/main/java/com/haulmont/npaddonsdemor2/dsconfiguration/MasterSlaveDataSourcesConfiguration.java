package com.haulmont.npaddonsdemor2.dsconfiguration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;

@Configuration
public class MasterSlaveDataSourcesConfiguration {

    @Bean("slaveDs")
    @ConfigurationProperties(prefix = "datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("slave1Ds")
    @ConfigurationProperties(prefix = "datasource.slave1")
    public DataSource slave1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("masterDs")
    @ConfigurationProperties(prefix = "datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("routingDs")
    @Primary
    public DataSource routingDataSource(@Qualifier("masterDs") DataSource masterDataSource,
                                        List<DataSource> slaveDataSources) {
        return new MasterReplicaRoutingDataSource(
                masterDataSource,
                slaveDataSources
        );
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                       @Qualifier("routingDs") DataSource routingDataSource) {
        return builder
                .dataSource(routingDataSource)
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
}
