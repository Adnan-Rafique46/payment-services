package com.bundlen.paymentservices.service;

import com.bundlen.paymentservices.dto.CustomerDTO;
import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.request.StripeSessionDTO;
import com.bundlen.paymentservices.dto.response.LongResponseDTO;
import com.bundlen.paymentservices.dto.response.StripeSessionResponseDTO;
import com.bundlen.paymentservices.model.Billing;
import com.bundlen.paymentservices.model.Customer;
import com.bundlen.paymentservices.repository.BillingRepository;
import com.bundlen.paymentservices.repository.CustomerRepository;
import com.bundlen.paymentservices.util.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@EnableScheduling
@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private BillingRepository billingRepository;
    private CustomerRepository customerRepository;
    @Value("${stripe.secret-key}")
    private String API_SECRET_KEY;
    @Value("${service.url}")
    private String serviceURL;
    @Value("${patient.count.service.url}")
    private String patientCountServiceURL;
    @Autowired
    private RestTemplate restTemplate;

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
    public CustomerDTO updateCustomerIntent(CustomerDTO dto) {
        CustomerDTO customerDTO = null;
        try {
            Customer customer = customerRepository.findByOrganizationId(dto.getOrganizationId()).get();
            customer.setStripeIntentId(dto.getStripeIntentId());
            customer.setStripePaymentMethod(dto.getStripePaymentMethod());
            customer.setCheckoutDate(new Date());
            customer.setNextPaymentDate(DateUtil.nextDate(customer.getCheckoutDate(), 30));
            customerRepository.save(customer);
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
    @Override
    @Scheduled(cron = "0 0 9 * * *")
    public void chargeCustomers() {
        //@todo for testing purpose passing the date by adding 30, later just pass current date
        List<Customer> paymentsDueToday = customerRepository.findAllByNextPaymentDate(DateUtil.nextDate(new Date(), 30));
        for (Customer customer:paymentsDueToday) {
            String response = callPatientService(customer.getOrganizationId());
            ObjectMapper mapper = new ObjectMapper();
            try {
                LongResponseDTO patientsCount = mapper.readValue(response, LongResponseDTO.class);

                PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setCurrency("usd")
                            .setAmount(patientsCount.getResult() * 2)
                            .setPaymentMethod(customer.getStripePaymentMethod())
                            .setCustomer(customer.getStripeCustomerId())
                            .setConfirm(true)
                            .setOffSession(true)
                            .build();
                PaymentIntent.create(params);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            catch (StripeException err) {
                // Error code will be authentication_required if authentication is needed
                System.out.println("Error code is : " + err.getCode());
                String paymentIntentId = err.getStripeError().getPaymentIntent().getId();
                PaymentIntent paymentIntent = null;
                try {
                    paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                } catch (StripeException e) {
                    e.printStackTrace();
                }
                System.out.println(paymentIntent.getId());
            }
        }
    }
    private String callPatientService(String organizationId) {
        String response = StringUtils.EMPTY;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            response = restTemplate.exchange(patientCountServiceURL + organizationId, HttpMethod.GET, entity, String.class).getBody();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }
}
