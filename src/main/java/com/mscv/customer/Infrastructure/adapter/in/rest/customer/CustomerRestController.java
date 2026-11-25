package com.mscv.customer.Infrastructure.adapter.in.rest.customer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerResponseDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.mapper.CustomerRestMapper;
import com.mscv.customer.domain.models.Customer;
import com.mscv.customer.domain.port.in.CustomerUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {

    private final CustomerUseCase customerUseCase;
    private final CustomerRestMapper customerRestMapper;

    public CustomerRestController(CustomerUseCase customerUseCase, CustomerRestMapper customerRestMapper) {
        this.customerUseCase = customerUseCase;
        this.customerRestMapper = customerRestMapper;
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@Valid @RequestBody CustomerRequestDto request) {
        Customer created = customerUseCase.create(customerRestMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(customerRestMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    public CustomerResponseDto getById(@PathVariable Long id) {
        return customerRestMapper.toResponse(customerUseCase.searchById(id));
    }

    @GetMapping("/search")
    public CustomerResponseDto getByDocumentNumber(@RequestParam("document") String documentNumber) {
        return customerRestMapper.toResponse(customerUseCase.searchByDocumentNumber(documentNumber));
    }

    @PatchMapping("/{id}/name")
    public void changeName(@PathVariable Long id, @RequestParam String name) {
        customerUseCase.changeName(id, name);
    }

    @PatchMapping("/{id}/last-name")
    public void changeLastName(@PathVariable Long id, @RequestParam String lastName) {
        customerUseCase.changeLastName(id, lastName);
    }

    @PatchMapping("/{id}/document-number")
    public void changeDocumentNumber(@PathVariable Long id, @RequestParam String documentNumber) {
        customerUseCase.changeDocumentNumber(id, documentNumber);
    }

    @PatchMapping("/{id}/document-type")
    public void changeDocumentType(@PathVariable Long id, @RequestParam String documentType) {
        customerUseCase.changeDocumentType(id, documentType);
    }

    @PatchMapping("/{id}/age")
    public void changeAge(@PathVariable Long id, @RequestParam int age) {
        customerUseCase.changeAge(id, age);
    }

    @PatchMapping("/{id}/active")
    public void changeActive(@PathVariable Long id, @RequestParam boolean active) {
        customerUseCase.changeActive(id, active);
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<CustomerResponseDto> addAddress(@PathVariable Long customerId,
            @Valid @RequestBody AddressRequestDto request) {
        Customer updated = customerUseCase.addAddress(customerId, customerRestMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(customerRestMapper.toResponse(updated));
    }

    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAddress(@PathVariable Long customerId, @PathVariable Long addressId) {
        customerUseCase.removeAddress(customerId, addressId);
    }
}