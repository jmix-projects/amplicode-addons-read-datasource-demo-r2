import com.haulmont.npaddonsdemor2.dsconfiguration.MasterReplicaRoutingDataSource
import com.haulmont.npaddonsdemor2.repository.OwnerRepository
import com.haulmont.npaddonsdemor2.service.OwnerServiceInner
import com.haulmont.npaddonsdemor2.service.OwnerServiceOuter
import org.spockframework.spring.EnableSharedInjection
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

import javax.sql.DataSource

@ContextConfiguration(classes = DemoR2TestConfiguration)
@EnableSharedInjection
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
    @Qualifier("slave1Ds")
    DataSource slave1Ds

    @Autowired
    Map<String, DataSource> dataSources

    @Autowired
    MasterReplicaRoutingDataSource routingDs

    @Shared
    @Autowired
    List<JdbcTemplate> jdbcTemplates

    @Autowired
    OwnerRepository ownerRepository

    void setup() {
        routingDs.clearSlaveCounter()
    }

    def "Test read-write transaction"() {
        when:
        def owners = ownerServiceInner.findAll()

        then:
        !owners.isEmpty()
        "Anton" == owners.get(0).firstName
        "Ivanov" == owners.get(0).lastName
    }

    def "Test read-only transaction and round-robin routing"() {

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
        "Anton_Slave1" == owners.get(0).firstName
        "Ivanov_Slave1" == owners.get(0).lastName

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

    void cleanupSpec() {
        jdbcTemplates.each { it.execute("DROP SCHEMA PUBLIC CASCADE") }
    }
}
