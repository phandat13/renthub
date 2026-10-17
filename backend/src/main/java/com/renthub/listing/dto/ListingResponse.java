package com.renthub.listing.dto;

import com.renthub.listing.ListingStatus;
import com.renthub.listing.PropertyType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public record ListingResponse(
        Long id, String title, String description, PropertyType propertyType,
        Long pricePerMonth, BigDecimal areaM2, Integer bedrooms, Integer bathrooms,
        String address, String district, String city, ListingStatus status,
        String rejectReason, Set<String> amenities, List<ImageResponse> images,
        OwnerResponse owner, Instant createdAt, Instant updatedAt
) {}