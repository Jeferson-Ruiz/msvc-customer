package com.mscv.customer.Infrastructure.adapter.out.persistence.customer;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.mscv.customer.domain.models.Address;
import com.mscv.customer.domain.models.Customer;
import com.mscv.customer.domain.port.out.CustomerRepositoryPort;

@Repository
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository customerJpaRepository;

    public CustomerPersistenceAdapter(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntityJpa entity = toEntity(customer);
        return toDomain(customerJpaRepository.save(entity));
    }

    @Override
    public Optional<Customer> findByDocumentNumber(String documentNumber) {
        return customerJpaRepository.findByDocumentNumber(documentNumber).map(this::toDomain);
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existByDocumentNumber(String documentNumber) {
        return customerJpaRepository.existsByDocumentNumber(documentNumber);
    }

    private CustomerEntityJpa toEntity(Customer customer) {
        CustomerEntityJpa entity = new CustomerEntityJpa();
        entity.setId(customer.getId());
        entity.setName(customer.getName());
        entity.setLastName(customer.getLastName());
        entity.setDocumentNumber(customer.getDocumentNumber());
        entity.setDocumentType(customer.getDocumentType());
        entity.setAge(customer.getAge());
        entity.setActive(customer.isActive());
        List<Address> addresses = customer.getAddresses();
        if (addresses != null) {
            for (Address address : addresses) {
                AddressEntityJpa addressEntity = new AddressEntityJpa();
                addressEntity.setId(address.getId());
                addressEntity.setState(address.getState());
                addressEntity.setCity(address.getCity());
                addressEntity.setFullAddress(address.getFullAddress());
                entity.addAddress(addressEntity);
            }
        }
        return entity;
    }

    private Customer toDomain(CustomerEntityJpa entity) {
        Customer customer = new Customer();
        customer.setId(entity.getId());
        customer.setName(entity.getName());
        customer.setLastName(entity.getLastName());
        customer.setDocumentNumber(entity.getDocumentNumber());
        customer.setDocumentType(entity.getDocumentType());
        customer.setAge(entity.getAge());
        customer.setActive(entity.isActive());
        customer.setAddresses(entity.getAddresses().stream().map(this::toDomain).toList());
        return customer;
    }

    private Address toDomain(AddressEntityJpa entity) {
        return new Address(entity.getId(), entity.getState(), entity.getCity(), entity.getFullAddress());
    }
}
