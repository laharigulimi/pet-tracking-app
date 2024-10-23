package com.screening.pettrackingapp.service;

import com.screening.pettrackingapp.entity.Cat;
import com.screening.pettrackingapp.entity.Dog;
import com.screening.pettrackingapp.entity.Pet;

import java.util.List;
import java.util.Map;

public interface PetService {

    Pet savePet(Pet pet);

    List<Pet> getAllPets();

    Map<String, Long> getPetsOutsideZone();

    void deletePet(Long id);

    Pet updatePet(Long id, Pet pet);

    Pet getPetById(Long id);

    List<Pet> getPetsByOwnerId(Integer ownerId);

    List<Cat> getAllCats();

    List<Dog> getAllDogs();

    List<Cat> getLostTrackerCats();
}
