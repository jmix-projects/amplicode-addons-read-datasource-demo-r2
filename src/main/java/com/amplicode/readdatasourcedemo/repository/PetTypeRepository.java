package com.amplicode.readdatasourcedemo.repository;

import com.amplicode.readdatasourcedemo.entity.PetType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetTypeRepository extends JpaRepository<PetType, Long> {
}