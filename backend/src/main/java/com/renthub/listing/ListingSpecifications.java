package com.renthub.listing;

import com.renthub.listing.dto.ListingSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

public final class ListingSpecifications {
    private ListingSpecifications() {}

    public static Specification<Listing> publicSearch(ListingSearchCriteria c) {
        List<Specification<Listing>> specs = new ArrayList<>();
        specs.add((root, q, cb) -> cb.equal(root.get("status"), ListingStatus.ACTIVE));

        if (hasText(c.keyword())) {
            String p = "%" + c.keyword().trim().toLowerCase() + "%";
            specs.add((root, q, cb) -> cb.or(
                    cb.like(cb.lower(root.<String>get("title")), p),
                    cb.like(cb.lower(root.<String>get("address")), p),
                    cb.like(cb.lower(root.<String>get("district")), p)));
        }
        if (hasText(c.city())) {
            String city = c.city().toLowerCase();
            specs.add((root, q, cb) -> cb.equal(cb.lower(root.<String>get("city")), city));
        }
        if (hasText(c.district())) {
            String district = c.district().toLowerCase();
            specs.add((root, q, cb) -> cb.equal(cb.lower(root.<String>get("district")), district));
        }
        if (c.propertyType() != null) {
            specs.add((root, q, cb) -> cb.equal(root.get("propertyType"), c.propertyType()));
        }
        if (c.minPrice() != null) {
            specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.<Long>get("pricePerMonth"), c.minPrice()));
        }
        if (c.maxPrice() != null) {
            specs.add((root, q, cb) -> cb.lessThanOrEqualTo(root.<Long>get("pricePerMonth"), c.maxPrice()));
        }
        if (c.minArea() != null) {
            specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.<BigDecimal>get("areaM2"), c.minArea()));
        }
        if (c.maxArea() != null) {
            specs.add((root, q, cb) -> cb.lessThanOrEqualTo(root.<BigDecimal>get("areaM2"), c.maxArea()));
        }
        if (c.minBedrooms() != null) {
            specs.add((root, q, cb) -> cb.greaterThanOrEqualTo(root.<Integer>get("bedrooms"), c.minBedrooms()));
        }
        return Specification.allOf(specs);
    }

    private static boolean hasText(String s) {
        return s != null && !s.isEmpty();
    }
}