import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

@TestConfiguration
public class DemoTestConfiguration {

    @Bean("readonlyDatasource")
    public DataSource readOnlyDatasource() {
        return new EmbeddedDatabaseBuilder()
                .setName("slave;sql.syntax_pgs=true")
                .addScripts("scripts/schema.sql", "scripts/data-slave.sql")
                .build();
    }

    @Bean("mainDatasource")
    public DataSource mainDatasource() {
        return new EmbeddedDatabaseBuilder()
                .setName("master;sql.syntax_pgs=true")
                .addScripts("scripts/schema.sql", "scripts/data-master.sql")
                .build();
    }

    @Bean
    public JdbcTemplate mainJdbcTemplate() {
        return new JdbcTemplate(mainDatasource());
    }

    @Bean
    public JdbcTemplate readOnlyJdbcTemplate() {
        return new JdbcTemplate(readOnlyDatasource());
    }
}
