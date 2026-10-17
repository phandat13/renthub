package com.renthub.listing.dto;
import com.renthub.listing.ListingStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull(message = "Thiếu trạng thái") ListingStatus status) {}

