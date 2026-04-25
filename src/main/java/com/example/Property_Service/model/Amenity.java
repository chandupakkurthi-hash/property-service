package com.example.Property_Service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amenityId;

    private int bathrooms;
    private Integer balcony;
    private String waterSupply;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean petAllowed = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean gym = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean nonVeg = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean gatedSecurity = false;

    private String showProperty;
    private String propertyCondition;
    private String secondaryNumber;
    private String nearByPlace;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean lift = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean gasPipeLine = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean airConditioner = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean park = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean houseKeeping = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean internetService = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean powerBackUp = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean serventRoom = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean swimmingPool = false;
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean fireSafety = false;
}
