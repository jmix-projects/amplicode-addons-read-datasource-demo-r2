package com.haulmont.npaddonsdemor2.service;

import com.haulmont.npaddonsdemor2.entity.Owner;
import com.haulmont.npaddonsdemor2.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OwnerServiceInner {
    private OwnerRepository ownerRepository;

    public OwnerServiceInner(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Owner> findAllReadOnly() {
        return ownerRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Owner> findAllRequiresNew() {
        return ownerRepository.findAll();
    }
}
