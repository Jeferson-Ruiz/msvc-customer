package com.mscv.customer.application;

import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mscv.customer.application.exception.AddressNotFoundException;
import com.mscv.customer.application.exception.CustomerNotFoundException;
import com.mscv.customer.application.exception.DuplicateDocumentException;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;
import com.mscv.customer.domain.port.in.CustomerUseCase;
import com.mscv.customer.domain.port.out.CustomerRepositoryPort;

@Service
public class CustomerService implements CustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;

    public CustomerService(CustomerRepositoryPort customerRepositoryPort) {
        this.customerRepositoryPort = customerRepositoryPort;
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        Objects.requireNonNull(customer, "El cliente no puede ser nulo");
        requireNonBlank(customer.getDocumentNumber(), "documentNumber");
        validateDocumenNumer(customer.getDocumentNumber());
        return customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public Customer searchById(Long id) {
        return getCustomerById(id);
    }

    @Override
    @Transactional
    public Customer searchByDocumentNumber(String documentNumber) {
        return getCustomerByDocumenNumber(documentNumber);
    }

    @Override
    @Transactional
    public void changeName(Long id, String name) {
        requireNonBlank(name, "name");
        Customer customer = getCustomerById(id);
        customer.setName(name);
        customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public void changeLastName(Long id, String lastName) {
        requireNonBlank(lastName, "lastName");
        Customer customer = getCustomerById(id);
        customer.setLastName(lastName);
        customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public void changeDocumentNumber(Long id, String documentNumber) {
        requireNonBlank(documentNumber, "documentNumber");
        Customer customer = getCustomerById(id);
        if (Objects.equals(customer.getDocumentNumber(), documentNumber)) {
            return;
        }
        if (customerRepositoryPort.existByDocumentNumber(documentNumber)) {
            throw new DuplicateDocumentException(documentNumber);
        }
        customer.setDocumentNumber(documentNumber);
        customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public void changeDocumentType(Long id, String documentType) {
        requireNonBlank(documentType, "documentType");
        Customer customer = getCustomerById(id);
        customer.setDocumentType(documentType);
        customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public void changeAge(Long id, int age) {
        if (age < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        Customer customer = getCustomerById(id);
        customer.setAge(age);
        customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public void changeActive(Long id, boolean active) {
        Customer customer = getCustomerById(id);
        customer.setActive(active);
        customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public Customer addAddress(Long customerId, Address address) {
        Objects.requireNonNull(address, "Direccion no puede ser nula");
        requireNonBlank(address.getState(), "state");
        requireNonBlank(address.getCity(), "city");
        requireNonBlank(address.getFullAddress(), "fullAddress");
        Customer customer = getCustomerById(customerId);
        customer.addAddress(new Address(null, address.getState(), address.getCity(), address.getFullAddress()));
        return customerRepositoryPort.save(customer);
    }

    @Override
    @Transactional
    public void removeAddress(Long customerId, Long addressId) {
        Objects.requireNonNull(addressId, "Direccion no puede ser nula");
        Customer customer = getCustomerById(customerId);
        boolean removed = customer.getAddresses()
                .removeIf(address -> Objects.equals(address.getId(), addressId));
        if (!removed) {
            throw new AddressNotFoundException(customerId, addressId);
        }
        customerRepositoryPort.save(customer);
    }

    private Customer getCustomerById(Long id) {
        Objects.requireNonNull(id, "id no puede ser nulo");
        return customerRepositoryPort.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    private Customer getCustomerByDocumenNumber(String documentNumber) {
        requireNonBlank(documentNumber, "documentNumber");
        return customerRepositoryPort.findByDocumentNumber(documentNumber)
                .orElseThrow(() -> new CustomerNotFoundException(documentNumber));
    }

    private void validateDocumenNumer(String documentNumer) {
        if (customerRepositoryPort.existByDocumentNumber(documentNumer)) {
            throw new DuplicateDocumentException(documentNumer);
        }
    }

    private static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " No puede ser nulo");
        }
    }

}
