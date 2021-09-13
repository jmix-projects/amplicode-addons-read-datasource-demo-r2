import com.haulmont.npaddonsdemor2.dsconfiguration.MasterSlaveDataSourcesConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.haulmont.npaddonsdemor2",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ,
                pattern = "com.haulmont.npaddonsdemor2.dsconfiguration.* " +
                        "|| com.haulmont.npaddonsdemor2.NpAddonsDemoR2Application"))
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "com.haulmont.npaddonsdemor2")
@EnableTransactionManagement
public class DemoR2TestConfiguration {

    //todo: maybe we should not extend the app configuration and just reuse the same code from the app config. now for example
    // the test depends on data sources definitions order in the app config
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

        @Override
        public DataSource routingDataSource(DataSource masterDataSource, List<DataSource> slaveDataSources) {
            return new MasterReplicaRoutingDataSource(masterDataSource, slaveDataSources);
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
