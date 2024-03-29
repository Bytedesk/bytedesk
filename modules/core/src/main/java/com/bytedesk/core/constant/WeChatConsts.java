/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-26 12:21:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-02-26 12:21:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.constant;

/**
 *
 * @author xiaper.io
 */
public class WeChatConsts {

    // Prevents instantiation
    private WeChatConsts() {
    }

    // 开放平台
    public static String WECHAT_OPEN_PLATFORM_COMPONENT_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";

    public static String WECHAT_OPEN_PLATFORM_COMPONENT_PRECODE = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=";

    public static String WECHAT_OPEN_PLATFORM_COMPONENT_AUTH_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=";

    /**
     * 小程序
     */
    public static String WECHAT_MINI_PUSH_URL = "https://wechat.bytedesk.com/wechat/mini/push";

    /**
     * 公众号-》设置 -》基本配置-》服务器配置-》URL
     */
    public static String WECHAT_MP_PUSH_URL = "https://wechat.bytedesk.com/wechat/mp/push";

    /**
     * 获取access_token
     * 
     * @see <a href=
     *      "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140183">token</a>
     */
    public static String WECHAT_MINI_GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    // https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/mp-access-token/getStableAccessToken.html
    public static String WECHAT_MINI_GET_STABLE_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/stable_token";

    /**
     * 微信公众号获取用户信息接口URL
     * 文档：<a href=
     * "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140839">url</a>
     */
    public static String WECHAT_MP_USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=";
    /**
     * 微信开放平台定时刷新授权的公众号的access_token
     * 文档：<a href=
     * "https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1453779503&token=79a5d4fc45cccdf0a2d4001607a8beb6624fc7bb&lang=zh_CN"></a>
     * 5、获取（刷新）授权公众号或小程序的接口调用凭据（令牌）
     */
    public static String WECHAT_MP_REFESH_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=";
    /**
     * 公众号发送消息URL
     * 文档：<a href=
     * "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140547">url</a>
     */
    public static String WECHAT_MP_MESSAGE_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
    /**
     * <a href=
     * "https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1498704804_iARAL&token=05cb036b8a640b99a9f381172d71bbffcbe10494&lang=zh_CN">url</a>
     * 3、将公众号/小程序从开放平台帐号下解绑
     * http请求方式:
     * POST（请使用https协议）https://api.weixin.qq.com/cgi-bin/open/unbind?access_token=xxxx
     */
    public static String WECHAT_MP_UNBIND_URL = "https://api.weixin.qq.com/cgi-bin/open/unbind?access_token=";
    /**
     * 新增临时素材，要发送图片、语音和视频时，需要通过此url提前上传素材
     * <a href=
     * "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738726">upload</a>
     * 栗子：<a href=
     * "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE">example</a>
     */
    public static String WECHAT_MP_UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";
    /**
     * 新增永久素材
     * <a href=
     * "https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1444738729"></a>
     */
    public static String WECHAT_MP_UPLOAD_NEWS_URL = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=";
    /**
     * 获取临时素材, 收到语音、视频消息之后，通过此url下载
     */
    public static String WECHAT_MP_DOWNLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=";

}
