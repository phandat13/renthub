package com.renthub.listing;

import com.renthub.common.dto.PageResponse;
import com.renthub.common.exception.*;
import com.renthub.listing.dto.*;
import com.renthub.security.SecurityUtils;
import com.renthub.security.UserPrincipal;
import com.renthub.storage.StorageService;
import com.renthub.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ListingService {
    private static final int MAX_IMAGES = 10;
    private static final int MAX_PAGE_SIZE = 50;

    private static final Map<ListingStatus, Set<ListingStatus>> OWNER_TRANSITIONS = Map.of(
            ListingStatus.ACTIVE, Set.of(ListingStatus.HIDDEN, ListingStatus.RENTED),
            ListingStatus.HIDDEN, Set.of(ListingStatus.ACTIVE),
            ListingStatus.RENTED, Set.of(ListingStatus.ACTIVE)
    );

    private final ListingRepository listingRepository;
    private final ListingImageRepository imageRepository;
    private final UserService userService;
    private final StorageService storageService;

    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> search(ListingSearchCriteria criteria, int page, int size, String sort) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE), toSort(sort));
        return PageResponse.from(listingRepository
                .findAll(ListingSpecifications.publicSearch(criteria), pageable)
                .map(ListingMapper::toSummary));
    }

    @Transactional(readOnly = true)
    public ListingResponse getDetail(Long id) {
        Listing listing = listingRepository.findWithOwnerById(id).orElseThrow(this::notFound);
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            boolean canView = SecurityUtils.currentUser()
                    .map(u -> u.isAdmin() || listing.isOwnedBy(u.getId()))
                    .orElse(false);
            if (!canView) throw notFound(); // Trả 404 để giấu tin ẩn
        }
        return ListingMapper.toResponse(listing);
    }

    @Transactional(readOnly = true)
    public PageResponse<ListingSummaryResponse> getMine(Long ownerId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.clamp(size, 1, MAX_PAGE_SIZE), Sort.by("createdAt").descending());
        return PageResponse.from(listingRepository.findByOwner_Id(ownerId, pageable).map(ListingMapper::toSummary));
    }

    @Transactional
    public ListingResponse create(Long ownerId, ListingRequest req) {
        Listing listing = new Listing();
        listing.setOwner(userService.getEntity(ownerId));
        ListingMapper.apply(req, listing);
        listing.setStatus(ListingStatus.PENDING);
        return ListingMapper.toResponse(listingRepository.save(listing));
    }

    @Transactional
    public ListingResponse update(Long id, Long userId, ListingRequest req) {
        Listing listing = getOwnedListing(id, userId);
        ListingMapper.apply(req, listing);
        if (listing.getStatus() == ListingStatus.REJECTED) {
            listing.setStatus(ListingStatus.PENDING);
            listing.setRejectReason(null);
        }
        return ListingMapper.toResponse(listing);
    }

    @Transactional
    public ListingResponse updateStatus(Long id, Long userId, ListingStatus target) {
        Listing listing = getOwnedListing(id, userId);
        if (!OWNER_TRANSITIONS.getOrDefault(listing.getStatus(), Set.of()).contains(target)) {
            throw new BadRequestException("Không thể chuyển tin từ %s sang %s".formatted(listing.getStatus(), target));
        }
        listing.setStatus(target);
        return ListingMapper.toResponse(listing);
    }

    @Transactional
    public void delete(Long id, UserPrincipal user) {
        Listing listing = listingRepository.findById(id).orElseThrow(this::notFound);
        if (!user.isAdmin() && !listing.isOwnedBy(user.getId())) {
            throw new ForbiddenException("Bạn không có quyền xoá tin này");
        }
        List<String> urls = listing.getImages().stream().map(ListingImage::getUrl).toList();
        listingRepository.delete(listing);
        deleteFilesAfterCommit(urls);
    }

    @Transactional
    public ListingResponse addImages(Long id, Long userId, List<MultipartFile> files) {
        Listing listing = getOwnedListing(id, userId);
        if (files == null || files.isEmpty()) throw new BadRequestException("Chưa chọn ảnh");
        if (listing.getImages().size() + files.size() > MAX_IMAGES) {
            throw new BadRequestException("Mỗi tin tối đa " + MAX_IMAGES + " ảnh");
        }
        int order = listing.getImages().stream().mapToInt(ListingImage::getSortOrder).max().orElse(-1) + 1;
        for (MultipartFile file : files) {
            listing.addImage(new ListingImage(storageService.store(file), order++));
        }
        listingRepository.flush();
        return ListingMapper.toResponse(listing);
    }

    @Transactional
    public void deleteImage(Long listingId, Long imageId, Long userId) {
        Listing listing = getOwnedListing(listingId, userId);
        ListingImage image = imageRepository.findByIdAndListing_Id(imageId, listingId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy ảnh"));
        listing.getImages().remove(image);
        deleteFilesAfterCommit(List.of(image.getUrl()));
    }

    // Helper Methods
    public Listing getEntity(Long id) {
        return listingRepository.findById(id).orElseThrow(this::notFound);
    }

    private Listing getOwnedListing(Long id, Long userId) {
        Listing listing = getEntity(id);
        if (!listing.isOwnedBy(userId)) throw new ForbiddenException("Bạn không phải chủ tin này");
        return listing;
    }

    private void deleteFilesAfterCommit(List<String> urls) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                urls.forEach(storageService::delete);
            }
        });
    }

    private static Sort toSort(String sort) {
        Sort primary = switch (sort == null ? "newest" : sort) {
            case "price_asc" -> Sort.by("pricePerMonth").ascending();
            case "price_desc" -> Sort.by("pricePerMonth").descending();
            case "area_desc" -> Sort.by("areaM2").descending();
            default -> Sort.by("createdAt").descending();
        };
        return primary.and(Sort.by("id").descending());
    }

    private NotFoundException notFound() {
        return new NotFoundException("Không tìm thấy tin đăng");
    }
}