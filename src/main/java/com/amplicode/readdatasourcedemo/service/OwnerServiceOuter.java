package com.amplicode.readdatasourcedemo.service;

import com.amplicode.readdatasourcedemo.entity.Owner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OwnerServiceOuter {
    private OwnerServiceInner ownerServiceInner;

    public OwnerServiceOuter(OwnerServiceInner ownerServiceInner) {
        this.ownerServiceInner = ownerServiceInner;
    }

    @Transactional
    public List<Owner> findAll() {
        return ownerServiceInner.findAllReadOnly();
    }

    @Transactional(readOnly = true)
    public List<Owner> findAllReadOnly() {
        return ownerServiceInner.findAll();
    }

    @Transactional(readOnly = true)
    public List<Owner> findAllRequiresNew() {
        return ownerServiceInner.findAllRequiresNew();
    }
}
