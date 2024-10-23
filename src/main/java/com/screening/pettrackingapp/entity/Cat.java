package com.screening.pettrackingapp.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
public class Cat extends Pet {
    private Boolean lostTracker;

    public Cat() {
        super();
        setPetType(PetType.CAT);
    }
}
