package com.bundlen.paymentservices.api;

import com.bundlen.paymentservices.dto.CustomerDTO;
import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.request.StripeSessionDTO;
import com.bundlen.paymentservices.dto.response.BooleanResponseDTO;
import com.bundlen.paymentservices.dto.response.ResponseDTO;
import com.bundlen.paymentservices.dto.response.StripeSessionResponseDTO;
import com.bundlen.paymentservices.service.PaymentServiceImpl;
import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/payment-service")
public class ApiControllerImpl implements ApiController {
    @Autowired
    private PaymentServiceImpl paymentService;

    @Value("${stripe.secret-key}")
    private String API_SECRET_KEY;

    @Override
    public ResponseEntity<BooleanResponseDTO> paymentOneTime(@RequestHeader("Authorization") String authorization,
                                                             @Validated @RequestBody PaymentOneTimeDTO dto) {
        paymentService.paymentOneTime(dto);
        return ResponseEntity.status(CREATED).body(new BooleanResponseDTO(true));
    }

    @Override
    public ResponseEntity<ResponseDTO<StripeSessionResponseDTO>> createSession(@RequestHeader("Authorization") String authorization,
                                                                               @Validated @RequestBody StripeSessionDTO dto) {
        CustomerDTO customerDTO = paymentService.createCustomer(dto);
        Stripe.apiKey = API_SECRET_KEY;
        StripeSessionResponseDTO stripeSessionResponseDTO = paymentService.createStripeSession(customerDTO);
        return ResponseEntity.status(nonNull(stripeSessionResponseDTO)?OK:NO_CONTENT).body(new ResponseDTO<>(stripeSessionResponseDTO));
    }
    @Override
    public Object postEventsWebhook(@RequestBody String json, HttpServletRequest request, HttpServletResponse response) {
//        System.out.println(json);
        Event event = null;

        try {
            event = ApiResource.GSON.fromJson(json, Event.class);
        } catch (JsonSyntaxException e) {
            // Invalid payload
            response.setStatus(400);
            return "";
        }
        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        // Handle the event
        switch (event.getType()) {
            case "checkout.session.completed":
                Session checkoutSession = (Session) stripeObject;
                String setupIntentId = checkoutSession.getSetupIntent();
                System.out.println("Setup Intent Id: "+setupIntentId);
                try {
                    SetupIntent intent = SetupIntent.retrieve(setupIntentId);
                    String stripeCustomerId = checkoutSession.getCustomer();
                    String organizationId = checkoutSession.getMetadata().get("org_id");
                    String stripePaymentMethod = intent.getPaymentMethod();
                    paymentService.updateCustomerIntent(CustomerDTO.builder().organizationId(organizationId).stripeCustomerId(stripeCustomerId)
                            .stripeIntentId(intent.getId()).stripePaymentMethod(stripePaymentMethod).build());
                } catch (StripeException e) {
                    e.printStackTrace();
                }
                break;
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("Payment Intent for customer: "+paymentIntent.getCustomer());
                System.out.println("Payment Intent Id: "+paymentIntent.getId());
                // Then define and call a method to handle the successful payment intent.
                // handlePaymentIntentSucceeded(paymentIntent);
                break;
            case "payment_method.attached":
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                System.out.println("Payment method for customer: "+paymentMethod.getCustomer());
                // Then define and call a method to handle the successful attachment of a PaymentMethod.
                // handlePaymentMethodAttached(paymentMethod);
                break;
            // ... handle other event types
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }
        response.setStatus(200);
        return "";
    }
    @Override
    public void checkoutSuccess(@PathVariable("session_id") String sessionId) {
        System.out.println("Checkout Session completed with Session Id: "+sessionId);
    }
    @Override
    public void checkoutCancel() {
        System.out.println("Checkout Cancelled");
    }
}
