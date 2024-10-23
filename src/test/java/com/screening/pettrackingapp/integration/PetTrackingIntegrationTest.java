package com.screening.pettrackingapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.screening.pettrackingapp.entity.*;
import com.screening.pettrackingapp.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PetTrackingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PetRepository petRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        petRepository.deleteAll(); // Cleaning up the repository before each test
    }

    @Test
    void testAddCat_Success() throws Exception {
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, false, false);

        mockMvc.perform(post("/api/pets/cat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petType").value("CAT"))
                .andExpect(jsonPath("$.trackerType").value("SMALL"));

        List<Pet> pets = petRepository.findAll();
        assertEquals(1, pets.size());
    }

    @Test
    void testAddDog_Success() throws Exception {
        Dog dog = createDog(PetType.DOG, TrackerType.BIG, 1, true);

        mockMvc.perform(post("/api/pets/dog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petType").value("DOG"))
                .andExpect(jsonPath("$.trackerType").value("BIG"));


        List<Pet> pets = petRepository.findAll();
        assertEquals(1, pets.size());
    }

    @Test
    void testAddCat_WithInvalidPetType_fails() throws Exception {
        Cat cat = createCat(PetType.DOG, TrackerType.SMALL, 1, false, false);

        mockMvc.perform(post("/api/pets/cat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cat)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testAddDog_WithMediumTracker_fails() throws Exception {
        Dog dog = createDog(PetType.DOG, TrackerType.MEDIUM, 1, true);

        mockMvc.perform(post("/api/pets/dog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dog)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetPetsOutsideZone_Success() throws Exception {
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, false, false);
        Dog dog = createDog(PetType.DOG, TrackerType.BIG, 1, true);
        petRepository.save(cat);
        petRepository.save(dog);

        mockMvc.perform(get("/api/pets/outside-zone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.CAT-SMALL").value(1));
    }

    @Test
    void testGetAllPets() throws Exception {
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, true, false);
        petRepository.save(cat);

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petType").value("CAT"));
    }

    @Test
    void testUpdatePet_Success() throws Exception {
        Cat existingCat = createCat(PetType.CAT, TrackerType.SMALL, 1, false, false);
        Pet savedCat = petRepository.save(existingCat);

        Cat updatedCat = createCat(PetType.CAT, TrackerType.BIG, 1, true, true);
        updatedCat.setPetType(PetType.CAT); // Ensure pet type is set for deserialization

        mockMvc.perform(put("/api/pets/" + savedCat.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCat)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trackerType").value("BIG"))
                .andExpect(jsonPath("$.inZone").value(true))
                .andExpect(jsonPath("$.lostTracker").value(true));

        Cat retrievedCat = (Cat) petRepository.findById(savedCat.getId()).orElse(null);
        assertNotNull(retrievedCat, "Expected the retrieved cat to be non-null");
        assertEquals(TrackerType.BIG, retrievedCat.getTrackerType());
        assertEquals(true, retrievedCat.getInZone());
        assertEquals(true, retrievedCat.getLostTracker());
    }

    @Test
    void testDeletePet_Success() throws Exception {
        Dog dog = createDog(PetType.DOG, TrackerType.BIG, 1, true);
        Pet savedDog = petRepository.save(dog);

        mockMvc.perform(delete("/api/pets/" + savedDog.getId()))
                .andExpect(status().isNoContent());

        List<Pet> pets = petRepository.findAll();
        assertEquals(0, pets.size());
    }

    @Test
    void testGetPetById_Success() throws Exception {
        Cat cat = createCat(PetType.CAT, TrackerType.SMALL, 1, false, false);
        Pet savedCat = petRepository.save(cat);

        mockMvc.perform(get("/api/pets/" + savedCat.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.petType").value("CAT"));
    }

    @Test
    void testGetPetsByOwnerId_Success() throws Exception {
        Cat cat1 = createCat(PetType.CAT, TrackerType.SMALL, 1, false, false);
        petRepository.save(cat1);

        Cat cat2 = createCat(PetType.CAT, TrackerType.BIG, 1, true, true);
        petRepository.save(cat2);

        mockMvc.perform(get("/api/pets/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petType").value("CAT"))
                .andExpect(jsonPath("$[1].petType").value("CAT"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void testGetAllCats_Success() throws Exception {
        Cat cat1 = createCat(PetType.CAT, TrackerType.SMALL, 1, false, false);
        petRepository.save(cat1);

        Cat cat2 = createCat(PetType.CAT, TrackerType.BIG, 2, true, true);
        petRepository.save(cat2);

        mockMvc.perform(get("/api/pets/cats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petType").value("CAT"))
                .andExpect(jsonPath("$[1].petType").value("CAT"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void testGetAllDogs_Success() throws Exception {
        Dog dog1 = createDog(PetType.DOG, TrackerType.BIG, 1, false);
        petRepository.save(dog1);

        Dog dog2 = createDog(PetType.DOG, TrackerType.SMALL, 2, true);
        petRepository.save(dog2);

        mockMvc.perform(get("/api/pets/dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].petType").value("DOG"))
                .andExpect(jsonPath("$[1].petType").value("DOG"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
    }

    @Test
    void testGetLostTrackers_Success() throws Exception {
        Cat lostCat = createCat(PetType.CAT, TrackerType.SMALL, 1, false, true);
        petRepository.save(lostCat);

        Cat normalCat = createCat(PetType.CAT, TrackerType.BIG, 2, true, false);
        petRepository.save(normalCat);

        mockMvc.perform(get("/api/pets/lost-trackers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lostTracker").value(true))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));
    }

    // Helper method to create cat
    private Cat createCat(PetType petType, TrackerType trackerType, int ownerId, boolean inZone, boolean lostTracker) {
        Cat cat = new Cat();
        cat.setPetType(petType);
        cat.setTrackerType(trackerType);
        cat.setOwnerId(ownerId);
        cat.setInZone(inZone);
        cat.setLostTracker(lostTracker);
        return cat;
    }

    // Helper method to create dog
    private Dog createDog(PetType petType, TrackerType trackerType, int ownerId, boolean inZone) {
        Dog dog = new Dog();
        dog.setPetType(petType);
        dog.setTrackerType(trackerType);
        dog.setOwnerId(ownerId);
        dog.setInZone(inZone);
        return dog;
    }
}