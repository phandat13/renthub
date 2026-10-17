package com.renthub.favorite;

import com.renthub.common.BaseEntity;
import com.renthub.listing.Listing;
import com.renthub.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorites")
@Getter
@NoArgsConstructor
public class Favorite extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    public Favorite(User user, Listing listing) {
        this.user = user;
        this.listing = listing;
    }
}