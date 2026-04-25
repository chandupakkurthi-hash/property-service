package com.example.Property_Service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "address", indexes = {
        @Index(name = "idx_address_city", columnList = "city")
})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    private String city;
    private String locality;
    private String landmark;
    private Double latitude;
    private Double longitude;
}
