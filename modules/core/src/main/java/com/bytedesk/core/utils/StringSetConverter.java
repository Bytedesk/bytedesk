/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-15 16:14:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-15 16:14:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import com.google.common.base.Strings;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Converter
public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> set) {
        Iterator<String> iterator = set.iterator();  
        while(iterator.hasNext()){  
            String str = iterator.next();  
            if(Strings.isNullOrEmpty(str)){  
                iterator.remove();  // 正确
            }  
        }
        return String.join(",", set);
    }

    @Override
    public Set<String> convertToEntityAttribute(String joined) {
        return joined == null ? new HashSet<>() : new HashSet<>(Arrays.asList(joined.split(",")));
    }

}
