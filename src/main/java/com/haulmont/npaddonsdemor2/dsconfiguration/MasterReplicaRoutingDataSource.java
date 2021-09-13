package com.haulmont.npaddonsdemor2.dsconfiguration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterReplicaRoutingDataSource extends AbstractRoutingDataSource {
    private static final String MASTER_DATASOURCE = "master";
    private static final String REPLICA_DATASOURCE = "replica";

    private static final ThreadLocal<String> currentDataSource = ThreadLocal.withInitial(() -> MASTER_DATASOURCE);
    private static final ThreadLocal<Integer> slaveCounter = ThreadLocal.withInitial(() -> 1);
    private static Integer dataSourcesSize;

    protected MasterReplicaRoutingDataSource(DataSource masterDataSource, List<DataSource> slaveDataSources) {
        Map<Object, Object> dataSources = new HashMap<>(slaveDataSources.size());
        dataSources.put(MASTER_DATASOURCE, masterDataSource);

        int i = 1;
        for (DataSource slaveDataSource : slaveDataSources) {
            dataSources.put(REPLICA_DATASOURCE + i++, slaveDataSource);
        }
        dataSourcesSize = slaveDataSources.size();

        super.setTargetDataSources(dataSources);
        super.setDefaultTargetDataSource(masterDataSource);
    }

    public static boolean isCurrentlyReadonly() {
        return currentDataSource.get().contains(REPLICA_DATASOURCE);
    }

    public static void setReadonlyDataSource(boolean readOnly) {
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
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return currentDataSource.get();
    }
}
