/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-16 11:54:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-24 22:57:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.utils;

import java.util.List;

import org.springframework.util.StringUtils;

import com.github.houbb.pinyin.bs.PinyinBs;
import com.github.houbb.pinyin.support.style.PinyinToneStyles;

import lombok.experimental.UtilityClass;

/**
 * 高性能中文拼音转换工具
 * 
 * https://github.com/houbb/pinyin
 * https://mvnrepository.com/artifact/com.github.houbb/pinyin
 * https://github.com/houbb/pinyin/blob/master/src/test/java/com/github/houbb/pinyin/test/util/PinyinHelperTest.java
 * 
 */
@UtilityClass
public class BdPinyinUtils {

    /**
     * 普通格式, 没有声调
     * 
     * @since 0.0.3
     */
    public static String toPinYin(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        
        // 检查是否包含中文字符
        boolean containsChinese = false;
        for (char c : text.toCharArray()) {
            if (isChinese(c)) {
                containsChinese = true;
                break;
            }
        }
        
        // 如果不包含中文，直接返回原始文本
        if (!containsChinese) {
            return text;
        }
        
        // 转换为拼音
        return PinyinBs.newInstance().style(PinyinToneStyles.normal()).toPinyin(text);
    }

    /**
     * 判断字符是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || 
               ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
               ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B;
    }
    
    /**
     * 获取字符串的拼音首字母
     */
    public static String getFirstLetters(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        
        return PinyinBs.newInstance().style(PinyinToneStyles.firstLetter()).toPinyin(text);
    }

    /**
     * 拼音转换测试，带声调
     * 
     * @since 0.0.1
     */
    public static String toPinyinWithShengDiao(String text) {
        // String text = "我爱中文";
        PinyinBs pinyinBs = PinyinBs.newInstance();
        return pinyinBs.toPinyin(text);
        // Assert.assertEquals("wǒ ài zhōng wén", pinyin);
    }

    /**
     * 返回多音字列表测试
     * 
     * @since 0.0.2
     */
    public static List<String> toPinyinList(char c) {
        // final char c = '重';
        return PinyinBs.newInstance().toPinyinList(c);
        // Assert.assertEquals("[zhòng, chóng, tóng]", pinyinList.toString());
    }

    /**
     * 首字母格式
     * 
     * @since 0.0.3
     */
    public static String firstLetterStyle(String text) {
        // final String text = "我爱中文";
        return PinyinBs.newInstance().style(PinyinToneStyles.firstLetter()).toPinyin(text);
        // Assert.assertEquals("w a z w", pinyin);
    }
    
}
