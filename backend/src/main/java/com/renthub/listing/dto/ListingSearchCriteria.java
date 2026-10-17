package com.renthub.listing.dto;

import com.renthub.listing.PropertyType;
import java.math.BigDecimal;

public record ListingSearchCriteria(
        String keyword, String city, String district, PropertyType propertyType,
        Long minPrice, Long maxPrice, BigDecimal minArea, BigDecimal maxArea,
        Integer minBedrooms
) {}