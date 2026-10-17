package  com.renthub.rental;

import com.renthub.common.BaseEntity;
import com.renthub.listing.Listing;
import com.renthub.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "rental_requests")
@Getter
@Setter
@NoArgsConstructor
public class RentalRequest extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @Column(length = 1000)
    private String message;

    @Column(name = "move_in_date", nullable = false)
    private LocalDate moveInDate;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RentalRequestStatus status = RentalRequestStatus.PENDING;
}
