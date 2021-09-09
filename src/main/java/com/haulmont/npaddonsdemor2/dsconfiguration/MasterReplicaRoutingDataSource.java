package com.haulmont.npaddonsdemor2.dsconfiguration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class MasterReplicaRoutingDataSource extends AbstractRoutingDataSource {
    private static final ThreadLocal<DataSourceType> currentDataSource = new ThreadLocal<>();

    protected MasterReplicaRoutingDataSource(DataSource master, DataSource slave) {
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put(DataSourceType.MASTER, master);
        dataSources.put(DataSourceType.REPLICA, slave);

        super.setTargetDataSources(dataSources);
        super.setDefaultTargetDataSource(master);
    }

    public static boolean isCurrentlyReadonly() {
        return currentDataSource.get() == DataSourceType.REPLICA;
    }

    public static void setReadonlyDataSource(boolean readOnly) {
        currentDataSource.set(readOnly ? DataSourceType.REPLICA : DataSourceType.MASTER);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return currentDataSource.get();
    }

    private enum DataSourceType {
        MASTER, REPLICA
    }
}
