package com.screening.pettrackingapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum PetType {
    CAT, DOG
}
