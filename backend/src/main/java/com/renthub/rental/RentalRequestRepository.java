package com.renthub.rental;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import java.util.List;

public interface RentalRequestRepository  extends JpaRepository<RentalRequest,Long> {
    boolean existsByListing_IdAndTenant_IdAndStatus(Long listingId,Long tenantId,RentalRequestStatus status);
    Page<RentalRequest> findByTenant_Id(Long tenantId, Pageable pageable);
    Page<RentalRequest> findByListing_Owner_Id(Long ownerId, Pageable pageable);
    List<RentalRequest> findByListing_IdAndStatusAndIdNot(Long listingId,RentalRequestStatus status,Long id);
}