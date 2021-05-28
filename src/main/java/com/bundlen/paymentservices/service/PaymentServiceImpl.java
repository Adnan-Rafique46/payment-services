package com.bundlen.paymentservices.service;

import com.bundlen.paymentservices.dto.CustomerDTO;
import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.request.StripeSessionDTO;
import com.bundlen.paymentservices.dto.response.StripeSessionResponseDTO;
import com.bundlen.paymentservices.model.Billing;
import com.bundlen.paymentservices.model.Customer;
import com.bundlen.paymentservices.repository.BillingRepository;
import com.bundlen.paymentservices.repository.CustomerRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private BillingRepository billingRepository;
    private CustomerRepository customerRepository;
    @Value("${stripe.secret-key}")
    private String API_SECRET_KEY;
    @Value("${service.url}")
    private String serviceURL;

    @Override
    public void paymentOneTime(PaymentOneTimeDTO dto) {
        String stripeId = null;
        double amount = dto.getAmount()*100;
        try {
            Map chargeParams = new HashMap<>();
            chargeParams.put("amount", amount);
            chargeParams.put("currency", dto.getCurrency());
            chargeParams.put("description", dto.getDescription());
            chargeParams.put("source", dto.getSource());
            Charge charge = Charge.create(chargeParams);
            stripeId = charge.getId();
        }
        catch(StripeException e) {
            throw new RuntimeException("Unable to process the charge", e);
        }
        billingRepository.save(Billing.builder().stripeId(stripeId).amount(amount).currency(dto.getCurrency())
                .description(dto.getDescription()).build());
    }

    @Override
    public CustomerDTO createCustomer(StripeSessionDTO dto) {
        CustomerDTO customerDTO = null;
        try {
            Stripe.apiKey = API_SECRET_KEY;
            Map<String, Object> customerParams = new HashMap<>();
            // add customer unique id here to track them in your web application
            customerParams.put("description", "Customer for " + dto.getEmail());
            customerParams.put("email", dto.getEmail());
            customerParams.put("source", dto.getToken()); // ^ obtained with Stripe.js

            Map<String, String> initialMetadata = new HashMap<>();
            initialMetadata.put("org_id", dto.getOrganizationId());
            customerParams.put("metadata", initialMetadata);

            //create a new customer
            com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.create(customerParams);
            Customer customer = customerRepository.save(Customer.builder().organizationId(dto.getOrganizationId()).stripeCustomerId(stripeCustomer.getId()).build());
            customerDTO = customer.toDTO();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return customerDTO;
    }
    @Override
    public StripeSessionResponseDTO createStripeSession(CustomerDTO customerDTO) {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.SETUP)
                .setCustomer(customerDTO.getStripeCustomerId())
                .setSuccessUrl(serviceURL+"/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(serviceURL+"/checkout/cancel")
                .build();
        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
        return StripeSessionResponseDTO.builder().stripeCustomerId(customerDTO.getStripeCustomerId())
                .stripeSessionId(session.getId()).build();
    }
}
