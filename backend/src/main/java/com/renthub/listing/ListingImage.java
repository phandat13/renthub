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
@Table(name = "listing_images")
@Getter
@Setter
@NoArgsConstructor
public class ListingImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public ListingImage(String url, int sortOrder) {
        this.url = url;
        this.sortOrder = sortOrder;
    }
}