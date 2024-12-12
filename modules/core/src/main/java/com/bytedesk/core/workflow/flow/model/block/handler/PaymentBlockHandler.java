package com.bytedesk.core.workflow.flow.model.block.handler;

import org.springframework.stereotype.Component;

import com.bytedesk.core.workflow.flow.model.block.model.Block;
import com.bytedesk.core.workflow.flow.model.block.model.BlockType;
import com.bytedesk.core.workflow.flow.model.block.model.options.PaymentBlockOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.stripe.Stripe;
// import com.stripe.model.PaymentIntent;
// import com.stripe.param.PaymentIntentCreateParams;

import lombok.extern.slf4j.Slf4j;
// import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PaymentBlockHandler implements BlockHandler {
    private final ObjectMapper objectMapper;

    public PaymentBlockHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String getType() {
        return BlockType.PAYMENT_INPUT.name();
    }

    @Override
    public Map<String, Object> processBlock(Block block, Map<String, Object> context) {
        PaymentBlockOptions options = objectMapper.convertValue(block.getOptions(), PaymentBlockOptions.class);
        Map<String, Object> result = new HashMap<>(context);

        try {
            switch (options.getProvider().toLowerCase()) {
                case "stripe":
                    handleStripePayment(options, result);
                    break;
                case "paypal":
                    handlePaypalPayment(options, result);
                    break;
                default:
                    throw new UnsupportedOperationException(
                            "Unsupported payment provider: " + options.getProvider());
            }
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            result.put("error", e.getMessage());
            result.put("success", false);
            result.put("message", options.getErrorMessage());
        }

        return result;
    }

    @Override
    public boolean validateOptions(Block block) {
        try {
            PaymentBlockOptions options = objectMapper.convertValue(block.getOptions(), PaymentBlockOptions.class);
            return options.getProvider() != null &&
                    options.getAmount() != null &&
                    options.getCurrency() != null &&
                    options.getCredentials() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private void handleStripePayment(PaymentBlockOptions options, Map<String, Object> result) {
        // try {
        // Stripe.apiKey = options.getCredentials().get("secretKey");

        // PaymentIntentCreateParams.Builder paramsBuilder =
        // PaymentIntentCreateParams.builder()
        // .setAmount(options.getAmount().multiply(new BigDecimal(100)).longValue())
        // .setCurrency(options.getCurrency().toLowerCase())
        // .setDescription(options.getDescription())
        // .setAutomaticPaymentMethods(
        // PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
        // .setEnabled(true)
        // .build()
        // );

        // if (options.getReturnUrl() != null) {
        // paramsBuilder.setReturnUrl(options.getReturnUrl());
        // }

        // PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

        // result.put("clientSecret", paymentIntent.getClientSecret());
        // result.put("paymentIntentId", paymentIntent.getId());
        // result.put("success", true);
        // result.put("message", options.getSuccessMessage());

        // if (options.getVariableName() != null) {
        // result.put(options.getVariableName(), paymentIntent.getId());
        // }

        // } catch (Exception e) {
        // log.error("Stripe payment failed", e);
        // throw new RuntimeException("Failed to create payment intent", e);
        // }
    }

    private void handlePaypalPayment(PaymentBlockOptions options, Map<String, Object> result) {
        // TODO: 实现PayPal支付逻辑
        throw new UnsupportedOperationException("PayPal payment not implemented yet");
    }

    private String processTemplate(String template, Map<String, Object> context) {
        if (template == null)
            return null;

        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            if (template.contains(placeholder)) {
                template = template.replace(placeholder, String.valueOf(entry.getValue()));
            }
        }
        return template;
    }
}
