import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

public class MasterReplicaRoutingDataSource extends com.haulmont.npaddonsdemor2.dsconfiguration.MasterReplicaRoutingDataSource
        implements ApplicationContextAware {

    private Map<String, DataSource> dataSourceBeans;

    public MasterReplicaRoutingDataSource(DataSource master, DataSource slave) {
        super(master, slave);
    }

    @Override
    public DataSource determineTargetDataSource() {
        DataSource dataSource = super.determineTargetDataSource();
        String dataSourceName = getBeanName(dataSource);
        logger.info(String.format("Currently used datasource is %s", dataSourceName));
        return dataSource;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.dataSourceBeans = applicationContext.getBeansOfType(DataSource.class);
    }

    private String getBeanName(DataSource dataSource) {
        return dataSourceBeans.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), dataSource))
                .map(Map.Entry::getKey)
                .findFirst()
                .get();
    }
}
