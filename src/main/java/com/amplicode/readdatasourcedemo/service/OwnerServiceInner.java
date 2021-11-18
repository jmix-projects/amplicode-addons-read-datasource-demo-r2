package com.amplicode.readdatasourcedemo.service;

import com.amplicode.readdatasourcedemo.entity.Owner;
import com.amplicode.readdatasourcedemo.repository.OwnerRepository;
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

    @Transactional
    public Owner saveAndGet(Owner owner) {
        ownerRepository.saveAndFlush(owner);
        return ownerRepository.findById(owner.getId()).orElse(null);
    }

    @Transactional(readOnly = true)
    public Owner saveAndGetReadOnly(Owner owner) {
        ownerRepository.saveAndFlush(owner);
        return ownerRepository.findById(owner.getId()).orElse(null);
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
