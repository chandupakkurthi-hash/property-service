package com.example.Property_Service.repository;

import com.example.Property_Service.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndPropertyId(Long userId, Long propertyId);

    @Transactional
    void deleteByUserIdAndPropertyId(Long userId, Long propertyId);

    @Query("select distinct b.propertyId from Bookmark b where b.userId = :userId")
    List<Long> findPropertyIdsByUserId(@Param("userId") Long userId);
}
