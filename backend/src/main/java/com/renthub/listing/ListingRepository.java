package com.renthub.listing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.Optional;

public interface ListingRepository extends JpaRepository<Listing,Long>, JpaSpecificationExecutor<Listing> {
    Page<Listing> findByOwner_Id(Long ownerId, Pageable pageable);
    Page<Listing> findByStatus(Listing status, Pageable pageable);

    @EntityGraph(attributePaths = {"owner"})
    Optional<Listing> findWithOwnerById(Long ownerId);
}