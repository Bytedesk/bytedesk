package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class OrderContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;

    /** 订单号 */
    private String uid;

    /** 下单时间（字符串，前端直接展示） */
    private String time;

    /** 订单状态：pending/paid/shipped/delivered */
    private String status;

    /** 状态文案（可选，前端优先展示该字段） */
    private String statusText;

    /** 商品信息 */
    private GoodsContent goods;

    /** 订单总金额 */
    private Double totalAmount;

    /** 收货地址 */
    private ShippingAddress shippingAddress;

    /** 支付方式 */
    private String paymentMethod;

    /** 业务扩展字段（建议为JSON字符串） */
    private String extra;

    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShippingAddress {
        private String name;
        private String phone;
        private String address;
    }
}
