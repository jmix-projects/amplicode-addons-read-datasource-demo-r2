package com.haulmont.npaddonsdemor2.repository;

import com.haulmont.npaddonsdemor2.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    List<Owner> findAllByFirstName(String firstName);
}