/*
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.core.rbac.user;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.constant.BytedeskConsts;

import org.springframework.util.StringUtils;

/**
 * 消息作者 extra 扩展字段中职务快照（jobTitle）的读写工具。
 *
 * extra 为自由 JSON 字符串，可能为 null/空串/非 JSON/含未知业务键，
 * 本类只维护 "jobTitle" 一个键，读写均保留其余键不变；任何解析失败按无职务处理。
 */
public final class UserExtraJobTitleUtils {

    /** 职务快照在 user.extra JSON 中的键名 */
    public static final String KEY_JOB_TITLE = "jobTitle";

    private UserExtraJobTitleUtils() {
    }

    /**
     * 读取职务快照；extra 为 null/空串/非 JSON/缺键/非字符串值时返回空串。
     */
    public static String getJobTitle(String extra) {
        JSONObject object = parseObject(extra);
        if (object == null) {
            return "";
        }
        Object value = object.get(KEY_JOB_TITLE);
        return value instanceof String ? (String) value : "";
    }

    /**
     * 写入职务快照并保留未知键。
     * value 为空白时不写入该键；extra 无法解析为 JSON 对象时，以仅含职务键的新对象替换。
     */
    public static String putJobTitle(String extra, String value) {
        JSONObject object = parseObject(extra);
        if (object == null) {
            object = new JSONObject();
        }
        if (StringUtils.hasText(value)) {
            object.put(KEY_JOB_TITLE, value);
        }
        return object.toJSONString();
    }

    /**
     * 移除职务键并保留未知键（用于以库中快照覆盖客户端伪造值前清除旧值等场景）。
     */
    public static String removeJobTitle(String extra) {
        JSONObject object = parseObject(extra);
        if (object == null) {
            return BytedeskConsts.EMPTY_JSON_STRING;
        }
        object.remove(KEY_JOB_TITLE);
        return object.toJSONString();
    }

    private static JSONObject parseObject(String extra) {
        if (!StringUtils.hasText(extra)) {
            return null;
        }
        try {
            Object parsed = JSON.parse(extra);
            return parsed instanceof JSONObject ? (JSONObject) parsed : null;
        } catch (Exception e) {
            return null;
        }
    }
}
