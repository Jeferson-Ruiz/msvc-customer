package com.mscv.customer.Infrastructure.adapter.in.rest.address.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressRequestDto(
        @NotBlank(message = "El municipio no puede estar vacío") String state,
        @NotBlank(message = "La ciudad no puede estar vacía") String city,
        @NotBlank(message = "La dirección completa no puede estar vacía") String fullAddress) {
}
