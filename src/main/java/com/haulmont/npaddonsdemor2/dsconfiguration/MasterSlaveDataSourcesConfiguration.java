package com.haulmont.npaddonsdemor2.dsconfiguration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class MasterSlaveDataSourcesConfiguration {

    @Bean("slaveDs")
    @Qualifier("slaveDs")
    @ConfigurationProperties(prefix = "datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("slave1Ds")
    @Qualifier("slaveDs")
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
    public DataSource routingDataSource(@Qualifier("masterDs") DataSource masterDataSource,
                                        @Qualifier("slaveDs") List<DataSource> slaveDataSources) {
        return new MasterReplicaRoutingDataSource(
                masterDataSource,
                slaveDataSources
        );
    }

    @Bean("proxyRoutingDs")
    @Primary
    public DataSource dataSource(@Qualifier("routingDs") DataSource routingDs) {
        return new LazyConnectionDataSourceProxy(routingDs);
    }
}
