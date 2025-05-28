/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-05-28 11:17:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-28 11:31:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class VisitorGoodsInfo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 商品唯一标识
     */
    private String uid;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 商品图片URL
     */
    private String image;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 商品价格
     */
    private Double price;
    
    /**
     * 商品链接URL
     */
    private String url;
    
    /**
     * 商品标签
     */
    private List<String> tagList;
    
    /**
     * 额外信息，可用于存储其他扩展数据
     */
    private String extra;
}
