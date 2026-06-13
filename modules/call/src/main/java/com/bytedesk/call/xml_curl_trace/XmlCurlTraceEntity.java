/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:14:28
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:35:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.xml_curl_trace;

import com.bytedesk.core.constant.TypeConsts;

import org.checkerframework.checker.units.qual.C;

import com.bytedesk.core.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
// import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Persistent xml_curl request trace entity.
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
// @EntityListeners({XmlCurlTraceEntityListener.class})
@Table(name = "bytedesk_call_xml_curl_trace")
public class XmlCurlTraceEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 请求分区（如 directory/dialplan/configuration）
     * Request section (e.g. directory/dialplan/configuration).
     */
    @Column(name = "section", nullable = false)
    private String section;

    /**
     * 请求分类（更细粒度的业务分类）
     * Request category for finer-grained business grouping.
     */
    @Column(name = "category", nullable = false)
    private String category;

    /**
     * 请求来源地址（通常为客户端或网关 IP）
     * Request source address, usually client/gateway IP.
     */
    @Column(name = "remote_addr")
    private String remote;

    /**
     * HTTP 请求方法（GET/POST 等）
     * HTTP method (GET/POST, etc.).
     */
    @Column(name = "http_method")
    private String method;

    /**
     * 请求 URI 路径
     * Request URI path.
     */
    @Column(name = "trace_uri")
    private String uri;

    /**
     * 查询字符串原文
     * Raw query string.
     */
    @Column(name = "trace_query", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String query;

    /**
     * 是否命中有效返回
     * Whether the request hit a valid response.
     */
    @Builder.Default
    @Column(name = "is_found")
    private Boolean found = false;

    /**
     * 响应体大小（字节）
     * Response payload size in bytes.
     */
    @Builder.Default
    @Column(name = "response_size")
    private Integer responseSize = 0;

    /**
     * 处理耗时（毫秒）
     * Processing cost in milliseconds.
     */
    @Builder.Default
    @Column(name = "cost_ms")
    private Long costMs = 0L;

    /**
     * 关键字段快照（JSON 文本）
     * Snapshot of key fields (JSON text).
     */
    @Column(name = "key_fields", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String keyFields;
}
