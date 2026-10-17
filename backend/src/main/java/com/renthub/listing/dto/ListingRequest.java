package com.renthub.listing.dto;

import com.renthub.listing.PropertyType;
import jakarta.validation.constraints.*;
import org.aspectj.bridge.IMessage;

import java.math.BigDecimal;
import java.util.Set;

public record ListingRequest(
        @NotBlank(message = "Tiêu đề không được để trống") @Size(max = 200)
        String title,

        @NotBlank(message = "Mô tả không được để trống") @Size(max = 5000)
        String description,

        @NotNull(message = "Chọn loại nhà")
        PropertyType propertyType,

        @NotNull(message = "Nhập giá thuê") @Positive(message = "Giá phải lớn hơn 0")
        Long pricePerMonth,

        @NotNull(message = "Nhập diện tích") @DecimalMin(value = "1.0", message = "Diện tích tối thiểu 1 m²")
        @Digits(integer = 6, fraction = 2)
        BigDecimal areaM2,

        @NotNull @Min(0) @Max(20)
        Integer bedrooms,

        @NotNull @Min(0) @Max(20)
        Integer bathrooms,

        @NotBlank(message = "Nhập địa chỉ")
        @Size(max = 255)
        String address,

        @NotBlank(message = "Nhập quận/khu vực")
        @Size(max = 100)
        String district,

        @NotBlank(message = "Chọn thành phố")
        @Size(max = 100)
        String city,

        @Size(max = 20)
        Set<@NotBlank @Size(max = 50) String> amenities)
{}