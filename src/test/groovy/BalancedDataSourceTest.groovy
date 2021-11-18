import com.amplicode.readdatasourcedemo.BalancedDataSource
import com.amplicode.readdatasourcedemo.DemoApplication
import com.amplicode.readdatasourcedemo.entity.Owner
import com.amplicode.readdatasourcedemo.repository.OwnerRepository
import com.amplicode.readdatasourcedemo.service.OwnerServiceInner
import com.amplicode.readdatasourcedemo.service.OwnerServiceOuter
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.init.DatabasePopulator
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator
import org.springframework.test.context.TestPropertySource
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

@EnableSharedInjection
@SpringBootTest(classes = [DemoApplication, DemoTestConfiguration])
@TestPropertySource("classpath:test-application.properties")
class BalancedDataSourceTest extends Specification {

    @Autowired
    OwnerServiceInner ownerServiceInner

    @Autowired
    OwnerServiceOuter ownerServiceOuter

    @Autowired
    DataSource dataSource

    @Autowired
    @Qualifier("mainDatasource")
    DataSource mainDatasource

    @Autowired
    @Qualifier("readonlyDatasource")
    DataSource readonlyDatasource

    @Shared
    @Autowired
    List<JdbcTemplate> jdbcTemplates

    @Autowired
    OwnerRepository ownerRepository

    @Autowired
    ResourceLoader resourceLoader

    void setup() {
        DatabasePopulator mainDbMigrator = new ResourceDatabasePopulator()
        mainDbMigrator.addScript(resourceLoader.getResource("classpath:scripts/schema.sql"))
        mainDbMigrator.addScript(resourceLoader.getResource("classpath:scripts/data-main.sql"))

        mainDbMigrator.execute(mainDatasource)

        DatabasePopulator readonlyDbMigrator = new ResourceDatabasePopulator()
        readonlyDbMigrator.addScript(resourceLoader.getResource("classpath:scripts/schema.sql"))
        readonlyDbMigrator.addScript(resourceLoader.getResource("classpath:scripts/data-readonly.sql"))

        readonlyDbMigrator.execute(readonlyDatasource)

        DataSource targetDataSource = null
        if (dataSource instanceof LazyConnectionDataSourceProxy) {
            targetDataSource = dataSource.getTargetDataSource();
        }
        if (targetDataSource instanceof BalancedDataSource) {
            (BalancedDataSource) targetDataSource.clearReadOnlyCounter()
        }
    }

    void cleanup() {
        jdbcTemplates.each {
            it.execute("DROP TABLE owner") }
    }

    def "test read in read-write transaction"() {
        when:
        def owners = ownerServiceInner.findAll()

        then:
        !owners.isEmpty()
        "Anton" == owners.get(0).firstName
        "Ivanov" == owners.get(0).lastName
    }

    def "test write in read-write transaction"() {
        when:
        Owner owner = new Owner(firstName: "Alex", lastName: "Petrov", address: "Address", city: "Samara")

        def saved = ownerServiceInner.saveAndGet(owner)

        then:
        saved != null
    }

    def "test write in read-only transaction"() {
        when:
        Owner owner = new Owner(firstName: "Alex", lastName: "Petrov", address: "Address", city: "Samara")

        ownerServiceInner.saveAndGetReadOnly(owner)

        then:
        thrown(DataAccessException)
    }


    def "test read-only transaction and round-robin routing"() {

        when:
        def owners = ownerServiceInner.findAllReadOnly()

        then:
        !owners.isEmpty()
        "Anton_Slave" == owners.get(0).firstName
        "Ivanov_Slave" == owners.get(0).lastName

        when:
        owners = ownerServiceInner.findAllReadOnly()

        then:
        !owners.isEmpty()
        "Anton" == owners.get(0).firstName
        "Ivanov" == owners.get(0).lastName

        when:
        owners = ownerServiceInner.findAllReadOnly()

        then:
        !owners.isEmpty()
        "Anton_Slave" == owners.get(0).firstName
        "Ivanov_Slave" == owners.get(0).lastName
    }

    def "Test nested transactions: read-only -> read-write"() {
        when:
        def owners = ownerServiceOuter.findAllReadOnly()

        then:
        "Anton_Slave" == owners.get(0).firstName
        "Ivanov_Slave" == owners.get(0).lastName
    }

    def "Test nested transactions: read-only -> read-write (propagation: requires_new)"() {
        when:
        def owners = ownerServiceOuter.findAllRequiresNew()

        then:
        noExceptionThrown()
        "Anton" == owners.get(0).firstName
        "Ivanov" == owners.get(0).lastName
    }

    def "Test nested transactions: read-write -> read-only"() {
        when:
        def owners = ownerServiceOuter.findAll()

        then:
        !owners.isEmpty()
        "Anton" == owners.get(0).firstName
        "Ivanov" == owners.get(0).lastName
    }
}
