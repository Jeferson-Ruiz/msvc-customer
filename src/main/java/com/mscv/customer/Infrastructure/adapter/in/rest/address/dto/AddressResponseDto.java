package com.mscv.customer.Infrastructure.adapter.in.rest.address.dto;

public record AddressResponseDto(
        Long id,
        String state,
        String city,
        String fullAddress) {
}
