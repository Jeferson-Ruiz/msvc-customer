package com.mscv.customer.Infrastructure.adapter.in.rest.customer.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressResponseDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerResponseDto;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;

@Component
public class CustomerRestMapper {

    public Customer toDomain(CustomerRequestDto request) {
        List<Address> addresses = request.addresses() == null
                ? new ArrayList<>()
                : new ArrayList<>(request.addresses().stream().map(this::toDomain).toList());
        return new Customer(
                null,
                request.name(),
                request.lastName(),
                request.documentNumber(),
                request.documentType(),
                request.age(),
                request.active(),
                addresses);
    }

    public CustomerResponseDto toResponse(Customer customer) {
        List<AddressResponseDto> addresses = customer.getAddresses() == null
                ? List.of()
                : customer.getAddresses().stream().map(this::toResponse).toList();
        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getLastName(),
                customer.getDocumentNumber(),
                customer.getDocumentType(),
                customer.getAge(),
                customer.isActive(),
                addresses);
    }

    public Address toDomain(AddressRequestDto request) {
        return new Address(null, request.state(), request.city(), request.fullAddress());
    }

    private AddressResponseDto toResponse(Address address) {
        return new AddressResponseDto(
                address.getId(),
                address.getState(),
                address.getCity(),
                address.getFullAddress());
    }
}
