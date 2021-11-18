package com.amplicode.readdatasourcedemo.repository;

import com.amplicode.readdatasourcedemo.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {
    List<Owner> findAllByFirstName(String firstName);
}