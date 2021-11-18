import org.springframework.beans.factory.annotation.Qualifier;
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
                .setName("readOnly;sql.syntax_pgs=true")
                .build();
    }

    @Bean("mainDatasource")
    public DataSource mainDatasource() {
        return new EmbeddedDatabaseBuilder()
                .setName("main;sql.syntax_pgs=true")
                .build();
    }

    @Bean
    public JdbcTemplate mainJdbcTemplate(@Qualifier("mainDatasource") DataSource mainDatasource) {
        return new JdbcTemplate(mainDatasource);
    }

    @Bean
    public JdbcTemplate readOnlyJdbcTemplate(@Qualifier("readonlyDatasource") DataSource readonlyDatasource) {
        return new JdbcTemplate(readonlyDatasource);
    }
}
