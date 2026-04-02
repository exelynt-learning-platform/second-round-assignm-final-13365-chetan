package com.multigenesys.ecommerce.gateway;

import com.multigenesys.ecommerce.exception.BadRequestException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

public interface PaymentGateway {

    String charge(long amountInCents, String paymentMethodId);
}

@Component
class StripePaymentGateway implements PaymentGateway {

    @Value("${app.stripe.secret-key}")
    private String stripeSecretKey;

    @Override
    public String charge(long amountInCents, String paymentMethodId) {
        if (stripeSecretKey == null || stripeSecretKey.isBlank() || stripeSecretKey.startsWith("change-this")) {
            throw new BadRequestException("Stripe is not configured");
        }

        try {
            Stripe.apiKey = stripeSecretKey;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setConfirm(true)
                    .setPaymentMethod(paymentMethodId)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            if (!"succeeded".equalsIgnoreCase(intent.getStatus())) {
                throw new BadRequestException("Payment was not completed");
            }
            return intent.getId();
        } catch (StripeException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}
