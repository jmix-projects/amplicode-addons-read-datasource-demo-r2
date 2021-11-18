package com.amplicode.readdatasourcedemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalancedDataSource extends AbstractRoutingDataSource {
    private static final String MAIN_DATASOURCE = "main";
    private static final String READ_ONLY_DATASOURCE = "read_only";

    private final ThreadLocal<String> currentDataSource = ThreadLocal.withInitial(() -> MAIN_DATASOURCE);
    private final ThreadLocal<Integer> readonlyCounter = ThreadLocal.withInitial(() -> 1);
    private final int dataSourcesSize;

    private static final Logger log = LoggerFactory.getLogger(BalancedDataSource.class);

    public BalancedDataSource(DataSource masterDataSource, DataSource readOnlyDataSource) {
        this(masterDataSource, Collections.singletonList(readOnlyDataSource));
    }

    public BalancedDataSource(DataSource mainDataSource, List<DataSource> readonlyDataSources) {
        Map<Object, Object> dataSources = new HashMap<>(readonlyDataSources.size());

        dataSources.put(MAIN_DATASOURCE, mainDataSource);

        int i = 1;
        for (DataSource readonlyDataSource : readonlyDataSources) {
            dataSources.put(READ_ONLY_DATASOURCE + i, readonlyDataSource);
            i++;
        }
        dataSources.put(READ_ONLY_DATASOURCE + i, mainDataSource);
        dataSourcesSize = readonlyDataSources.size() + 1;

        super.setTargetDataSources(dataSources);
        super.setDefaultTargetDataSource(mainDataSource);

        afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        if (!readOnly) {
            currentDataSource.set(MAIN_DATASOURCE);
        } else {
            Integer counter = this.readonlyCounter.get();
            currentDataSource.set(READ_ONLY_DATASOURCE + counter);
            if (counter.equals(dataSourcesSize)) {
                this.readonlyCounter.set(1);
            } else {
                this.readonlyCounter.set(++counter);
            }
        }

        String lookupKey = currentDataSource.get();
        log.debug("Current datasource: {}", lookupKey);
        return lookupKey;
    }

    public void clearReadOnlyCounter() {
        readonlyCounter.set(1);
    }
}
