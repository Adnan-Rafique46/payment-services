package com.bundlen.paymentservices.repository;

import com.bundlen.paymentservices.model.Billing;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BillingRepository extends MongoRepository<Billing, String> {
    Optional<Billing> findById(String id);
}