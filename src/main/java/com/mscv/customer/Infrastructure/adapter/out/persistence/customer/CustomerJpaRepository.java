package com.mscv.customer.Infrastructure.adapter.out.persistence.customer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntityJpa, Long> {

    Optional<CustomerEntityJpa> findByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumber(String documentNumber);
}
