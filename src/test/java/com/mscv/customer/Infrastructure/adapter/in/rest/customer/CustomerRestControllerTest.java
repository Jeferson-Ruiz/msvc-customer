package com.mscv.customer.Infrastructure.adapter.in.rest.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerResponseDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.mapper.CustomerRestMapper;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;
import com.mscv.customer.domain.port.in.CustomerUseCase;

@ExtendWith(MockitoExtension.class)
class CustomerRestControllerTest {

    @Mock
    private CustomerUseCase customerUseCase;

    private CustomerRestController customerRestController;

    @BeforeEach
    void setUp() {
        customerRestController = new CustomerRestController(customerUseCase, new CustomerRestMapper());
    }

    @Test
    void testCreateReturnsCreatedWithBody() {
        CustomerRequestDto request = new CustomerRequestDto(
                "Ana", "Lopez", "12345678", "CC", 30, true, List.of());
        given(customerUseCase.create(any(Customer.class))).willReturn(customer());

        ResponseEntity<CustomerResponseDto> result = customerRestController.create(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody().id()).isEqualTo(1L);
        assertThat(result.getBody().name()).isEqualTo("Ana");
        then(customerUseCase).should().create(any(Customer.class));
    }

    @Test
    void testGetByIdReturnsCustomer() {
        given(customerUseCase.searchById(1L)).willReturn(customer());

        CustomerResponseDto result = customerRestController.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.documentNumber()).isEqualTo("12345678");
        then(customerUseCase).should().searchById(1L);
    }

    @Test
    void testGetByDocumentNumber() {
        String document = "12345678";
        given(customerUseCase.searchByDocumentNumber(anyString())).willReturn(customer());

        CustomerResponseDto result = customerRestController.getByDocumentNumber(document);

        assertThat(result.documentNumber()).isEqualTo(document);
        then(customerUseCase).should().searchByDocumentNumber(document);
    }

    @Test
    void testChangeNameDelegates() {
        customerRestController.changeName(1L, "Ana Maria");
        then(customerUseCase).should().changeName(1L, "Ana Maria");
    }

    @Test
    void testChangeLastNameDelegates() {
        customerRestController.changeLastName(1L, "Pascal");
        then(customerUseCase).should().changeLastName(1L, "Pascal");
    }

    @Test
    void testChangeDocumentNumberDelegates() {
        customerRestController.changeDocumentNumber(1L, "87654321");
        then(customerUseCase).should().changeDocumentNumber(1L, "87654321");
    }

    @Test
    void testChangeDocumentTypeDelegates() {
        customerRestController.changeDocumentType(1L, "PAS");
        then(customerUseCase).should().changeDocumentType(1L, "PAS");
    }

    @Test
    void testChangeAgeDelegates() {
        customerRestController.changeAge(1L, 35);
        then(customerUseCase).should().changeAge(1L, 35);
    }

    @Test
    void testChangeActiveDelegates() {
        customerRestController.changeActive(1L, false);
        then(customerUseCase).should().changeActive(1L, false);
    }

    @Test
    void testAddAddressReturnsCreatedWithBody() {
        AddressRequestDto request = new AddressRequestDto("Antioquia", "Medellin", "Calle 1 # 2-3");
        given(customerUseCase.addAddress(eq(1L), any(Address.class))).willReturn(customer());

        ResponseEntity<CustomerResponseDto> result = customerRestController.addAddress(1L, request);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        then(customerUseCase).should().addAddress(eq(1L), any(Address.class));
    }

    @Test
    void testRemoveAddressDelegates() {
        customerRestController.removeAddress(1L, 10L);
        then(customerUseCase).should().removeAddress(1L, 10L);
    }

    private static Customer customer() {
        return new Customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true, List.of());
    }
}
