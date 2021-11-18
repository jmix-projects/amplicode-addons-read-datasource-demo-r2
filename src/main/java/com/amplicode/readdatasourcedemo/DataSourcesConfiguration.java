package com.amplicode.readdatasourcedemo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class DataSourcesConfiguration {

    @Bean("readonlyDatasource")
    @ConfigurationProperties(prefix = "datasource.readonly")
    public DataSource readonlyDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("mainDatasource")
    @ConfigurationProperties(prefix = "datasource.main")
    public DataSource mainDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean("datasource")
    public DataSource balancedDatasource(@Qualifier("mainDatasource") DataSource mainDatasource,
                                         @Qualifier("readonlyDatasource") DataSource readonlyDatasource) {
        return new LazyConnectionDataSourceProxy(
                new BalancedDataSource(mainDatasource, readonlyDatasource));
    }
}
