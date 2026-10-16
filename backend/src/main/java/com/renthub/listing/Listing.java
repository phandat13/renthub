package com.renthub.listing;

import com.renthub.common.BaseEntity;
import com.renthub.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "listings")
@Getter
@Setter
@NoArgsConstructor
public class Listing extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false, length = 20)
    private PropertyType propertyType;

    @Column(name = "price_per_month", nullable = false)
    private Long pricePerMonth;

    @Column(name = "area_m2", nullable = false, precision = 8, scale = 2)
    private BigDecimal areaM2;

    @Column(nullable = false)
    private Integer bedrooms = 0;

    @Column(nullable = false)
    private Integer bathrooms = 0;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(nullable = false, length = 100)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ListingStatus status = ListingStatus.PENDING;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ListingImage> images = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "listing_amenities", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "amenity", length = 50)
    private Set<String> amenities = new HashSet<>();

    public void addImage(ListingImage image) {
        image.setListing(this);
        images.add(image);
    }

    public boolean isOwnedBy(Long userId) {
        return userId != null && userId.equals(this.getOwner().getId());
    }
}