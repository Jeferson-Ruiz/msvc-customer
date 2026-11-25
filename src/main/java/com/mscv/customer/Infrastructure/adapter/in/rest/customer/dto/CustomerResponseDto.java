package com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto;

import java.util.List;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressResponseDto;

public record CustomerResponseDto(
        Long id,
        String name,
        String lastName,
        String documentNumber,
        String documentType,
        int age,
        boolean active,
        List<AddressResponseDto> addresses) {
}
