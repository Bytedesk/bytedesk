package com.bytedesk.core.message.content;

import java.util.List;

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

    /** 业务订单号 */
    private String orderUid;

    /** 详情跳转路径 */
    private String navigateToPath;

    /** 订单类型 */
    private String type;

    /** 订单卡片标题 */
    private String title;

    /** 订单卡片描述 */
    private String description;

    /** 订单状态机状态 */
    private String state;

    /** 下单时间（字符串，前端直接展示） */
    private String time;

    /** 订单状态：pending/paid/shipped/delivered */
    private String status;

    /** 状态文案（可选，前端优先展示该字段） */
    private String statusText;

    /** 订单商品标题 */
    private String orderTitle;

    /** 订单商品图片 */
    private String orderImage;

    /** 订单商品描述 */
    private String orderDescription;

    /** 订单商品价格 */
    private Double orderPrice;

    /** 订单商品链接 */
    private String orderUrl;

    /** 订单商品标签 */
    private List<String> orderTagList;

    /** 订单商品扩展字段 */
    private String orderExtra;

    /** 订单商品数量 */
    private Integer orderQuantity;

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

    /** 访客uid */
    private String visitorUid;

    /** 店铺uid */
    private String shopUid;

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
