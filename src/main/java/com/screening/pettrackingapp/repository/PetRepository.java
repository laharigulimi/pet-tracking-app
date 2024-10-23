package com.screening.pettrackingapp.repository;

import com.screening.pettrackingapp.entity.Cat;
import com.screening.pettrackingapp.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByInZoneFalse();

    List<Pet> findByOwnerId(Integer ownerId);

    List<Cat> findByLostTrackerTrue();
}
