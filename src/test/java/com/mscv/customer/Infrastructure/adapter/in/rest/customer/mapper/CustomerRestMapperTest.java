package com.mscv.customer.Infrastructure.adapter.in.rest.customer.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.address.dto.AddressResponseDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerRequestDto;
import com.mscv.customer.Infrastructure.adapter.in.rest.customer.dto.CustomerResponseDto;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;

class CustomerRestMapperTest {

    private final CustomerRestMapper customerRestMapper = new CustomerRestMapper();

    @Test
    void testToDomainWithAddressesMapsAllFields() {
        List<AddressRequestDto> addresses = List.of(
                new AddressRequestDto("Antioquia", "Medellin", "Calle 1 # 2-3"),
                new AddressRequestDto("Cundinamarca", "Bogota", "Carrera 7 # 10-20"));
                
        CustomerRequestDto request = new CustomerRequestDto(
                "Ana", "Lopez", "12345678A", "CC", 30, true, addresses);

        Customer result = customerRestMapper.toDomain(request);

        assertThat(result.getId()).isNull();
        assertThat(result.getName()).isEqualTo("Ana");
        assertThat(result.getLastName()).isEqualTo("Lopez");
        assertThat(result.getDocumentNumber()).isEqualTo("12345678A");
        assertThat(result.getDocumentType()).isEqualTo("CC");
        assertThat(result.getAge()).isEqualTo(30);
        assertThat(result.isActive()).isTrue();
        assertThat(result.getAddresses()).hasSize(2);
        assertThat(result.getAddresses().get(0).getId()).isNull();
        assertThat(result.getAddresses().get(0).getState()).isEqualTo("Antioquia");
        assertThat(result.getAddresses().get(0).getCity()).isEqualTo("Medellin");
        assertThat(result.getAddresses().get(0).getFullAddress()).isEqualTo("Calle 1 # 2-3");
        assertThat(result.getAddresses().get(1).getCity()).isEqualTo("Bogota");
    }

    @Test
    void testToDomainWithNullAddressesUsesEmptyList() {
        CustomerRequestDto request = new CustomerRequestDto(
                "Ana", "Lopez", "12345678A", "CC", 30, true, null);

        Customer result = customerRestMapper.toDomain(request);

        assertThat(result.getAddresses()).isEmpty();
    }

    @Test
    void testToResponseWithAddressesMapsAllFields() {
        Customer customer = new Customer(1L, "Ana", "Lopez", "12345678A", "CC", 30, true,
                List.of(new Address(10L, "Antioquia", "Medellin", "Calle 1 # 2-3")));

        CustomerResponseDto result = customerRestMapper.toResponse(customer);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Ana");
        assertThat(result.lastName()).isEqualTo("Lopez");
        assertThat(result.documentNumber()).isEqualTo("12345678A");
        assertThat(result.documentType()).isEqualTo("CC");
        assertThat(result.age()).isEqualTo(30);
        assertThat(result.active()).isTrue();
        assertThat(result.addresses()).hasSize(1);
        AddressResponseDto address = result.addresses().get(0);
        assertThat(address.id()).isEqualTo(10L);
        assertThat(address.state()).isEqualTo("Antioquia");
        assertThat(address.city()).isEqualTo("Medellin");
        assertThat(address.fullAddress()).isEqualTo("Calle 1 # 2-3");
    }

    @Test
    void testToResponseWithNullAddressesUsesEmptyList() {
        Customer customer = new Customer(1L, "Ana", "Lopez", "12345678A", "CC", 30, true, null);

        CustomerResponseDto result = customerRestMapper.toResponse(customer);

        assertThat(result.addresses()).isEmpty();
    }

    @Test
    void testToDomainAddressMapsFieldsWithNullId() {
        AddressRequestDto request = new AddressRequestDto("Antioquia", "Medellin", "Calle 1 # 2-3");

        Address result = customerRestMapper.toDomain(request);

        assertThat(result.getId()).isNull();
        assertThat(result.getState()).isEqualTo("Antioquia");
        assertThat(result.getCity()).isEqualTo("Medellin");
        assertThat(result.getFullAddress()).isEqualTo("Calle 1 # 2-3");
    }
}
