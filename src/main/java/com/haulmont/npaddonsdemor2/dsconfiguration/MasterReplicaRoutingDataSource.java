package com.haulmont.npaddonsdemor2.dsconfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterReplicaRoutingDataSource extends AbstractRoutingDataSource {
    private static final String MASTER_DATASOURCE = "master";
    private static final String REPLICA_DATASOURCE = "replica";

    private static final Logger log = LoggerFactory.getLogger(MasterReplicaRoutingDataSource.class);
    private static final ThreadLocal<String> currentDataSource = ThreadLocal.withInitial(() -> MASTER_DATASOURCE);
    private static final ThreadLocal<Integer> slaveCounter = ThreadLocal.withInitial(() -> 1);
    private static Integer dataSourcesSize;

    public MasterReplicaRoutingDataSource(DataSource masterDataSource, List<DataSource> slaveDataSources) {
        Map<Object, Object> dataSources = new HashMap<>(slaveDataSources.size());
        dataSources.put(MASTER_DATASOURCE, masterDataSource);

        int i = 1;
        for (DataSource slaveDataSource : slaveDataSources) {
            dataSources.put(REPLICA_DATASOURCE + i++, slaveDataSource);
        }
        dataSources.put(REPLICA_DATASOURCE + i, masterDataSource);
        dataSourcesSize = slaveDataSources.size() + 1;

        super.setTargetDataSources(dataSources);
        super.setDefaultTargetDataSource(masterDataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (!readOnly) {
            currentDataSource.set(MASTER_DATASOURCE);
        } else {
            Integer counter = slaveCounter.get();
            currentDataSource.set(REPLICA_DATASOURCE + counter);
            if (counter.equals(dataSourcesSize)) {
                slaveCounter.set(1);
            } else {
                slaveCounter.set(++counter);
            }
        }

        String lookupKey = currentDataSource.get();
         if (log.isDebugEnabled()) {
            log.debug("Current lookup key: {}", lookupKey);
        }
        return lookupKey;
    }

    public void clearSlaveCounter() {
        slaveCounter.set(1);
    }
}
