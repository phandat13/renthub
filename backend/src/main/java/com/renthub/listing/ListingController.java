package com.renthub.listing;

import com.renthub.common.dto.PageResponse;
import com.renthub.listing.dto.*;
import com.renthub.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {
    private final ListingService listingService;

    @GetMapping
    public PageResponse<ListingSummaryResponse> search(ListingSearchCriteria criteria,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "12") int size,
                                                       @RequestParam(defaultValue = "newest") String sort) {
        return listingService.search(criteria, page, size, sort);
    }

    @GetMapping("/mine")
    public PageResponse<ListingSummaryResponse> mine(@AuthenticationPrincipal UserPrincipal user,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        return listingService.getMine(user.getId(), page, size);
    }

    @GetMapping("/{id}")
    public ListingResponse detail(@PathVariable Long id) {
        return listingService.getDetail(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ListingResponse create(@AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody ListingRequest request) {
        return listingService.create(user.getId(), request);
    }

    @PutMapping("/{id}")
    public ListingResponse update(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody ListingRequest request) {
        return listingService.update(id, user.getId(), request);
    }

    @PatchMapping("/{id}/status")
    public ListingResponse updateStatus(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user, @Valid @RequestBody StatusUpdateRequest request) {
        return listingService.updateStatus(id, user.getId(), request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user) {
        listingService.delete(id, user);
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ListingResponse uploadImages(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal user, @RequestParam("file") List<MultipartFile> files) {
        return listingService.addImages(id, user.getId(), files);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable Long id, @PathVariable Long imageId, @AuthenticationPrincipal UserPrincipal user) {
        listingService.deleteImage(id, imageId, user.getId());
    }
}