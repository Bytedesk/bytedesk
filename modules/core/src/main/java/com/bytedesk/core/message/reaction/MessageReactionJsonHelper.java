package com.bytedesk.core.message.reaction;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.message.content.TextContent;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageReactionJsonHelper {

    public static String toggleReaction(String rawContent, String messageType, String emoji, String userUid, String userNickname, String userAvatar) {
        if (!StringUtils.hasText(emoji) || !StringUtils.hasText(userUid)) {
            return rawContent;
        }

        JSONObject obj = parseAsObject(rawContent);
        if (obj == null) {
            obj = new JSONObject();
            // 兼容历史纯文本：尽量不丢失原始内容
            if (StringUtils.hasText(rawContent)) {
                // TEXT/FAQ_QUESTION 等文本类常见
                obj.put("text", rawContent);
            } else {
                obj.put("text", "");
            }
        }

        JSONArray reactionsArr = obj.getJSONArray("reactions");
        if (reactionsArr == null) {
            reactionsArr = new JSONArray();
        }

        // 单选 reaction：同一用户对同一条消息最多只能存在一个 emoji。
        // - 点同一 emoji：视为取消（并清空该用户在所有 emoji 下的记录）
        // - 点不同 emoji：先清空旧记录，再添加新 emoji 记录
        String needle = emoji.trim();
        boolean alreadyOnNeedle = containsUserInEmoji(reactionsArr, needle, userUid);
        removeUserFromAllReactions(reactionsArr, userUid);

        if (!alreadyOnNeedle) {
            int idx = findReactionIndex(reactionsArr, needle);
            if (idx < 0) {
                JSONObject rec = new JSONObject();
                rec.put("emoji", needle);

                JSONArray userUidsArr = new JSONArray();
                userUidsArr.add(userUid);
                rec.put("userUids", dedupe(userUidsArr));

                JSONArray usersArr = new JSONArray();
                usersArr.add(buildUserObject(userUid, userNickname, userAvatar));
                rec.put("users", dedupeUsers(usersArr));

                reactionsArr.add(rec);
            } else {
                JSONObject rec = reactionsArr.getJSONObject(idx);
                if (rec == null) {
                    rec = new JSONObject();
                    rec.put("emoji", needle);
                }

                JSONArray userUidsArr = rec.getJSONArray("userUids");
                if (userUidsArr == null) {
                    userUidsArr = new JSONArray();
                }
                userUidsArr.add(userUid);

                JSONArray usersArr = rec.getJSONArray("users");
                if (usersArr == null) {
                    usersArr = new JSONArray();
                }
                removeUserObj(usersArr, userUid);
                usersArr.add(buildUserObject(userUid, userNickname, userAvatar));

                rec.put("userUids", dedupe(userUidsArr));
                rec.put("users", dedupeUsers(usersArr));
                reactionsArr.set(idx, rec);
            }
        }

        obj.put("reactions", reactionsArr);
        return obj.toJSONString();
    }

    private static boolean containsUserInEmoji(JSONArray reactionsArr, String emoji, String userUid) {
        if (reactionsArr == null || !StringUtils.hasText(emoji) || !StringUtils.hasText(userUid)) {
            return false;
        }
        int idx = findReactionIndex(reactionsArr, emoji.trim());
        if (idx < 0) {
            return false;
        }
        JSONObject rec = reactionsArr.getJSONObject(idx);
        if (rec == null) {
            return false;
        }
        JSONArray userUidsArr = rec.getJSONArray("userUids");
        if (containsUser(userUidsArr, userUid)) {
            return true;
        }
        JSONArray usersArr = rec.getJSONArray("users");
        return containsUserObj(usersArr, userUid);
    }

    private static boolean containsUserObj(JSONArray usersArr, String userUid) {
        if (usersArr == null || !StringUtils.hasText(userUid)) {
            return false;
        }
        String needle = userUid.trim().toLowerCase(Locale.ROOT);
        for (int i = 0; i < usersArr.size(); i++) {
            Object v = usersArr.get(i);
            if (!(v instanceof JSONObject jo)) {
                continue;
            }
            String uid = jo.getString("uid");
            if (uid != null && needle.equals(uid.trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static void removeUserFromAllReactions(JSONArray reactionsArr, String userUid) {
        if (reactionsArr == null || !StringUtils.hasText(userUid)) {
            return;
        }

        for (int i = reactionsArr.size() - 1; i >= 0; i--) {
            Object it = reactionsArr.get(i);
            if (!(it instanceof JSONObject rec)) {
                continue;
            }

            JSONArray userUidsArr = rec.getJSONArray("userUids");
            JSONArray usersArr = rec.getJSONArray("users");

            if (userUidsArr != null) {
                removeUser(userUidsArr, userUid);
            }
            if (usersArr != null) {
                removeUserObj(usersArr, userUid);
            }

            // 兼容：如果缺少 userUids 但存在 users，则派生 userUids，保证前端统计一致
            if ((userUidsArr == null || userUidsArr.isEmpty()) && usersArr != null && !usersArr.isEmpty()) {
                userUidsArr = new JSONArray();
                for (int j = 0; j < usersArr.size(); j++) {
                    Object u = usersArr.get(j);
                    if (!(u instanceof JSONObject jo)) {
                        continue;
                    }
                    String uid = jo.getString("uid");
                    if (StringUtils.hasText(uid)) {
                        userUidsArr.add(uid);
                    }
                }
            }

            // 去重并保持顺序
            if (userUidsArr != null) {
                userUidsArr = dedupe(userUidsArr);
                rec.put("userUids", userUidsArr);
            }
            if (usersArr != null) {
                usersArr = dedupeUsers(usersArr);
                rec.put("users", usersArr);
            }

            boolean hasAny = (userUidsArr != null && !userUidsArr.isEmpty()) || (usersArr != null && !usersArr.isEmpty());
            if (!hasAny) {
                reactionsArr.remove(i);
            } else {
                reactionsArr.set(i, rec);
            }
        }
    }

    private static JSONObject buildUserObject(String uid, String nickname, String avatar) {
        JSONObject u = new JSONObject();
        u.put("uid", uid);
        if (StringUtils.hasText(nickname)) {
            u.put("nickname", nickname);
        }
        if (StringUtils.hasText(avatar)) {
            u.put("avatar", avatar);
        }
        return u;
    }

    private static void removeUserObj(JSONArray usersArr, String userUid) {
        if (usersArr == null || !StringUtils.hasText(userUid)) {
            return;
        }
        String needle = userUid.trim().toLowerCase(Locale.ROOT);
        for (int i = usersArr.size() - 1; i >= 0; i--) {
            Object v = usersArr.get(i);
            if (!(v instanceof JSONObject jo)) {
                continue;
            }
            String uid = jo.getString("uid");
            if (uid != null && needle.equals(uid.trim().toLowerCase(Locale.ROOT))) {
                usersArr.remove(i);
            }
        }
    }

    private static JSONArray dedupeUsers(JSONArray arr) {
        if (arr == null || arr.isEmpty()) {
            return new JSONArray();
        }
        // 保留第一次出现的 uid，保持顺序
        LinkedHashSet<String> seen = new LinkedHashSet<>();
        JSONArray out = new JSONArray();
        for (int i = 0; i < arr.size(); i++) {
            Object v = arr.get(i);
            if (!(v instanceof JSONObject jo)) {
                continue;
            }
            String uid = jo.getString("uid");
            if (!StringUtils.hasText(uid)) {
                continue;
            }
            String key = uid.trim().toLowerCase(Locale.ROOT);
            if (seen.contains(key)) {
                continue;
            }
            seen.add(key);
            out.add(jo);
        }
        return out;
    }

    private static JSONObject parseAsObject(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        try {
            Object parsed = JSON.parse(raw);
            if (parsed instanceof JSONObject jo) {
                return jo;
            }
            // 兼容：某些旧 TEXT 可能是纯字符串，TextContent.fromJson 会兜底；这里也做一层
            TextContent text = TextContent.fromJson(raw);
            if (text != null && StringUtils.hasText(text.getText())) {
                return JSON.parseObject(text.toJson());
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static int findReactionIndex(JSONArray arr, String emoji) {
        if (arr == null || !StringUtils.hasText(emoji)) {
            return -1;
        }
        String needle = emoji.trim();
        for (int i = 0; i < arr.size(); i++) {
            Object it = arr.get(i);
            if (!(it instanceof JSONObject jo)) {
                continue;
            }
            String e = jo.getString("emoji");
            if (e != null && needle.equals(e.trim())) {
                return i;
            }
        }
        return -1;
    }

    private static boolean containsUser(JSONArray userUidsArr, String userUid) {
        if (userUidsArr == null || !StringUtils.hasText(userUid)) {
            return false;
        }
        String needle = userUid.trim().toLowerCase(Locale.ROOT);
        for (int i = 0; i < userUidsArr.size(); i++) {
            Object v = userUidsArr.get(i);
            if (v == null) {
                continue;
            }
            if (needle.equals(String.valueOf(v).trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static void removeUser(JSONArray userUidsArr, String userUid) {
        if (userUidsArr == null || !StringUtils.hasText(userUid)) {
            return;
        }
        String needle = userUid.trim().toLowerCase(Locale.ROOT);
        for (int i = userUidsArr.size() - 1; i >= 0; i--) {
            Object v = userUidsArr.get(i);
            if (v == null) {
                continue;
            }
            if (needle.equals(String.valueOf(v).trim().toLowerCase(Locale.ROOT))) {
                userUidsArr.remove(i);
            }
        }
    }

    private static JSONArray dedupe(JSONArray arr) {
        if (arr == null || arr.isEmpty()) {
            return new JSONArray();
        }
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (int i = 0; i < arr.size(); i++) {
            Object v = arr.get(i);
            if (v == null) {
                continue;
            }
            String s = String.valueOf(v).trim();
            if (StringUtils.hasText(s)) {
                set.add(s);
            }
        }
        List<String> list = new ArrayList<>(set);
        return new JSONArray(list);
    }
}
