package com.example.Property_Service.repository;

import com.example.Property_Service.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT p FROM Property p " +
            "JOIN p.address a " +
            "WHERE p.isSale = :isSale " +
            "AND LOWER(a.city) = LOWER(:city) " +
            "AND (" +
            "LOWER(a.locality) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.landmark) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.apartmentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.facing) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(p.bhkType AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(CASE WHEN p.isSale = true THEN p.price ELSE p.expectedRent END AS string) LIKE CONCAT('%', :keyword, '%')" +
            ") " +
            "AND (:bhkType IS NULL OR p.bhkType IN :bhkType) " +
            "AND (:furnishing IS NULL OR p.furnishing IN :furnishing) " +
            "AND (:parking IS NULL OR p.parking IN :parking) " +
            "AND (:apartmentType IS NULL OR p.apartmentType IN :apartmentType) " +
            "AND (:propertyAge IS NULL OR p.propertyAge <= :propertyAge) " +
            "AND (:propertyStatus IS NULL OR :propertyStatus = '' OR p.propertyStatus = :propertyStatus) " +
            "AND (:minBuiltUpArea IS NULL OR p.builtUpArea >= :minBuiltUpArea) " +
            "AND (:maxBuiltUpArea IS NULL OR p.builtUpArea <= :maxBuiltUpArea) " +
            "AND (:minRent IS NULL OR " +
            "    (CASE WHEN :isSale = true THEN p.price ELSE p.expectedRent END) >= :minRent) " +
            "AND (:maxRent IS NULL OR " +
            "    (CASE WHEN :isSale = true THEN p.price ELSE p.expectedRent END) <= :maxRent)")
    Page<Property> searchProperties(
            @Param("isSale") boolean isSale,
            @Param("city") String city,
            @Param("keyword") String keyword,
            @Param("bhkType") List<Integer> bhkType,
            @Param("furnishing") List<String> furnishing,
            @Param("parking") List<String> parking,
            @Param("apartmentType") List<String> apartmentType,
            @Param("propertyAge") Integer propertyAge,
            @Param("propertyStatus") String propertyStatus,
            @Param("minBuiltUpArea") Double minBuiltUpArea,
            @Param("maxBuiltUpArea") Double maxBuiltUpArea,
            @Param("minRent") Long minRent,
            @Param("maxRent") Long maxRent,
            Pageable pageable);

    List<Property> findByOwnerId(Long ownerId);
}
