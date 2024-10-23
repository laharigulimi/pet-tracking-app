package com.screening.pettrackingapp.controller;

import com.screening.pettrackingapp.entity.*;
import com.screening.pettrackingapp.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;

    }

    @PostMapping(value = "/cat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pet> addCat(@RequestBody Cat cat) {
        return ResponseEntity.ok(petService.savePet(cat));
    }

    @PostMapping(value = "/dog", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pet> addDog(@RequestBody Dog dog) {
        return ResponseEntity.ok(petService.savePet(dog));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/outside-zone")
    public ResponseEntity<Map<String, Long>> getPetsOutsideZone() {
        return ResponseEntity.ok(petService.getPetsOutsideZone());
    }

    @GetMapping("/lost-trackers")
    public ResponseEntity<List<Cat>> getLostTrackersPets() {
        List<Cat> lostTrackerCats = petService.getLostTrackerCats();
        return ResponseEntity.ok(lostTrackerCats);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @RequestBody Pet pet) {
        return ResponseEntity.ok(petService.updatePet(id, pet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Pet>> getPetsByOwnerId(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(petService.getPetsByOwnerId(ownerId));
    }

    @GetMapping("/cats")
    public ResponseEntity<List<Cat>> getAllCats() {
        return ResponseEntity.ok(petService.getAllCats());
    }

    @GetMapping("/dogs")
    public ResponseEntity<List<Dog>> getAllDogs() {
        return ResponseEntity.ok(petService.getAllDogs());
    }
}
