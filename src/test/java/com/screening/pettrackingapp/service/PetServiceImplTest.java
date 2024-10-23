package com.screening.pettrackingapp.service;

import com.screening.pettrackingapp.entity.*;
import com.screening.pettrackingapp.exception.PetValidationException;
import com.screening.pettrackingapp.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceImplTest {

    @Mock
    PetRepository petRepository;

    @InjectMocks
    PetServiceImpl petService;

    @Test
    void testSaveCat_Success() {
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, true, false);
        when(petRepository.save(cat)).thenReturn(cat);

        Pet savedCat = petService.savePet(cat);

        assertEquals(cat, savedCat);
        verify(petRepository, times(1)).save(cat);
    }

    @Test
    void testSaveDog_Success() {
        Dog dog = createDog(PetType.DOG, TrackerType.BIG, 2, false);
        when(petRepository.save(dog)).thenReturn(dog);

        Pet savedDog = petService.savePet(dog);

        assertEquals(dog, savedDog);
        verify(petRepository, times(1)).save(dog);
    }

    @Test
    void testGetAllPets_Success() {
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, true, false);
        Dog dog = createDog(PetType.DOG, TrackerType.BIG, 2, false);
        when(petRepository.findAll()).thenReturn(Arrays.asList(cat, dog));

        List<Pet> pets = petService.getAllPets();

        assertEquals(2, pets.size());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    void testGetPetsOutsideZone_Success() {
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, false, false);
        Dog dog = createDog(PetType.DOG, TrackerType.BIG, 2, false);
        when(petRepository.findByInZoneFalse()).thenReturn(Arrays.asList(cat, dog));

        Map<String, Long> result = petService.getPetsOutsideZone();

        assertEquals(2, result.size());
        assertEquals(1L, result.get("CAT-SMALL"));
        assertEquals(1L, result.get("DOG-BIG"));
        verify(petRepository, times(1)).findByInZoneFalse();
    }

    @Test
    void testValidateDog_WithInvalidTrackerType_ThrowsException() {
        Dog invalidDog = createDog(PetType.DOG, TrackerType.MEDIUM, 2, true);

        PetValidationException exception = assertThrows(PetValidationException.class, () -> petService.savePet(invalidDog));
        assertEquals("Tracker type MEDIUM is not applicable for DOG", exception.getMessage());
    }

    @Test
    void testDeletePet_Success() {
        Long petId = 1L;
        petService.deletePet(petId);
        verify(petRepository, times(1)).deleteById(petId);
    }

    @Test
    void testGetPetById_Success() {
        Long petId = 1L;
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, true, false);
        cat.setId(petId);

        when(petRepository.findById(petId)).thenReturn(Optional.of(cat));

        Pet foundPet = petService.getPetById(petId);

        assertEquals(cat, foundPet);
        verify(petRepository, times(1)).findById(petId);
    }

    @Test
    void testGetPetById_ThrowsEntityNotFoundException() {
        Long petId = 1L;

        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> petService.getPetById(petId));
        assertEquals("Pet with ID " + petId + " not found.", exception.getMessage());
        verify(petRepository, times(1)).findById(petId);
    }

    @Test
    void testGetLostTrackerCats_Success() {
        Cat catWithLostTracker = new Cat();
        catWithLostTracker.setLostTracker(true);

        when(petRepository.findByLostTrackerTrue()).thenReturn(List.of(catWithLostTracker));

        List<Cat> lostTrackerCats = petService.getLostTrackerCats();

        assertEquals(1, lostTrackerCats.size());
        assertTrue(lostTrackerCats.getFirst().getLostTracker());
        verify(petRepository, times(1)).findByLostTrackerTrue();
    }

    @Test
    void testUpdatePet_Success() {
        Long petId = 1L;
        Cat existingCat = createCat(PetType.CAT, TrackerType.SMALL, 1, true, false);
        existingCat.setId(petId);

        Cat updatedCat = createCat(PetType.CAT, TrackerType.BIG, 2, false, true);

        when(petRepository.findById(petId)).thenReturn(Optional.of(existingCat));

        when(petRepository.save(any(Cat.class))).thenAnswer(invocation -> {
            Cat catToUpdate = invocation.getArgument(0);
            existingCat.setPetType(catToUpdate.getPetType());
            existingCat.setTrackerType(catToUpdate.getTrackerType());
            existingCat.setOwnerId(catToUpdate.getOwnerId());
            existingCat.setInZone(catToUpdate.getInZone());
            existingCat.setLostTracker(catToUpdate.getLostTracker());
            return existingCat;
        });

        Pet updatedPet = petService.updatePet(petId, updatedCat);
        assertInstanceOf(Cat.class, updatedPet);

        // Cast updatedPet to Cat to access the lostTracker field
        Cat updatedCatResult = (Cat) updatedPet;

        assertEquals(updatedCat.getPetType(), updatedCatResult.getPetType());
        assertEquals(updatedCat.getTrackerType(), updatedCatResult.getTrackerType());
        assertEquals(updatedCat.getOwnerId(), updatedCatResult.getOwnerId());
        assertEquals(updatedCat.getInZone(), updatedCatResult.getInZone());
        assertEquals(updatedCat.getLostTracker(), updatedCatResult.getLostTracker());

        verify(petRepository, times(1)).findById(petId);
        verify(petRepository, times(1)).save(existingCat);
    }

    @Test
    void testGetPetsByOwnerId_Success() {
        Integer ownerId = 1;

        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, ownerId, true, false);
        Dog dog = createDog(PetType.DOG, TrackerType.BIG, ownerId, false);
        Dog anotherDog = createDog(PetType.DOG, TrackerType.SMALL, 2, true);

        List<Pet> petsByOwner = Arrays.asList(cat, dog);
        when(petRepository.findByOwnerId(ownerId)).thenReturn(petsByOwner);

        // When: Getting pets by owner ID
        List<Pet> pets = petService.getPetsByOwnerId(ownerId);

        // Then: Assert that the returned list contains only pets for the specified owner ID
        assertEquals(2, pets.size());
        assertTrue(pets.contains(cat));
        assertTrue(pets.contains(dog));
        assertFalse(pets.contains(anotherDog));

        // Verify repository interaction
        verify(petRepository, times(1)).findByOwnerId(ownerId);
    }

    @Test
    void testGetAllCats_Success() {
        Cat cat1 = createCat(PetType.CAT, TrackerType.SMALL, 1, true, false);
        Cat cat2 = createCat(PetType.CAT, TrackerType.BIG, 2, false, true);
        Dog dog = createDog(PetType.DOG, TrackerType.MEDIUM, 3, true);

        when(petRepository.findAll()).thenReturn(Arrays.asList(cat1, cat2, dog));

        List<Cat> cats = petService.getAllCats();

        assertEquals(2, cats.size());
        assertTrue(cats.contains(cat1));
        assertTrue(cats.contains(cat2));

        verify(petRepository, times(1)).findAll();
    }
    @Test
    void testGetAllDogs_Success() {
        Dog dog1 = createDog(PetType.DOG, TrackerType.BIG, 1, true);
        Dog dog2 = createDog(PetType.DOG, TrackerType.SMALL, 2, false);
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 3, true, false);

        when(petRepository.findAll()).thenReturn(Arrays.asList(dog1, dog2, cat));

        List<Dog> dogs = petService.getAllDogs();

        assertEquals(2, dogs.size());
        assertTrue(dogs.contains(dog1));
        assertTrue(dogs.contains(dog2));

        verify(petRepository, times(1)).findAll();
    }

    // Helper method to create a Cat object
    private Cat createCat(PetType petType, TrackerType trackerType, Integer ownerId, Boolean inZone, Boolean lostTracker) {
        Cat cat = new Cat();
        cat.setPetType(petType);
        cat.setTrackerType(trackerType);
        cat.setOwnerId(ownerId);
        cat.setInZone(inZone);
        cat.setLostTracker(lostTracker);
        return cat;
    }

    // Helper method to create a Dog object
    private Dog createDog(PetType petType, TrackerType trackerType, Integer ownerId, Boolean inZone) {
        Dog dog = new Dog();
        dog.setPetType(petType);
        dog.setTrackerType(trackerType);
        dog.setOwnerId(ownerId);
        dog.setInZone(inZone);
        return dog;
    }

}