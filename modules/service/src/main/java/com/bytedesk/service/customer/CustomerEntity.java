/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:52:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 11:07:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.customer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 客户留资，自动提取，手动添加
 * TODO: 同步存储visitor备注信息
 * @author jackning 270580156@qq.com
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_customer")
public class CustomerEntity extends BaseEntity {

    @NotBlank(message = "name is required")
    @Column(nullable = false)
    private String nickname;

    private String email;

    private String mobile;

    // 备注信息note
    @Builder.Default
    private String description = I18Consts.I18N_DESCRIPTION;
    
    // 个人基本信息
    private String realname; // 真实姓名
    private String avatar; // 头像URL
    private String gender; // 性别
    private String birthday; // 出生日期
    
    // 额外联系方式
    private String wechat; // 微信
    private String qq; // QQ号
    private String telephone; // 固定电话
    
    // 公司信息
    private String company; // 公司名称
    private String position; // 职位
    private String industry; // 所属行业
    
    // 地址信息
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String address; // 详细地址
    private String province; // 省份
    private String city; // 城市
    private String district; // 区/县
    private String zipCode; // 邮政编码
    
    // 客户分类和标签
    private String category; // 客户类别(个人/企业)
    private String crmlevel; // 客户级别(A/B/C/D), 重命名level为crmlevel
    private String priority; // 优先级(高/中/低)
    private String tags; // 标签，多个用逗号分隔
    
    // 客户来源和状态
    private String source; // 客户来源(网站/APP/电话/转介绍等)
    private String status; // 客户状态(潜在/活跃/流失等)
    
    // 跟进信息
    private String owner; // 负责人ID
    private String ownerName; // 负责人姓名
    private LocalDateTime lastContactTime; // 最后联系时间
    private LocalDateTime nextContactTime; // 下次联系时间
    
    // 交易相关
    private Double totalAmount; // 累计消费金额
    private Integer dealCount; // 成交次数
    private LocalDateTime lastDealTime; // 最后成交时间

    // 标签
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

	// extra info，customFields，开发者自定义URL参数，使用json格式存储，便于扩展
	@Builder.Default
	@Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String extra = BytedeskConsts.EMPTY_JSON_STRING;

    // 自定义字段存储
    // @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    // private String customFields; // JSON格式存储自定义字段
    
    // 备注信息
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String notes; // 备注信息
}
