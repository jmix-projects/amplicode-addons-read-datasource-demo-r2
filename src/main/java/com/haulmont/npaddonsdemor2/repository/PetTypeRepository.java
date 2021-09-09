package com.haulmont.npaddonsdemor2.repository;

import com.haulmont.npaddonsdemor2.entity.PetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetTypeRepository extends JpaRepository<PetType, Long> {
}