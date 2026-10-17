package com.renthub.favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    Optional<Favorite> findByUser_IdAndListing_Id(Long userId,Long listingId);
    boolean existsByUser_IdAndListing_Id(Long userId,Long listingId);
    Page<Favorite> findByUser_Id(Long userId,Pageable pageable);

    @Query("select f.listing.id from Favorite f where f.user.id = :userId")
    List<Long> findListingIdsByUserId(@Param("userId") Long userId);
}