package com.bytedesk.core.workflow.flow.model.block.model.options;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.util.Map;

import com.bytedesk.core.workflow.flow.model.block.model.BlockOptions;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentBlockOptions extends BlockOptions {
    private String provider;  // stripe, paypal, etc.
    private String currency;
    private BigDecimal amount;
    private String description;
    private String successMessage;
    private String errorMessage;
    private String returnUrl;
    private String cancelUrl;
    private Map<String, String> credentials;
    private Map<String, Object> additionalOptions;
    private String variableName;  // 存储支付结果的变量名
} 
