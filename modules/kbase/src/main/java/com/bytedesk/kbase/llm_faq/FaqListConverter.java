/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-06 09:28:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-07 09:42:35
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_faq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FaqListConverter implements AttributeConverter<List<String>, String> {

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
        return String.join("|", list);
    }

    @Override
    public List<String> convertToEntityAttribute(String joined) {
        if (joined == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(joined.split("\\|")));
    }

}
