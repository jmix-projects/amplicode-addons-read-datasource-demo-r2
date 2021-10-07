import com.haulmont.npaddonsdemor2.dsconfiguration.MasterSlaveDataSourcesConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.haulmont.npaddonsdemor2",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ,
                pattern = "com.haulmont.npaddonsdemor2.dsconfiguration.* " +
                        "|| com.haulmont.npaddonsdemor2.NpAddonsDemoR2Application"))
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "com.haulmont.npaddonsdemor2.repository")
@EnableTransactionManagement
public class DemoR2TestConfiguration {

    @Configuration
    static class MasterSlaveDataSourcesTestConfiguration extends MasterSlaveDataSourcesConfiguration {

        @Override
        public DataSource slaveDataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setName("slave;sql.syntax_pgs=true")
                    .addScripts("scripts/schema.sql", "scripts/data-slave.sql")
                    .build();
        }

        @Override
        public DataSource slave1DataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setName("slave1;sql.syntax_pgs=true")
                    .addScripts("scripts/schema.sql", "scripts/data-slave1.sql")
                    .build();
        }

        @Override
        public DataSource masterDataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setName("master;sql.syntax_pgs=true")
                    .addScripts("scripts/schema.sql", "scripts/data-master.sql")
                    .build();
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
                                                                           @Qualifier("proxyRoutingDs") DataSource routingDataSource) {
            return builder
                    .dataSource(routingDataSource)
                    .packages("com.haulmont.npaddonsdemor2")
                    .build();
        }

        @Bean
        JdbcTemplate masterJdbcTemplate() {
            return new JdbcTemplate(masterDataSource());
        }

        @Bean
        JdbcTemplate slaveJdbcTemplate() {
            return new JdbcTemplate(slaveDataSource());
        }

        @Bean
        JdbcTemplate slave1JdbcTemplate() {
            return new JdbcTemplate(slave1DataSource());
        }
    }
}
