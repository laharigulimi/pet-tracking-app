package com.screening.pettrackingapp.service;

import com.screening.pettrackingapp.entity.*;
import com.screening.pettrackingapp.exception.PetValidationException;
import com.screening.pettrackingapp.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;

    @Autowired
    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Pet savePet(Pet pet) {
        validate(pet);
        return petRepository.save(pet);
    }

    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public Map<String, Long> getPetsOutsideZone() {
        return petRepository.findByInZoneFalse()
                .stream()
                .collect(Collectors.groupingBy(pet -> pet.getPetType() + "-" + pet.getTrackerType(), Collectors.counting()));
    }

    @Override
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    @Override
    public Pet updatePet(Long id, Pet pet) {
        Pet existingPet = petRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Pet with ID " + id + " not found."));

        existingPet.setPetType(pet.getPetType());
        existingPet.setTrackerType(pet.getTrackerType());
        existingPet.setOwnerId(pet.getOwnerId());
        existingPet.setInZone(pet.getInZone());

        // If updating a Cat, set lostTracker
        if (existingPet instanceof Cat && pet instanceof Cat) {
            ((Cat) existingPet).setLostTracker(((Cat) pet).getLostTracker());
        }

        return petRepository.save(existingPet);
    }

    @Override
    public Pet getPetById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pet with ID " + id + " not found."));
    }

    @Override
    public List<Pet> getPetsByOwnerId(Integer ownerId) {
        return petRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Cat> getAllCats() {
        return petRepository.findAll().stream()
                .filter(pet -> pet instanceof Cat)
                .map(pet -> (Cat) pet)
                .collect(Collectors.toList());
    }

    @Override
    public List<Dog> getAllDogs() {
        return petRepository.findAll().stream()
                .filter(pet -> pet instanceof Dog)
                .map(pet -> (Dog) pet)
                .collect(Collectors.toList());
    }

    @Override
    public List<Cat> getLostTrackerCats() {
        return petRepository.findByLostTrackerTrue();
    }

    private void validate(Pet pet) {
       if (pet instanceof Dog) {
           if (pet.getTrackerType() == TrackerType.MEDIUM) {
               throw new PetValidationException("Tracker type " + TrackerType.MEDIUM + " is not applicable for " + PetType.DOG);
           }
        }
    }

}
