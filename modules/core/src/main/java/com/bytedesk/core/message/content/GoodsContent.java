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
public class GoodsContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;

    /** 商品唯一标识 */
    private String uid;

    /** 商品标题 */
    private String title;

    /** 商品图片 */
    private String image;

    /** 商品描述 */
    private String description;

    /** 商品价格 */
    private Double price;

    /** 商品链接 */
    private String url;

    /** 商品标签列表 */
    private List<String> tagList;

    /** 业务扩展字段（建议为JSON字符串） */
    private String extra;

    /** 商品数量（订单场景可选） */
    private Integer quantity;
}
