package com.example.Property_Service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        name = "property_bookmarks",
        uniqueConstraints = @UniqueConstraint(name = "uk_bookmark_user_property", columnNames = {"user_id", "property_id"}),
        indexes = {
                @Index(name = "idx_bookmark_user", columnList = "user_id"),
                @Index(name = "idx_bookmark_property", columnList = "property_id")
        }
)
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "property_id", nullable = false)
    private Long propertyId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

