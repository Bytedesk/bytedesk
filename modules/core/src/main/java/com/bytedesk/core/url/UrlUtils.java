/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-08 09:59:48
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 10:15:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.url;

import java.nio.charset.Charset;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class UrlUtils {

    // 将长url转换为短url
    public static String to62url(String longUrl) {
        //
        int size = 1000000;// 预计要插入多少数据
        double fpp = 0.001;// 期望的误判率
        BloomFilter<Integer> bloomFilter = BloomFilter.create(Funnels.integerFunnel(), size, fpp);
        //
        // MurmurHash算法
        HashFunction function = Hashing.murmur3_32_fixed();
        HashCode hashCode = function.hashString(longUrl, Charset.forName("utf-8"));
        // i为长url的murmur值
        int i = Math.abs(hashCode.asInt());
        // 准备一个url在生成的murmur值重复时拼接字符串用
        String newUrl = longUrl;
        // bo如果为true说明布隆过滤器中已存在
        boolean bo = bloomFilter.mightContain(i);
        while (bo) {
            newUrl += "ALREADY";
            hashCode = function.hashString(newUrl, Charset.forName("utf-8"));
            // 使用拼接过字符串的url重新生成murmur值
            i = Math.abs(hashCode.asInt());
            bo = bloomFilter.mightContain(i);
        }
        // 将murmur值放入布隆过滤器
        bloomFilter.put(i);
        // 转成62进制位数更短
        String to62url = encode(i);
        return to62url;
    }

    // 将目标转换为62进制 位数更短
    private static String encode(long num) {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int scale = 62;
        StringBuilder sb = new StringBuilder();
        int remainder = 0;
        while (num > scale - 1) {
            remainder = Long.valueOf(num % scale).intValue();
            sb.append(chars.charAt(remainder));
            num = num / scale;
        }
        sb.append(chars.charAt(Long.valueOf(num).intValue()));
        return sb.reverse().toString();
    }

}
