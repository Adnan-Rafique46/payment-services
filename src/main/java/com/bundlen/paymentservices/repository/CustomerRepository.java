package com.bundlen.paymentservices.repository;

import com.bundlen.paymentservices.model.Billing;
import com.bundlen.paymentservices.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findById(String id);
}