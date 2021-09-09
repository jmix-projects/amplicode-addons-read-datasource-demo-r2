package com.haulmont.npaddonsdemor2.repository;

import com.haulmont.npaddonsdemor2.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    @Query("select p from Visit v left join v.pet p where v.id = 1")
    Optional<Pet> findPetByVisit(Long visitId);
}