package com.bundlen.paymentservices.repository;

import com.bundlen.paymentservices.model.Billing;
import com.bundlen.paymentservices.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findById(String id);
    Optional<Customer> findByOrganizationId(String organizationId);
    List<Customer> findAllByNextPaymentDate(Date nextPaymentDate);
}