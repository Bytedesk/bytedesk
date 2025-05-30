/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-01 10:22:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 09:54:04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    // /**
    //  * ID 生成器
    //  */
    // private static final IdGenerator ID_GENERATOR = new DefaultIdGenerator();

    // /**
    //  * 根据时间戳，生成随机唯一id 如：202009151044291
    //  * 必须只能返回数字串，不能包含字母标点符号等
    //  *
    //  * @return 随机
    //  */
    // public static String timeSerialId() {
    //     return ID_GENERATOR.next();
    // }

    /**
     * UUID
     * 
     * @return UUID
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 带有时间戳的uuid
     * UUID.randomUUID().toString()长度为36，我们去掉'-'之后，截取前半段
     * 
     * @return uuid
     */
    // public static String timeUuid() {
    //     return ID_GENERATOR.next() + UUID.randomUUID().toString().replace("-", "");// .substring(0, 18);
    // }

    public static String getUid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String formatUid(String orgUid, String uid) {
        Assert.hasText(orgUid, "Organization UID must not be empty");
        Assert.hasText(uid, "UID must not be empty");
        return orgUid + "_" + uid;
    }


    /**
     * for test
     * 
     * @param mobile
     * @return
     */
    public static boolean isTestMobile(String mobile) {
        return mobile.startsWith("123");
    }

    public static boolean isTestEmail(String email) {
        return email.endsWith("@email.com");
    }

    /**
     * 
     * @return
     */
    public static String getRandomCode() {
        int min = 100001;
        int max = 999998;
        int code = new Random().nextInt(max) % (max - min + 1) + min;
        return String.valueOf(code);
    }

    public static String convertListToString(List<String> list) {
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

    /**
     * 将中文字符串转换为Unicode编码
     * @param str 需要转换的中文字符串
     * @return Unicode编码字符串
     */
    public static String convertToUnicode(String str) {
        if (str == null) {
            return null;
        }
        
        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            // 判断是否是ASCII码表中的字符
            if (c < 256) {
                unicode.append(c);
            } else {
                // 转换为Unicode
                unicode.append("\\u").append(String.format("%04x", (int) c));
            }
        }
        return unicode.toString();
    }
    
    /**
     * 判断字符串是否包含中文字符
     * @param str 要检查的字符串
     * @return true:包含中文字符 false:不包含中文字符
     */
    public static boolean containsChineseChar(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c > 255) {
                return true;
            }
        }
        return false;
    }



}
