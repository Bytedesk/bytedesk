/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-22 17:05:17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

/**
 * 头像常量, 可以去这里获取 https://www.iconfont.cn/
 *
 * @author bytedesk.com
 */
public class AvatarConsts {

    // EffectiveJava Item 22
    // Prevents instantiation
    private AvatarConsts() {}

    // TODO: 从配置文件获取, 便于局域网访问， 默认使用 weiyuai 的 CDN
    public static final String DEFAULT_HOST = "https://cdn.weiyuai.cn";

    public static final String DEFAULT_AVATAR_URL = DEFAULT_HOST + "/avatars/admin.png";

    public static final String DEFAULT_AGENT_AVATAR_URL = DEFAULT_HOST + "/avatars/agent.png";

    public static final String DEFAULT_VISITOR_AVATAR_URL = DEFAULT_HOST + "/avatars/visitor_default_avatar.png";

    public static final String DEFAULT_UNIAPP_AVATAR_URL = DEFAULT_HOST + "/avatars/uniapp.png";

    public static final String DEFAULT_USER_AVATAR_URL = DEFAULT_HOST + "/avatars/user.png";

    public static final String DEFAULT_WEB_AVATAR_URL = DEFAULT_HOST + "/avatars/chrome.png";

    public static final String DEFAULT_WAP_AVATAR_URL = DEFAULT_HOST + "/avatars/chrome.png";

    public static final String DEFAULT_WECHAT_MP_AVATAR_URL = DEFAULT_HOST + "/avatars/wechat.png";

    public static final String DEFAULT_WECHAT_MINI_AVATAR_URL = DEFAULT_HOST + "/avatars/wechat.png";

    public static final String DEFAULT_WECHAT_KEFU_AVATAR_URL = DEFAULT_HOST + "/avatars/wechat.png";

    public static final String DEFAULT_ANDROID_AVATAR_URL = DEFAULT_HOST + "/avatars/android.png";

    public static final String DEFAULT_IOS_AVATAR_URL = DEFAULT_HOST + "/avatars/apple.png";

    public static final String DEFAULT_FLUTTER_ANDROID_AVATAR_URL = DEFAULT_HOST + "/avatars/android.png";

    public static final String DEFAULT_FLUTTER_IOS_AVATAR_URL = DEFAULT_HOST + "/avatars/apple.png";

    public static final String DEFAULT_FLUTTER_WEB_AVATAR_URL = DEFAULT_HOST + "/avatars/chrome.png";

    public static final String DEFAULT_SYSTEM_AVATAR_URL = DEFAULT_HOST + "/avatars/system_default_avatar.png";

    public static final String DEFAULT_WORK_GROUP_AVATAR_URL = DEFAULT_HOST + "/avatars/workgroup_default_avatar.png";

    public static final String DEFAULT_GROUP_AVATAR_URL = DEFAULT_HOST + "/avatars/group.png";
    
    public static final String DEFAULT_FILE_ASSISTANT_AVATAR_URL = DEFAULT_HOST + "/avatars/file.png";
    
    public static final String DEFAULT_CLIPBOARD_ASSISTANT_AVATAR_URL = DEFAULT_HOST + "/avatars/clipboard.png";
    
    public static final String DEFAULT_SYSTEM_NOTIFICATION_AVATAR_URL = DEFAULT_HOST + "/avatars/notification.png";

    public static final String LLM_THREAD_DEFAULT_AVATAR = DEFAULT_HOST + "/assets/images/llm/provider/zhipu.png";

    public static final String LLM_THREAD_DEFAULT_AVATAR_BASE_URL = DEFAULT_HOST + "/assets/images/llm/provider/";

    public static final String DEFAULT_ROBOT_AVATAR = DEFAULT_HOST + "/avatars/robot.png";

    /**
     * https://cdn.weiyuai.cn/avatars/girl.png
     * https://cdn.weiyuai.cn/avatars/boy.png
     */
    

}
