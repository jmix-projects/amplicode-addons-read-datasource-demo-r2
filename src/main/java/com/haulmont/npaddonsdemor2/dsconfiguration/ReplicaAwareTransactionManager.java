package com.haulmont.npaddonsdemor2.dsconfiguration;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

//todo: remove if all is ok
class ReplicaAwareTransactionManager implements PlatformTransactionManager {
    private final PlatformTransactionManager wrapped;

    protected ReplicaAwareTransactionManager(PlatformTransactionManager wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        /*boolean isTxActive = TransactionSynchronizationManager.isActualTransactionActive();

        if (isTxActive && MasterReplicaRoutingDataSource.isCurrentlyReadonly()
                && !definition.isReadOnly() && definition.getPropagationBehavior() != TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
            throw new CannotCreateTransactionException("Can not request RW transaction from initialized readonly transaction");
        } */

        //if (!TransactionSynchronizationManager.isActualTransactionActive()) {
//            MasterReplicaRoutingDataSource.setReadonlyDataSource(definition.isReadOnly());
        //}

        return wrapped.getTransaction(definition);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        wrapped.commit(status);
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        wrapped.rollback(status);
    }
}
