package com.mscv.customer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.mscv.customer.application.exception.AddressNotFoundException;
import com.mscv.customer.application.exception.CustomerNotFoundException;
import com.mscv.customer.application.exception.DuplicateDocumentException;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;
import com.mscv.customer.domain.port.out.CustomerRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testCreateWhenDocumentDoesNotExistSavesCustomer() {
        String documentNumber = "12345678";
        Customer customer = customer(null, "Ana", "Lopez", documentNumber, "CC", 30, true);
        given(customerRepositoryPort.existByDocumentNumber(documentNumber)).willReturn(false);
        given(customerRepositoryPort.save(any(Customer.class))).willReturn(customer);

        Customer result = customerService.create(customer);

        assertNotNull(result);
        assertThat(result).isSameAs(customer);
        then(customerRepositoryPort).should().existByDocumentNumber(documentNumber);
        then(customerRepositoryPort).should().save(customer);
    }

    @Test
    void testCreateWhenDocumentExistsThrows() {
        String documentNumber = "12345678";
        Customer customer = customer(null, "Ana", "Lopez", documentNumber, "PAS", 30, true);
        given(customerRepositoryPort.existByDocumentNumber(documentNumber)).willReturn(true);

        assertThrows(DuplicateDocumentException.class, () -> customerService.create(customer));
        then(customerRepositoryPort).should(never()).save(any(Customer.class));
    }

    @Test
    void testCreateWhenDocumentIsBlankThrows() {
        Customer customer = customer(null, "Ana", "Lopez", "  ", "CC", 30, true);

        assertThrows(IllegalArgumentException.class, () -> customerService.create(customer));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testCreateWhenDocumentIsNullThrows() {
        Customer customer = customer(null, "Ana", "Lopez", null, "CC", 30, true);

        assertThrows(IllegalArgumentException.class,
                () -> customerService.create(customer));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testSearchByIdWhenCustomerExists() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        Customer result = customerService.searchById(1L);

        assertEquals(customer.getDocumentNumber(), result.getDocumentNumber());
        assertThat(result).isSameAs(customer);
        then(customerRepositoryPort).should().findById(1L);
    }

    @Test
    void testSearchByIdWhenCustomerDoesNotExistThrows() {
        given(customerRepositoryPort.findById(99L)).willReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.searchById(99L));
        then(customerRepositoryPort).should().findById(99L);
    }

    @Test
    void testSearchByDocumentNumberWhenCustomerExist() {
        String documentNumber = "12345678";
        Customer customer = customer(1L, "Ana", "Lopez", documentNumber, "CC", 30, true);
        given(customerRepositoryPort.findByDocumentNumber(documentNumber)).willReturn(Optional.of(customer));

        Customer result = customerService.searchByDocumentNumber(documentNumber);

        assertNotNull(customer);
        assertThat(result).isSameAs(customer);
        assertEquals(customer.getDocumentNumber(), result.getDocumentNumber());
        then(customerRepositoryPort).should().findByDocumentNumber(documentNumber);
    }

    @Test
    void testSearchByDocumentNumberWhenCustomerDoesNotExistThrows() {
        given(customerRepositoryPort.findByDocumentNumber("1004811")).willReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> customerService.searchByDocumentNumber("1004811"));
        then(customerRepositoryPort).should().findByDocumentNumber("1004811");
    }

    @Test
    void testSearchByDocumentNumberWhenDocumentIsBlankThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.searchByDocumentNumber(" "));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testSearchByDocumentNumberWhenDocumentIsNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.searchByDocumentNumber(null));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeNameWhenNameIsValidUpdate() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        customerService.changeName(1L, "Ana Maria");

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Ana Maria");
    }

    @Test
    void testChangeNameWhenNameIsBlankThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeName(1L, " "));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeNameWhenNameIsNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeName(1L, null));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeLastNameWhenLastNameIsValidUpdate() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        customerService.changeLastName(1L, "pascal");

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().getLastName()).isEqualTo("pascal");
    }

    @Test
    void testChangeLastNameWhenLastNameIsBlankThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeLastName(1L, " "));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeLastNameWhenLastNameIsNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeLastName(1L, null));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeDocumentNumberWhenDocumentIsValidUpdate() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepositoryPort.existByDocumentNumber("87654321")).willReturn(false);

        customerService.changeDocumentNumber(1L, "87654321");

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().getDocumentNumber()).isEqualTo("87654321");
    }

    @Test
    void testChangeDocumentNumberWhenDocumentExistsThrows() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepositoryPort.existByDocumentNumber("87654321")).willReturn(true);

        assertThrows(DuplicateDocumentException.class,
                () -> customerService.changeDocumentNumber(1L, "87654321"));
        then(customerRepositoryPort).should(never()).save(any(Customer.class));
    }

    @Test
    void testChangeDocumentNumberWhenDocumentIsSameDoesNotUpdate() {
        String documentNumber = "12345678";
        Customer customer = customer(1L, "Ana", "Lopez", documentNumber, "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        customerService.changeDocumentNumber(1L, documentNumber);

        then(customerRepositoryPort).should(never()).existByDocumentNumber(documentNumber);
        then(customerRepositoryPort).should(never()).save(any(Customer.class));
    }

    @Test
    void testChangeDocumentNumberWhenDocumentIsBlankThrows() {
        assertThrows(IllegalArgumentException.class, () -> customerService.changeDocumentNumber(1L, " "));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeDocumentNumberWhenDocumentIsNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeDocumentNumber(1L, null));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeDocumentTypeWhenTypeIsValidUpdate() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        customerService.changeDocumentType(1L, "PAS");

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().getDocumentType()).isEqualTo("PAS");
    }

    @Test
    void testChangeDocumentTypeWhenTypeIsBlankThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeDocumentType(1L, " "));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeDocumentTypeWhenTypeIsNullThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeDocumentType(1L, null));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeAgeWhenAgeIsNegativeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.changeAge(1L, -1));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testChangeAgeWhenAgeIsValidUpdate() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        customerService.changeAge(1L, 35);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().getAge()).isEqualTo(35);
    }

    @Test
    void testChangeActiveWhenActiveIsValidUpdate() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        customerService.changeActive(1L, false);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().isActive()).isFalse();
    }

    @Test
    void testAddAddressWhenAddressIsValidAddsAddress() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        Address address = new Address(null, "Antioquia", "Medellin", "Calle 1 # 2-3");
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepositoryPort.save(any(Customer.class))).willReturn(customer);

        Customer result = customerService.addAddress(1L, address);

        assertThat(result).isSameAs(customer);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().getAddresses()).hasSize(1);
        Address saved = captor.getValue().getAddresses().get(0);
        assertThat(saved.getId()).isNull();
        assertThat(saved.getState()).isEqualTo("Antioquia");
        assertThat(saved.getCity()).isEqualTo("Medellin");
        assertThat(saved.getFullAddress()).isEqualTo("Calle 1 # 2-3");
    }

    @Test
    void testAddAddressWhenCityIsBlankThrows() {
        Address address = new Address(null, "Antioquia", " ", "Calle 1 # 2-3");

        assertThrows(IllegalArgumentException.class,
                () -> customerService.addAddress(1L, address));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testAddAddressWhenStateIsNullThrows() {
        Address address = new Address(null, null, "Medellin", "Calle 1 # 2-3");
        assertThrows(IllegalArgumentException.class,
                () -> customerService.addAddress(1L, address));
        then(customerRepositoryPort).shouldHaveNoInteractions();
    }

    @Test
    void testRemoveAddressWhenAddressExistsRemovesIt() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        customer.addAddress(new Address(10L, "Antioquia", "Medellin", "Calle 1 # 2-3"));
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        customerService.removeAddress(1L, 10L);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);
        then(customerRepositoryPort).should().save(captor.capture());
        assertThat(captor.getValue().getAddresses()).isEmpty();
    }

    @Test
    void testRemoveAddressWhenAddressDoesNotExistThrows() {
        Customer customer = customer(1L, "Ana", "Lopez", "12345678", "CC", 30, true);
        customer.addAddress(new Address(10L, "Antioquia", "Medellin", "Calle 1 # 2-3"));
        given(customerRepositoryPort.findById(1L)).willReturn(Optional.of(customer));

        assertThrows(AddressNotFoundException.class,
                () -> customerService.removeAddress(1L, 99L));

        then(customerRepositoryPort).should(never()).save(any(Customer.class));
    }

    private static Customer customer(Long id, String name, String lastName, String documentNumber,
            String documentType, int age, boolean active) {
        return new Customer(id, name, lastName, documentNumber, documentType, age, active, new ArrayList<>());
    }
}
