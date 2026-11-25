package com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto;

import java.util.List;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CustomerRequestDto(
        @NotBlank(message = "El nombre no puede estar vacío") String name,
        @NotBlank(message = "El apellido no puede estar vacío") String lastName,
        @NotBlank(message = "El número de documento no puede estar vacío") String documentNumber,
        @NotBlank(message = "El tipo de documento no puede estar vacío") String documentType,
        @NotNull(message = "La edad es obligatoria")
        @PositiveOrZero(message = "La edad no puede ser negativa") Integer age,
        boolean active,
        @Valid List<AddressRequestDto> addresses) {
}
