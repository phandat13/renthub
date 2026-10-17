package com.renthub.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ListingImageRepository extends JpaRepository<ListingImage,Long> {
    Optional<ListingImage> findByIdAndListing_Id(Long id, Long listingId);
}