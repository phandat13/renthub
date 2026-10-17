package com.renthub.listing.dto;

import com.renthub.listing.ListingStatus;
import com.renthub.listing.PropertyType;
import java.math.BigDecimal;
import java.time.Instant;

public record ListingSummaryResponse(
        Long id, String title, PropertyType propertyType, Long pricePerMonth,
        BigDecimal areaM2, Integer bedrooms, Integer bathrooms, String district,
        String city, String coverImageUrl, ListingStatus status, Instant createdAt
) {}