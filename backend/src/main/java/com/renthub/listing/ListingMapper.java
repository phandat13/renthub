package com.renthub.listing;

import com.renthub.listing.dto.*;
import java.util.*;

public final class ListingMapper {
    private ListingMapper() {}

    public static ListingSummaryResponse toSummary(Listing l) {
        String cover = l.getImages().isEmpty() ? null : l.getImages().get(0).getUrl();
        return new ListingSummaryResponse(l.getId(), l.getTitle(), l.getPropertyType(), l.getPricePerMonth(),
                l.getAreaM2(), l.getBedrooms(), l.getBathrooms(), l.getDistrict(), l.getCity(),
                cover, l.getStatus(), l.getCreatedAt());
    }

    public static ListingResponse toResponse(Listing l) {
        List<ImageResponse> images = l.getImages().stream()
                .map(i -> new ImageResponse(i.getId(), i.getUrl(), i.getSortOrder()))
                .toList();
        var o = l.getOwner();
        return new ListingResponse(l.getId(), l.getTitle(), l.getDescription(), l.getPropertyType(),
                l.getPricePerMonth(), l.getAreaM2(), l.getBedrooms(), l.getBathrooms(), l.getAddress(),
                l.getDistrict(), l.getCity(), l.getStatus(), l.getRejectReason(),
                new TreeSet<>(l.getAmenities()), images,
                new OwnerResponse(o.getId(), o.getFullName(), o.getPhone(), o.getAvatarUrl()),
                l.getCreatedAt(), l.getUpdatedAt());
    }

    public static void apply(ListingRequest r, Listing l) {
        l.setTitle(r.title().trim());
        l.setDescription(r.description().trim());
        l.setPropertyType(r.propertyType());
        l.setPricePerMonth(r.pricePerMonth());
        l.setAreaM2(r.areaM2());
        l.setBedrooms(r.bedrooms());
        l.setBathrooms(r.bathrooms());
        l.setAddress(r.address().trim());
        l.setDistrict(r.district().trim());
        l.setCity(r.city().trim());
        l.getAmenities().clear();
        if (r.amenities() != null) {
            r.amenities().forEach(a -> l.getAmenities().add(a.trim().toLowerCase()));
        }
    }
}