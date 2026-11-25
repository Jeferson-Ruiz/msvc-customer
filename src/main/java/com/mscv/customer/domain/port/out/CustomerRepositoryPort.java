package com.mscv.customer.domain.port.out;

import java.util.Optional;
import com.mscv.customer.domain.models.Customer;

public interface CustomerRepositoryPort {

    Customer save(Customer customer);

    Optional<Customer> findByDocumentNumber(String documentNumber);

    Optional<Customer> findById(Long id);

    boolean existByDocumentNumber(String documentNumber);
}
