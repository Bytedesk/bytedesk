package com.bytedesk.core.utils;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.rbac.user.User;

// @Slf4j
public class BdRedisUtils {

    // user
    public static JSONObject userToJSONObject(User user) {

        if (user == null) {
            return null;
        }
        //
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user.getId());
        jsonObject.put("uid", user.getUid());
        jsonObject.put("username", user.getUsername());
        jsonObject.put("nickname", user.getNickname());
        jsonObject.put("mobile", user.getMobile());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("avatar", user.getAvatar());
       
        jsonObject.put("description", user.getDescription());
        

        return jsonObject;
    }





}
