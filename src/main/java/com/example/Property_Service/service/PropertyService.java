package com.example.Property_Service.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Property_Service.dto.PropertyRequest;
import com.example.Property_Service.model.Address;
import com.example.Property_Service.model.Amenity;
import com.example.Property_Service.model.Bookmark;
import com.example.Property_Service.model.Image;
import com.example.Property_Service.model.Property;
import com.example.Property_Service.repository.BookmarkRepository;
import com.example.Property_Service.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final Cloudinary cloudinary;
    private final BookmarkRepository bookmarkRepository;

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
    }

    public List<Property> getPropertiesByOwner(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId);
    }

    public Property savePropertyWithImages(PropertyRequest req, MultipartFile[] images, Long requesterUserId) {
        Property property;
        if (req.getPropertyId() != null) {
            property = propertyRepository.findById(req.getPropertyId())
                    .orElseThrow(() -> new RuntimeException("Property not found: " + req.getPropertyId()));
            if (property.getOwnerId() == null || !property.getOwnerId().equals(requesterUserId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
            }
        } else {
            property = new Property();
            property.setOwnerId(requesterUserId);
        }

        Address address = property.getAddress() != null ? property.getAddress() : new Address();
        address.setCity(req.getCity());
        address.setLocality(req.getLocality());
        address.setLandmark(req.getLandmark());
        address.setLatitude(req.getLatitude());
        address.setLongitude(req.getLongitude());

        Amenity amenity = property.getAmenity() != null ? property.getAmenity() : new Amenity();
        if (req.getBathrooms() != null) amenity.setBathrooms(req.getBathrooms());
        amenity.setBalcony(req.getBalcony());
        amenity.setWaterSupply(req.getWaterSupply());
        amenity.setPetAllowed(Boolean.TRUE.equals(req.getPetAllowed()));
        amenity.setGym(Boolean.TRUE.equals(req.getGym()));
        amenity.setNonVeg(Boolean.TRUE.equals(req.getNonVeg()));
        amenity.setGatedSecurity(Boolean.TRUE.equals(req.getGatedSecurity()));
        amenity.setShowProperty(req.getShowProperty());
        amenity.setPropertyCondition(req.getPropertyCondition());
        amenity.setSecondaryNumber(req.getSecondaryNumber());
        amenity.setNearByPlace(req.getNearByPlace());
        amenity.setLift(Boolean.TRUE.equals(req.getLift()));
        amenity.setGasPipeLine(Boolean.TRUE.equals(req.getGasPipeLine()));
        amenity.setAirConditioner(Boolean.TRUE.equals(req.getAirConditioner()));
        amenity.setPark(Boolean.TRUE.equals(req.getPark()));
        amenity.setHouseKeeping(Boolean.TRUE.equals(req.getHouseKeeping()));
        amenity.setInternetService(Boolean.TRUE.equals(req.getInternetService()));
        amenity.setPowerBackUp(Boolean.TRUE.equals(req.getPowerBackUp()));
        amenity.setServentRoom(Boolean.TRUE.equals(req.getServentRoom()));
        amenity.setSwimmingPool(Boolean.TRUE.equals(req.getSwimmingPool()));
        amenity.setFireSafety(Boolean.TRUE.equals(req.getFireSafety()));

        property.setApartmentType(req.getApartmentType());
        property.setApartmentName(req.getApartmentName());
        property.setBhkType(req.getBhkType());
        property.setFloor(req.getFloor());
        property.setTotalFloors(req.getTotalFloors());
        property.setPropertyAge(req.getPropertyAge());
        property.setFacing(req.getFacing());
        property.setBuiltUpArea(req.getBuiltUpArea());
        property.setAvailableFor(req.getAvailableFor());
        property.setExpectedRent(req.getExpectedRent() != null ? req.getExpectedRent() : 0L);
        property.setExpectedDeposit(req.getExpectedDeposit() != null ? req.getExpectedDeposit() : 0L);
        property.setMonthlyMaintenance(req.getMonthlyMaintenance());
        property.setPreferredTenets(req.getPreferredTenets());
        property.setNegotiation(req.getNegotiation());
        property.setAvailableFrom(req.getAvailableFrom());
        property.setFurnishing(req.getFurnishing());
        property.setParking(req.getParking());
        property.setPropertyStatus(req.getPropertyStatus());
        property.setPrice(req.getPrice());
        property.setIsSale(req.getIsSale());
        property.setDescription(req.getDescription());
        property.setAddress(address);
        property.setAmenity(amenity);

        if (images != null) {
            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) continue;
                try {
                    BufferedImage originalImage = ImageIO.read(file.getInputStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Thumbnails.of(originalImage).size(1280, 720).outputFormat("jpg").outputQuality(0.8).toOutputStream(out);
                    Map<?, ?> result = cloudinary.uploader().upload(out.toByteArray(), ObjectUtils.asMap("resource_type", "image"));
                    String url = result.get("secure_url").toString();
                    Image image = new Image();
                    image.setImageUrl(url);
                    image.setProperty(property);
                    property.getPhotos().add(image);
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed: " + e.getMessage());
                }
            }
        }

        return propertyRepository.save(property);
    }

    public Page<Property> searchProperties(boolean isSale, String city, String keyword,
                                           List<Integer> bhkType, String propertyStatus,
                                           List<String> furnishing, List<String> propertyType,
                                           List<String> parking, Integer propertyAge,
                                           Double minBuiltUpArea, Double maxBuiltUpArea,
                                           Long minRent, Long maxRent,
                                           String sortBy, int page, int size) {
        keyword = (keyword == null || keyword.trim().isEmpty()) ? "" : keyword;
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sortBy != null) {
            switch (sortBy) {
                case "oldest" -> sort = Sort.by(Sort.Direction.ASC, "createdAt");
                case "priceHighLow" -> sort = Sort.by(Sort.Direction.DESC, isSale ? "price" : "expectedRent");
                case "priceLowHigh" -> sort = Sort.by(Sort.Direction.ASC, isSale ? "price" : "expectedRent");
            }
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return propertyRepository.searchProperties(isSale, city, keyword.toLowerCase(),
                bhkType, furnishing, parking, propertyType, propertyAge, propertyStatus,
                minBuiltUpArea, maxBuiltUpArea, minRent, maxRent, pageable);
    }

    public void deleteById(Long propertyId) {
        throw new UnsupportedOperationException("Use deleteById(propertyId, requesterUserId)");
    }

    public void deleteById(Long propertyId, Long requesterUserId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found: " + propertyId));
        if (property.getOwnerId() == null || !property.getOwnerId().equals(requesterUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }
        propertyRepository.deleteById(propertyId);
    }

    @Transactional
    public boolean toggleBookmark(Long userId, Long propertyId) {
        Bookmark existing = bookmarkRepository.findByUserIdAndPropertyId(userId, propertyId).orElse(null);
        if (existing != null) {
            bookmarkRepository.deleteByUserIdAndPropertyId(userId, propertyId);
            return false;
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setPropertyId(propertyId);
        bookmarkRepository.save(bookmark);
        return true;
    }

    @Transactional
    public void removeBookmark(Long userId, Long propertyId) {
        bookmarkRepository.deleteByUserIdAndPropertyId(userId, propertyId);
    }

    public List<Long> getBookmarkedPropertyIds(Long userId) {
        return bookmarkRepository.findPropertyIdsByUserId(userId);
    }

    public List<Property> getBookmarkedProperties(Long userId) {
        List<Long> ids = getBookmarkedPropertyIds(userId);
        if (ids.isEmpty()) {
            return List.of();
        }
        return propertyRepository.findAllById(ids);
    }
}
