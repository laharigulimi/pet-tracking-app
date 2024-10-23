package com.screening.pettrackingapp.entity;

import jakarta.persistence.Entity;

@Entity
public class Dog extends Pet{

    public Dog() {
        super();
        setPetType(PetType.DOG);
    }
}
