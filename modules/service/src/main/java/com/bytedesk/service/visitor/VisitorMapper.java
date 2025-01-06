/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-12-25 11:49:50
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 11:52:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

public class VisitorMapper {

    private final ModelMapper modelMapper;

    public VisitorMapper() {
        this.modelMapper = new ModelMapper();
        configureMappings();
    }

    // 创建一个自定义的 PropertyMap，在映射过程中检查源对象的字段是否为 null，如果是，则使用目标对象的默认值。
    private void configureMappings() {
        modelMapper.addMappings(new PropertyMap<VisitorRequest, VisitorEntity>() {
            @Override
            protected void configure() {
                // 如果 visitorRequest 的字段为 null，则使用 visitorEntity 的默认值
                // using(ctx -> ctx.getSource() != null ? ctx.getSource() : ((VisitorEntity) ctx.getDestination()).getDefaultValue())
                //     .map(source.getNickname(), destination.getNickname());
                
                // using(ctx -> ctx.getSource() != null ? ctx.getSource() : ((VisitorEntity) ctx.getDestination()).getDefaultAvatar())
                //     .map(source.getAvatar(), destination.getAvatar());
                
                // 继续为其他字段添加类似的映射
            }
        });
    }

    public VisitorEntity map(VisitorRequest visitorRequest) {
        return modelMapper.map(visitorRequest, VisitorEntity.class);
    }
}