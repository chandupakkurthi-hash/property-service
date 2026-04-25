package com.example.Property_Service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.Property_Service.dto.PropertyRequest;
import com.example.Property_Service.model.Property;
import com.example.Property_Service.security.SecurityUtil;
import com.example.Property_Service.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;

    @GetMapping("/{id}")
    public ResponseEntity<Property> getProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Property>> getByOwner(@PathVariable Long ownerId) {
        Long requesterUserId = SecurityUtil.currentUserIdOrNull();
        if (requesterUserId == null || !requesterUserId.equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return ResponseEntity.ok(propertyService.getPropertiesByOwner(ownerId));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Property> saveProperty(
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        try {
            Long requesterUserId = SecurityUtil.currentUserIdOrNull();
            if (requesterUserId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT");
            }
            PropertyRequest request = objectMapper.readValue(propertyJson, PropertyRequest.class);
            request.setPropertyId(null);
            return ResponseEntity.ok(propertyService.savePropertyWithImages(request, images, requesterUserId));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid property JSON", e);
        }
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Long id,
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        try {
            Long requesterUserId = SecurityUtil.currentUserIdOrNull();
            if (requesterUserId == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT");
            }
            PropertyRequest request = objectMapper.readValue(propertyJson, PropertyRequest.class);
            request.setPropertyId(id);
            return ResponseEntity.ok(propertyService.savePropertyWithImages(request, images, requesterUserId));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid property JSON", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(@PathVariable Long id) {
        Long requesterUserId = SecurityUtil.currentUserIdOrNull();
        if (requesterUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT");
        }
        propertyService.deleteById(id, requesterUserId);
        return ResponseEntity.ok("Property deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Property>> searchProperties(
            @RequestParam("isSale") boolean isSale,
            @RequestParam("city") String city,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) List<Integer> bhkType,
            @RequestParam(required = false) String propertyStatus,
            @RequestParam(required = false) List<String> furnishing,
            @RequestParam(required = false) List<String> propertyType,
            @RequestParam(required = false) List<String> parking,
            @RequestParam(required = false) Integer propertyAge,
            @RequestParam(required = false) Double minBuiltUpArea,
            @RequestParam(required = false) Double maxBuiltUpArea,
            @RequestParam(defaultValue = "0") Long minRent,
            @RequestParam(defaultValue = "1000000000") Long maxRent,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return ResponseEntity.ok(propertyService.searchProperties(
                isSale, city, keyword, bhkType, propertyStatus, furnishing,
                propertyType, parking, propertyAge, minBuiltUpArea, maxBuiltUpArea,
                minRent, maxRent, sortBy, page, size));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Property-Service is UP");
    }
}
