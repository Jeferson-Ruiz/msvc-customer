package com.mscv.customer.Infrastructure.adapter.out.persistence.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;

@ExtendWith(MockitoExtension.class)
class CustomerPersistenceAdapterTest {

    @Mock
    private CustomerJpaRepository customerJpaRepository;

    @InjectMocks
    private CustomerPersistenceAdapter customerPersistenceAdapter;

    @Test
    void testSaveMapsEntityAndReturnsDomain() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true,
                List.of(new Address(null, "Antioquia", "Medellin", "Calle 1 # 2-3")));

        CustomerEntityJpa saved = entity(1L, "Ana", "Lopez", "12345678", "CC", 30, true,
                List.of(addressEntity(20L, "Antioquia", "Medellin", "Calle 1 # 2-3")));

        given(customerJpaRepository.save(any(CustomerEntityJpa.class))).willReturn(saved);

        Customer result = customerPersistenceAdapter.save(customer);

        ArgumentCaptor<CustomerEntityJpa> captor = ArgumentCaptor.forClass(CustomerEntityJpa.class);
        then(customerJpaRepository).should().save(captor.capture());

        CustomerEntityJpa entity = captor.getValue();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Ana");
        assertThat(entity.getLastName()).isEqualTo("Lopez");
        assertThat(entity.getDocumentNumber()).isEqualTo("12345678");
        assertThat(entity.getDocumentType()).isEqualTo("CC");
        assertThat(entity.getAge()).isEqualTo(30);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getAddresses()).hasSize(1);

        AddressEntityJpa address = entity.getAddresses().get(0);
        assertThat(address.getId()).isNull();
        assertThat(address.getState()).isEqualTo("Antioquia");
        assertThat(address.getCity()).isEqualTo("Medellin");
        assertThat(address.getFullAddress()).isEqualTo("Calle 1 # 2-3");

        assertThat(address.getCustomer()).isSameAs(entity);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Ana");
        assertThat(result.getAddresses()).hasSize(1);
        assertThat(result.getAddresses().get(0).getId()).isEqualTo(20L);
        assertThat(result.getAddresses().get(0).getState()).isEqualTo("Antioquia");
    }

    @Test
    void testFindByIdWhenExistsMapsEntityToDomain() {
        CustomerEntityJpa entity = entity(1L, "Ana", "Lopez", "12345678A", "CC", 30, true,
                List.of(addressEntity(20L, "Antioquia", "Medellin", "Calle 1 # 2-3")));
        given(customerJpaRepository.findById(1L)).willReturn(Optional.of(entity));

        Optional<Customer> result = customerPersistenceAdapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("Ana");
        assertThat(result.get().getLastName()).isEqualTo("Lopez");
        assertThat(result.get().getDocumentNumber()).isEqualTo("12345678A");
        assertThat(result.get().getDocumentType()).isEqualTo("CC");
        assertThat(result.get().getAge()).isEqualTo(30);
        assertThat(result.get().isActive()).isTrue();
        assertThat(result.get().getAddresses()).hasSize(1);
        assertThat(result.get().getAddresses().get(0).getCity()).isEqualTo("Medellin");
    }

    @Test
    void testFindByIdWhenIsEmpty() {
        given(customerJpaRepository.findById(99L)).willReturn(Optional.empty());

        Optional<Customer> result = customerPersistenceAdapter.findById(99L);

        assertThat(result).isEmpty();
        then(customerJpaRepository).should().findById(99L);
    }

    @Test
    void testFindByDocumentNumberWhenExist() {
        String documentNumber = "12345678";
        CustomerEntityJpa entity = entity(1L, "Ana", "Lopez", documentNumber, "CC", 30, true, List.of());
        given(customerJpaRepository.findByDocumentNumber(documentNumber)).willReturn(Optional.of(entity));

        Optional<Customer> result = customerPersistenceAdapter.findByDocumentNumber(documentNumber);

        assertThat(result).isPresent();
        assertNotNull(result);
        assertThat(result.get().getDocumentNumber()).isEqualTo(documentNumber);
        assertThat(result.get().getAddresses()).isEmpty();
        then(customerJpaRepository).should().findByDocumentNumber(documentNumber);
    }

    @Test
    void testExistByDocumentNumber() {
        String documentNumber = "12345678";

        given(customerJpaRepository.existsByDocumentNumber(documentNumber)).willReturn(true);

        boolean result = customerPersistenceAdapter.existByDocumentNumber(documentNumber);

        assertThat(result).isTrue();
        then(customerJpaRepository).should().existsByDocumentNumber(documentNumber);
    }

    private static Customer customer(Long id, String name, String lastName, String documentNumber,
            String documentType, int age, boolean active, List<Address> addresses) {
        return new Customer(id, name, lastName, documentNumber, documentType, age, active, addresses);
    }

    private static CustomerEntityJpa entity(Long id, String name, String lastName, String documentNumber,
            String documentType, int age, boolean active, List<AddressEntityJpa> addresses) {
        CustomerEntityJpa entity = new CustomerEntityJpa();
        entity.setId(id);
        entity.setName(name);
        entity.setLastName(lastName);
        entity.setDocumentNumber(documentNumber);
        entity.setDocumentType(documentType);
        entity.setAge(age);
        entity.setActive(active);
        addresses.forEach(entity::addAddress);
        return entity;
    }

    private static AddressEntityJpa addressEntity(Long id, String state, String city, String fullAddress) {
        AddressEntityJpa address = new AddressEntityJpa();
        address.setId(id);
        address.setState(state);
        address.setCity(city);
        address.setFullAddress(fullAddress);
        return address;
    }
}
