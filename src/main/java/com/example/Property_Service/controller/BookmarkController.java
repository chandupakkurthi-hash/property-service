package com.example.Property_Service.controller;

import com.example.Property_Service.model.Property;
import com.example.Property_Service.security.SecurityUtil;
import com.example.Property_Service.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final PropertyService propertyService;

    @PostMapping("/toggle/{propertyId}")
    public ResponseEntity<Map<String, Object>> toggle(@PathVariable Long propertyId,
                                                      @RequestParam("userId") Long userId) {
        Long requesterUserId = SecurityUtil.currentUserIdOrNull();
        if (requesterUserId == null || !requesterUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        boolean bookmarked = propertyService.toggleBookmark(userId, propertyId);
        return ResponseEntity.ok(Map.of("success", true, "bookmarked", bookmarked));
    }

    @GetMapping("/{userId}/ids")
    public ResponseEntity<List<Long>> ids(@PathVariable Long userId) {
        Long requesterUserId = SecurityUtil.currentUserIdOrNull();
        if (requesterUserId == null || !requesterUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return ResponseEntity.ok(propertyService.getBookmarkedPropertyIds(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Property>> properties(@PathVariable Long userId) {
        Long requesterUserId = SecurityUtil.currentUserIdOrNull();
        if (requesterUserId == null || !requesterUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        return ResponseEntity.ok(propertyService.getBookmarkedProperties(userId));
    }

    @DeleteMapping("/{userId}/{propertyId}")
    public ResponseEntity<Map<String, Object>> remove(@PathVariable Long userId, @PathVariable Long propertyId) {
        Long requesterUserId = SecurityUtil.currentUserIdOrNull();
        if (requesterUserId == null || !requesterUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        propertyService.removeBookmark(userId, propertyId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
