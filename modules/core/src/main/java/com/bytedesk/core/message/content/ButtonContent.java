/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-19 16:16:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-19 16:30:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 按钮内容类
 * 用于构建Messenger平台的各种按钮类型
 * 
 * 支持的按钮类型：
 * - web_url: 网页按钮
 * - postback: 回发按钮
 * - phone_number: 电话按钮
 * - game_play: 游戏开始按钮
 * - account_link: 登录按钮
 * - account_unlink: 注销按钮
 * - extension: 扩展按钮
 * 
 * 使用示例：
 * 
 * 1. 创建网页按钮：
 *    ButtonContent webButton = ButtonContent.createWebUrlButton("访问网站", "https://example.com", "full");
 * 
 * 2. 创建回发按钮：
 *    ButtonContent postbackButton = ButtonContent.createPostbackButton("开始", "GET_STARTED_PAYLOAD");
 * 
 * 3. 创建电话按钮：
 *    ButtonContent phoneButton = ButtonContent.createPhoneButton("联系我们", "+1234567890");
 * 
 * 4. 创建游戏按钮：
 *    ButtonContent gameButton = ButtonContent.createGamePlayButtonWithPlayer("开始游戏", "{\"game_id\":\"123\"}", "player_456");
 * 
 * 5. 验证按钮配置：
 *    List<ButtonContent&gt; buttons = Arrays.asList(webButton, postbackButton, phoneButton);
 *    ButtonContent.ValidationResult result = ButtonContent.validateButtons(buttons);
 *    if (result.isValid()) {
 *        // 按钮配置有效
 *    } else {
 *        // 处理错误
 *        System.out.println(result.getErrorMessage());
 *    }
 * 
 * 6. 转换为Map格式（用于API调用）：
 *    List&lt;Map&lt;String, Object&gt;&gt; buttonMaps = ButtonContent.toMapList(buttons);
 *    metaMessageService.sendButtonTemplateMessage(pageId, recipientId, text, buttonMaps, token);
 * 
 * 7. 使用Builder模式创建复杂按钮：
 *    ButtonContent complexButton = ButtonContent.builder()
 *        .type("web_url")
 *        .title("复杂按钮")
 *        .url("https://example.com")
 *        .webviewHeightRatio("tall")
 *        .messengerExtensions(true)
 *        .fallbackUrl("https://fallback.com")
 *        .build();
 * 
 * 优势：
 * - 类型安全：编译时检查按钮类型
 * - 配置验证：自动验证必填字段
 * - 代码可读性：清晰的API设计
 * - 维护性：集中管理按钮配置
 * - 扩展性：易于添加新的按钮类型
 * - 兼容性：自动转换为Map格式
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ButtonContent extends BaseContent {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 按钮类型
     * 必填字段
     */
    private String type;
    
    /**
     * 按钮标题
     * 必填字段，最多20个字符
     */
    private String title;
    
    /**
     * 按钮载荷
     * 用于postback、phone_number、game_play类型
     */
    private String payload;
    
    /**
     * URL地址
     * 用于web_url、account_link、extension类型
     */
    private String url;
    
    /**
     * WebView高度比例
     * 用于web_url、extension类型
     * 可选值：compact, tall, full
     */
    private String webviewHeightRatio;
    
    /**
     * 是否启用Messenger扩展
     * 用于web_url类型
     */
    private Boolean messengerExtensions;
    
    /**
     * 备用URL
     * 用于web_url、extension类型
     */
    private String fallbackUrl;
    
    /**
     * WebView分享按钮设置
     * 用于web_url类型
     * 可选值：hide
     */
    private String webviewShareButton;
    
    /**
     * 视图样式
     * 用于extension类型
     * 可选值：full, compact, tall
     */
    private String viewStyle;
    
    /**
     * 是否启用分享按钮
     * 用于extension类型
     */
    private Boolean enableShareButton;
    
    /**
     * 游戏元数据
     * 用于game_play类型
     */
    private GameMetadata gameMetadata;
    
    /**
     * 时区
     * 用于account_link类型
     */
    private String timezone;
    
    /**
     * 游戏元数据类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameMetadata {
        /**
         * 玩家ID
         */
        private String playerId;
        
        /**
         * 上下文ID
         */
        private String contextId;
    }
    
    /**
     * 创建网页按钮
     * 
     * @param title 按钮标题
     * @param url URL地址
     * @param webviewHeightRatio WebView高度比例
     * @return ButtonContent实例
     */
    public static ButtonContent createWebUrlButton(String title, String url, String webviewHeightRatio) {
        return ButtonContent.builder()
                .type("web_url")
                .title(title)
                .url(url)
                .webviewHeightRatio(webviewHeightRatio)
                .build();
    }
    
    /**
     * 创建网页按钮（带Messenger扩展）
     * 
     * @param title 按钮标题
     * @param url URL地址
     * @param webviewHeightRatio WebView高度比例
     * @param messengerExtensions 是否启用Messenger扩展
     * @param fallbackUrl 备用URL
     * @return ButtonContent实例
     */
    public static ButtonContent createWebUrlButtonWithExtensions(String title, String url, 
            String webviewHeightRatio, Boolean messengerExtensions, String fallbackUrl) {
        return ButtonContent.builder()
                .type("web_url")
                .title(title)
                .url(url)
                .webviewHeightRatio(webviewHeightRatio)
                .messengerExtensions(messengerExtensions)
                .fallbackUrl(fallbackUrl)
                .build();
    }
    
    /**
     * 创建回发按钮
     * 
     * @param title 按钮标题
     * @param payload 载荷
     * @return ButtonContent实例
     */
    public static ButtonContent createPostbackButton(String title, String payload) {
        return ButtonContent.builder()
                .type("postback")
                .title(title)
                .payload(payload)
                .build();
    }
    
    /**
     * 创建电话按钮
     * 
     * @param title 按钮标题
     * @param phoneNumber 电话号码
     * @return ButtonContent实例
     */
    public static ButtonContent createPhoneButton(String title, String phoneNumber) {
        return ButtonContent.builder()
                .type("phone_number")
                .title(title)
                .payload(phoneNumber)
                .build();
    }
    
    /**
     * 创建游戏开始按钮
     * 
     * @param title 按钮标题
     * @param payload 载荷
     * @return ButtonContent实例
     */
    public static ButtonContent createGamePlayButton(String title, String payload) {
        return ButtonContent.builder()
                .type("game_play")
                .title(title)
                .payload(payload)
                .build();
    }
    
    /**
     * 创建游戏开始按钮（带玩家ID）
     * 
     * @param title 按钮标题
     * @param payload 载荷
     * @param playerId 玩家ID
     * @return ButtonContent实例
     */
    public static ButtonContent createGamePlayButtonWithPlayer(String title, String payload, String playerId) {
        GameMetadata metadata = new GameMetadata(playerId, null);
        return ButtonContent.builder()
                .type("game_play")
                .title(title)
                .payload(payload)
                .gameMetadata(metadata)
                .build();
    }
    
    /**
     * 创建游戏开始按钮（带上下文ID）
     * 
     * @param title 按钮标题
     * @param payload 载荷
     * @param contextId 上下文ID
     * @return ButtonContent实例
     */
    public static ButtonContent createGamePlayButtonWithContext(String title, String payload, String contextId) {
        GameMetadata metadata = new GameMetadata(null, contextId);
        return ButtonContent.builder()
                .type("game_play")
                .title(title)
                .payload(payload)
                .gameMetadata(metadata)
                .build();
    }
    
    /**
     * 创建登录按钮
     * 
     * @param url 授权URL
     * @return ButtonContent实例
     */
    public static ButtonContent createLogInButton(String url) {
        return ButtonContent.builder()
                .type("account_link")
                .url(url)
                .build();
    }
    
    /**
     * 创建注销按钮
     * 
     * @return ButtonContent实例
     */
    public static ButtonContent createLogOutButton() {
        return ButtonContent.builder()
                .type("account_unlink")
                .build();
    }
    
    /**
     * 创建扩展按钮
     * 
     * @param title 按钮标题
     * @param url URL地址
     * @param viewStyle 视图样式
     * @return ButtonContent实例
     */
    public static ButtonContent createExtensionButton(String title, String url, String viewStyle) {
        return ButtonContent.builder()
                .type("extension")
                .title(title)
                .url(url)
                .viewStyle(viewStyle)
                .build();
    }
    
    /**
     * 创建扩展按钮（完整配置）
     * 
     * @param title 按钮标题
     * @param url URL地址
     * @param viewStyle 视图样式
     * @param fallbackUrl 备用URL
     * @param enableShareButton 是否启用分享按钮
     * @return ButtonContent实例
     */
    public static ButtonContent createExtensionButtonFull(String title, String url, String viewStyle, 
            String fallbackUrl, Boolean enableShareButton) {
        return ButtonContent.builder()
                .type("extension")
                .title(title)
                .url(url)
                .viewStyle(viewStyle)
                .fallbackUrl(fallbackUrl)
                .enableShareButton(enableShareButton)
                .build();
    }
    
    /**
     * 转换为Map结构
     * 用于与现有API兼容
     * 
     * @return Map结构
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        // 必填字段
        map.put("type", type);
        
        // 根据类型添加相应字段
        switch (type) {
            case "web_url":
                map.put("url", url);
                map.put("title", title);
                if (webviewHeightRatio != null) {
                    map.put("webview_height_ratio", webviewHeightRatio);
                }
                if (messengerExtensions != null) {
                    map.put("messenger_extensions", messengerExtensions);
                }
                if (fallbackUrl != null) {
                    map.put("fallback_url", fallbackUrl);
                }
                if (webviewShareButton != null) {
                    map.put("webview_share_button", webviewShareButton);
                }
                break;
                
            case "postback":
                map.put("title", title);
                map.put("payload", payload);
                break;
                
            case "phone_number":
                map.put("title", title);
                map.put("payload", payload);
                break;
                
            case "game_play":
                map.put("title", title);
                map.put("payload", payload);
                if (gameMetadata != null) {
                    Map<String, Object> metadataMap = new HashMap<>();
                    if (gameMetadata.getPlayerId() != null) {
                        metadataMap.put("player_id", gameMetadata.getPlayerId());
                    }
                    if (gameMetadata.getContextId() != null) {
                        metadataMap.put("context_id", gameMetadata.getContextId());
                    }
                    if (!metadataMap.isEmpty()) {
                        map.put("game_metadata", metadataMap);
                    }
                }
                break;
                
            case "account_link":
                map.put("url", url);
                break;
                
            case "account_unlink":
                // 无需额外字段
                break;
                
            case "extension":
                map.put("url", url);
                map.put("title", title);
                if (viewStyle != null) {
                    map.put("view_style", viewStyle);
                }
                if (fallbackUrl != null) {
                    map.put("fallback_url", fallbackUrl);
                }
                if (enableShareButton != null) {
                    map.put("enable_share_button", enableShareButton);
                }
                break;
        }
        
        return map;
    }
    
    /**
     * 验证按钮配置是否有效
     * 
     * @return 验证结果
     */
    public boolean isValid() {
        if (type == null || type.isEmpty()) {
            return false;
        }
        
        switch (type) {
            case "web_url":
                return title != null && !title.isEmpty() && url != null && !url.isEmpty();
            case "postback":
                return title != null && !title.isEmpty() && payload != null && !payload.isEmpty();
            case "phone_number":
                return title != null && !title.isEmpty() && payload != null && !payload.isEmpty();
            case "game_play":
                return title != null && !title.isEmpty() && payload != null && !payload.isEmpty();
            case "account_link":
                return url != null && !url.isEmpty();
            case "account_unlink":
                return true; // 无需额外字段
            case "extension":
                return title != null && !title.isEmpty() && url != null && !url.isEmpty();
            default:
                return false;
        }
    }
    
    /**
     * 获取按钮类型描述
     * 
     * @return 类型描述
     */
    public String getTypeDescription() {
        switch (type) {
            case "web_url":
                return "网页按钮";
            case "postback":
                return "回发按钮";
            case "phone_number":
                return "电话按钮";
            case "game_play":
                return "游戏开始按钮";
            case "account_link":
                return "登录按钮";
            case "account_unlink":
                return "注销按钮";
            case "extension":
                return "扩展按钮";
            default:
                return "未知按钮类型";
        }
    }
    
    /**
     * 将ButtonContent列表转换为Map列表
     * 用于与现有API兼容
     * 
     * @param buttons ButtonContent列表
     * @return Map列表
     */
    public static List<Map<String, Object>> toMapList(List<ButtonContent> buttons) {
        if (buttons == null || buttons.isEmpty()) {
            return new ArrayList<>();
        }
        
        return buttons.stream()
            .map(ButtonContent::toMap)
            .collect(Collectors.toList());
    }
    
    /**
     * 验证ButtonContent列表中的所有按钮
     * 
     * @param buttons ButtonContent列表
     * @return 验证结果，包含无效按钮的信息
     */
    public static ValidationResult validateButtons(List<ButtonContent> buttons) {
        ValidationResult result = new ValidationResult();
        
        if (buttons == null || buttons.isEmpty()) {
            result.setValid(false);
            result.addError("按钮列表为空");
            return result;
        }
        
        for (int i = 0; i < buttons.size(); i++) {
            ButtonContent button = buttons.get(i);
            if (button == null) {
                result.setValid(false);
                result.addError("第" + (i + 1) + "个按钮为null");
                continue;
            }
            
            if (!button.isValid()) {
                result.setValid(false);
                result.addError("第" + (i + 1) + "个按钮配置无效: " + 
                    button.getTypeDescription() + " - " + button.getTitle());
            }
        }
        
        return result;
    }
    
    /**
     * 验证结果类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResult {
        private boolean valid = true;
        private List<String> errors = new ArrayList<>();
        
        public void addError(String error) {
            this.errors.add(error);
        }
        
        public String getErrorMessage() {
            if (errors.isEmpty()) {
                return "验证通过";
            }
            return String.join("; ", errors);
        }
    }
    
    /**
     * 从JSON字符串反序列化为ButtonContent对象
     * @param json JSON字符串
     * @return ButtonContent对象，如果解析失败返回null
     */
    public static ButtonContent fromJson(String json) {
        return BaseContent.fromJson(json, ButtonContent.class);
    }
}
