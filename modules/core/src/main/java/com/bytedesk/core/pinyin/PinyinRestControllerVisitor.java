/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-07-04 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-04 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.pinyin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.BdPinyinUtils;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.PinyinResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 汉字转拼音演示 API
 * 
 * https://github.com/houbb/pinyin
 * https://github.com/houbb/pinyin/blob/master/src/test/java/com/github/houbb/pinyin/test/util/PinyinHelperTest.java
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/pinyin")
@Tag(name = "Pinyin Conversion", description = "Chinese character to Pinyin conversion APIs")
public class PinyinRestControllerVisitor {

    /**
     * 将中文转为普通格式拼音（不带声调）
     * http://127.0.0.1:9003/visitor/api/v1/pinyin/normal?text=你好世界
     */
    @Operation(summary = "Normal Pinyin", description = "Convert Chinese characters to pinyin without tone marks")
    @GetMapping("/normal")
    public JsonResult<?> normalPinyin(@RequestParam String text) {
        Map<String, String> result = new HashMap<>();
        result.put("original", text);
        result.put("pinyin", BdPinyinUtils.toPinYin(text));
        return new JsonResult<>("normal pinyin", 200, result);
    }

    /**
     * 将中文转为带声调的拼音
     * http://127.0.0.1:9003/visitor/api/v1/pinyin/tone?text=你好世界
     */
    @Operation(summary = "Pinyin with Tone", description = "Convert Chinese characters to pinyin with tone marks")
    @GetMapping("/tone")
    public JsonResult<?> toneStylePinyin(@RequestParam String text) {
        Map<String, String> result = new HashMap<>();
        result.put("original", text);
        result.put("pinyin", BdPinyinUtils.toPinyinWithShengDiao(text));
        return new JsonResult<>("pinyin with tone", 200, result);
    }

    /**
     * 将中文转为首字母格式
     * http://127.0.0.1:9003/visitor/api/v1/pinyin/firstletter?text=你好世界
     */
    @Operation(summary = "First Letter", description = "Convert Chinese characters to first letter of pinyin")
    @GetMapping("/firstletter")
    public JsonResult<?> firstLetterPinyin(@RequestParam String text) {
        Map<String, String> result = new HashMap<>();
        result.put("original", text);
        result.put("pinyin", BdPinyinUtils.firstLetterStyle(text));
        return new JsonResult<>("first letter pinyin", 200, result);
    }

    /**
     * 获取单个汉字的多音字列表
     * http://127.0.0.1:9003/visitor/api/v1/pinyin/multiple?character=重
     */
    @Operation(summary = "Polyphone List", description = "Get all possible pinyin pronunciations for a single Chinese character")
    @GetMapping("/multiple")
    public JsonResult<?> multiplePinyin(@RequestParam String character) {
        Map<String, Object> result = new HashMap<>();
        if (character != null && !character.isEmpty()) {
            char c = character.charAt(0);
            List<String> pinyinList = BdPinyinUtils.toPinyinList(c);
            result.put("character", String.valueOf(c));
            result.put("pinyinList", pinyinList);
        } else {
            result.put("error", "请提供一个汉字");
        }
        return new JsonResult<>("polyphone list", 200, result);
    }

    /**
     * 数字声调格式（声调数字放在拼音末尾）
     * http://127.0.0.1:9003/visitor/api/v1/pinyin/numlast?text=你好世界
     */
    @Operation(summary = "Num-Last Pinyin", description = "Convert Chinese characters to pinyin with tone numbers at the end (e.g. ni3 hao3)")
    @GetMapping("/numlast")
    public JsonResult<?> numLastPinyin(@RequestParam String text) {
        Map<String, String> result = new HashMap<>();
        result.put("original", text);
        result.put("pinyin", BdPinyinUtils.toPinyinNumLast(text));
        return new JsonResult<>("num-last pinyin", 200, result);
    }

    /**
     * 判断字符串是否包含中文
     * http://127.0.0.1:9003/visitor/api/v1/pinyin/has-chinese?text=hello你好world
     */
    @Operation(summary = "Check Chinese", description = "Check if the input text contains Chinese characters")
    @GetMapping("/has-chinese")
    public JsonResult<?> hasChinese(@RequestParam String text) {
        Map<String, Object> result = new HashMap<>();
        result.put("original", text);
        result.put("containsChinese", BdPinyinUtils.containsChinese(text));
        return new JsonResult<>("check chinese", 200, result);
    }

    /**
     * 一次性返回多种拼音格式
     * http://127.0.0.1:9003/visitor/api/v1/pinyin/all?text=你好世界
     */
    @Operation(summary = "All Pinyin Formats", description = "Convert Chinese characters to all pinyin formats at once (normal, tone, firstLetter, numLast)")
    @GetMapping("/all")
    public JsonResult<?> allPinyin(@RequestParam String text) {
        PinyinResult pinyinResult = BdPinyinUtils.toPinyinAll(text);
        Map<String, Object> result = new HashMap<>();
        result.put("original", text);
        result.put("normal", pinyinResult.getNormal());
        result.put("tone", pinyinResult.getTone());
        result.put("firstLetter", pinyinResult.getFirstLetter());
        result.put("numLast", pinyinResult.getNumLast());
        return new JsonResult<>("all pinyin formats", 200, result);
    }
}
