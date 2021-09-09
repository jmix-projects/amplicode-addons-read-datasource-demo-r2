import com.haulmont.npaddonsdemor2.dsconfiguration.MasterSlaveDataSourcesConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.haulmont.npaddonsdemor2",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ,
                pattern = "com.haulmont.npaddonsdemor2.dsconfiguration.* " +
                        "|| com.haulmont.npaddonsdemor2.NpAddonsDemoR2Application"))
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "com.haulmont.npaddonsdemor2")
@EnableTransactionManagement
public class DemoR2TestConfiguration {

    @Configuration
    static class MasterSlaveDataSourcesTestConfiguration extends MasterSlaveDataSourcesConfiguration {
        @Bean("masterDs")
        DataSource masterDataSourceTest() {
            return new EmbeddedDatabaseBuilder()
                    .setName("master;sql.syntax_pgs=true")
                    .addScripts("scripts/schema.sql", "scripts/data-master.sql")
                    .build();
        }

        @Bean("slaveDs")
        DataSource slaveDataSourceTest() {
            return new EmbeddedDatabaseBuilder()
                    .setName("slave;sql.syntax_pgs=true")
                    .addScripts("scripts/schema.sql", "scripts/data-slave.sql")
                    .build();
        }

        @Bean("masterTemplate")
        JdbcTemplate masterJdbcTemplate() {
            return new JdbcTemplate(masterDataSourceTest());
        }

        @Bean("slaveTemplate")
        JdbcTemplate slaveJdbcTemplate() {
            return new JdbcTemplate(slaveDataSourceTest());
        }

        @Bean
        @Primary
        @Override
        public DataSource routingDataSource() {
            return new MasterReplicaRoutingDataSource(
                    masterDataSourceTest(),
                    slaveDataSourceTest()
            );
        }
    }
}
