/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 09:28:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-31 11:48:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null) {
            return "";
        }
        Iterator<String> iterator = list.iterator();  
        while(iterator.hasNext()){  
            String str = iterator.next();  
            if(!StringUtils.hasText(str)){  
                iterator.remove();
            }  
        }
        return String.join(",", list);
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        // 空串/空白串对应空列表；"".split(",") 会得到 [""]，导致空列表被读成含一个空白元素
        if (joined == null || joined.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(joined.split(","))
                .filter(s -> s != null && StringUtils.hasText(s))
                .map(s -> s.trim())
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
