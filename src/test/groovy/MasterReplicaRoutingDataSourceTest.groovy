import com.haulmont.npaddonsdemor2.service.OwnerServiceInner
import com.haulmont.npaddonsdemor2.service.OwnerServiceOuter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.CannotCreateTransactionException
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

@ContextConfiguration(classes = DemoR2TestConfiguration)
class MasterReplicaRoutingDataSourceTest extends Specification {

    @Autowired
    OwnerServiceInner ownerServiceInner

    @Autowired
    OwnerServiceOuter ownerServiceOuter

    @Autowired
    @Qualifier("masterDs")
    DataSource masterDs

    @Autowired
    @Qualifier("slaveDs")
    DataSource slaveDs

    @Autowired
    MasterReplicaRoutingDataSource routingDs

    @Shared
    List<JdbcTemplate> jdbcTemplates

    @Autowired
    void autowireJdbcTemplates(List<JdbcTemplate> jdbcTemplates) {
        this.jdbcTemplates = jdbcTemplates
    }

    def "Test read-write transaction"() {
        when:
        def owners = ownerServiceInner.findAll()

        then:
        masterDs == routingDs.determineTargetDataSource()
        !owners.isEmpty()
        "Anton" == owners.get(0).firstName
        "Ivanov" == owners.get(0).lastName
    }

    def "Test read-only transaction"() {
        when:
        def owners = ownerServiceInner.findAllReadOnly()

        then:
        slaveDs == routingDs.determineTargetDataSource()
        !owners.isEmpty()
        "Anton_Slave" == owners.get(0).firstName
        "Ivanov_Slave" == owners.get(0).lastName
    }

    def "Test nested transactions: read-only -> read-write"() {
        when:
        ownerServiceOuter.findAllReadOnly()

        then:
        thrown CannotCreateTransactionException
    }

    def "Test nested transactions: read-write -> read-only"() {
        when:
        def owners = ownerServiceOuter.findAll()

        then:
        masterDs == routingDs.determineTargetDataSource()
        !owners.isEmpty()
        "Anton" == owners.get(0).firstName
        "Ivanov" == owners.get(0).lastName
    }

    void cleanupSpec() {
        jdbcTemplates.each { it.execute("DROP SCHEMA PUBLIC CASCADE") }
    }
}
