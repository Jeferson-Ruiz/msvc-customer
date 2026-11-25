package com.mscv.customer.domain.port.in;

import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;

public interface CustomerUseCase {

    Customer create(Customer customer);

    Customer searchById(Long id);

    Customer searchByDocumentNumber(String documentNumber);

    void changeName(Long id, String name);

    void changeLastName(Long id, String lastName);

    void changeDocumentNumber(Long id, String documentNumber);

    void changeDocumentType(Long id, String documentType);

    void changeAge(Long id, int age);

    void changeActive(Long id, boolean active);

    Customer addAddress(Long customerId, Address address);

    void removeAddress(Long customerId, Long addressId);
}
