/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-30 09:14:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-27 09:34:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.config.properties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(BytedeskProperties.CONFIG_PREFIX)
public class BytedeskProperties {

    public static final String CONFIG_PREFIX = "bytedesk";

    private static volatile BytedeskProperties instance; // 使用volatile关键字确保可见性

    @PostConstruct
    public void init() {
        if (instance == null) {
            synchronized (BytedeskProperties.class) {
                if (instance == null) {
                    // 处理所有可能包含中文的字段
                    try {
                        // 处理 Custom 相关字段
                        if (this.custom != null) {
                            this.custom.setName(handleChineseText(this.custom.getName(), "BYTEDESK_CUSTOM_NAME"));
                            this.custom.setDescription(handleChineseText(this.custom.getDescription(), "BYTEDESK_CUSTOM_DESCRIPTION"));
                            this.custom.setLogo(handleChineseText(this.custom.getLogo(), "BYTEDESK_CUSTOM_LOGO"));
                        }

                        // 处理 Admin 相关字段
                        if (this.admin != null) {
                            this.admin.setNickname(handleChineseText(this.admin.getNickname(), "BYTEDESK_ADMIN_NICKNAME"));
                        }

                        // 处理 Organization 相关字段
                        if (this.organization != null) {
                            this.organization.setName(handleChineseText(this.organization.getName(), "BYTEDESK_ORGANIZATION_NAME"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    instance = this;
                }
            }
        }
    }

    /**
     * 处理可能包含中文的文本
     * 
     * @param text 原始文本
     * @param envKey 对应的环境变量key
     * @return 处理后的文本
     */
    private String handleChineseText(String text, String envKey) {
        if (!StringUtils.hasText(text)) {
            return text;
        }

        try {
            // 检查是否包含 Unicode 转义序列
            if (text.contains("\\u")) {
                // 处理 Unicode 转义序列
                StringBuilder sb = new StringBuilder();
                int len = text.length();
                for (int i = 0; i < len; i++) {
                    char c = text.charAt(i);
                    if (c == '\\' && i + 1 < len && text.charAt(i + 1) == 'u') {
                        String hex = text.substring(i + 2, i + 6);
                        c = (char) Integer.parseInt(hex, 16);
                        i += 5;
                    }
                    sb.append(c);
                }
                return sb.toString();
            }

            // 检查是否来自环境变量（Docker）
            String envValue = System.getenv(envKey);
            if (envValue != null && envValue.equals(text)) {
                // 如果是来自Docker环境变量，使用UTF-8解码
                return new String(text.getBytes(), "UTF-8");
            }

            // 如果是来自properties文件，使用ISO-8859-1到UTF-8的转换
            return new String(text.getBytes("ISO-8859-1"), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }

    public static BytedeskProperties getInstance() {
        return instance;
    }

    private Boolean debug;

    // private String edition = Edition.COMMUNITY.name().toLowerCase();

    private String version;

    private String appkey;

    // 自定义配置
    private Custom custom = new Custom();

    // 管理员配置
    private Admin admin = new Admin();

    // 成员配置
    private Member member = new Member();

    // 性能测试配置
    private Testing testing = new Testing();
    
    // 组织配置
    private Organization organization = new Organization();

    // 功能开关配置
    private Features features = new Features();

    // CORS配置
    private Cors cors = new Cors();

    // JWT配置 
    private Jwt jwt = new Jwt();

    // 缓存配置
    private Cache cache = new Cache();

    // 上传配置
    private Upload upload = new Upload();

    // 集群配置
    private Cluster cluster = new Cluster();

    // 知识库配置
    private Kbase kbase = new Kbase();

    // 阿里云配置
    private Aliyun aliyun = new Aliyun();

    // 腾讯云配置
    private Tencent tencent = new Tencent();

    // 微信支付配置
    private WechatPay wechatPay = new WechatPay();

    public static enum Edition {
        COMMUNITY, // 社区版-不限人，免费, 功能受限
        ENTERPRISE, // 企业版-不限人，付费，功能不限
        PLATFORM // 平台版-不限人数，付费，功能不限
    }

    @Data
    public static class Custom {
        private Boolean enabled = false;
        private String name;
        private String logo;
        private String description;
        private Boolean showRightCornerChat = true;
        private String privacyPolicyUrl;
        private String termsOfServiceUrl;
        private Boolean loginUsernameEnable = true;
        private Boolean loginMobileEnable = true;
        private Boolean loginEmailEnable = false;
        private Boolean loginScanEnable = false;
        private Boolean docUrlShow = true;
        private String docUrl;
    }

    @Data
    public static class Admin {
        private String email;
        private String password;
        // private String passwordDefault;
        private String nickname;
        private String mobile;
        private List<String> mobileWhitelist = new ArrayList<>();
        private List<String> emailWhitelist = new ArrayList<>();
        private String validateCode;
        private Boolean allowRegister;
        private Boolean forceValidateMobile;
        private Boolean forceValidateEmail;
    }

    @Data
    public static class Member {
        private String password;
    }

    @Data
    private static class Testing {
        private Boolean enabled = false;
        private Integer accountCount = 300;
        private String accountUsername = "test";
        private String accountPassword = "password";
        private Boolean disableCaptcha = true;
        private Boolean disableIpFilter = true;
    }
    @Data 
    public static class Organization {
        private String name;
        private String code;
    }

    @Data
    public static class Features {
        private Boolean javaAi = false;
        private Boolean pythonAi = true;
        private String emailType = "javamail";
        private Date startDate;
        private Integer freeDays = 30;
        private String avatarBaseUrl;
    }

    @Data
    public static class Cors {
        private String allowedOrigins;
    }

    @Data
    public static class Jwt {
        private String secretKey;
        private String expiration;
        private String refreshTokenExpiration;
    }

    @Data
    public static class Cache {
        private Integer level;
        private String prefix;
        private String redisStreamKey;
    }

    @Data
    public static class Upload {
        private String type;
        private String dir;
        private String url;
    }

    @Data
    public static class Cluster {
        private Boolean enabled;
        private List<String> nodes = new ArrayList<>();
        private String host = "230.0.0.0";
        private Integer port = 6781;
    }

    @Data
    public static class Kbase {
        private String theme;
        private String htmlPath;
        private String apiUrl;
    }

    @Data
    public static class Aliyun {
        private String accessKeyId;
        private String accessKeySecret;
        private Oss oss = new Oss();
        private Sms sms = new Sms();

        @Data
        public static class Oss {
            private String endpoint;
            private String baseUrl;
            private String bucketName;
        }

        @Data
        public static class Sms {
            private String signName;
            private String templateCode;
        }
    }

    @Data
    public static class Tencent {
        private String appId;
        private String secretId;
        private String secretKey;
        private Bucket bucket = new Bucket();

        @Data
        public static class Bucket {
            private String location;
            private String name;
            private String domain;
        }
    }

    @Data
    public static class WechatPay {
        private Boolean enabled = false;
        private String certPath;
        private String appId;
        private String mchId;
        private String key;
        private String notifyUrl;
    }

    // 为了保持向后兼容,添加getter方法
    public String getEmail() {
        return admin.getEmail();
    }

    public String getPassword() {
        return admin.getPassword();
    }

    public String getPasswordDefault() {
        // return admin.getPasswordDefault();
        return member.getPassword();
    }

    public String getNickname() {
        return admin.getNickname();
    }

    public String getMobile() {
        return admin.getMobile();
    }

    public List<String> getMobileWhitelist() {
        return admin.getMobileWhitelist();
    }

    public List<String> getEmailWhitelist() {
        return admin.getEmailWhitelist();
    }

    public String getValidateCode() {
        return admin.getValidateCode();
    }

    public String getOrganizationName() {
        return organization.getName();
    }

    public String getOrganizationCode() {
        return organization.getCode();
    }

    public Boolean getJavaAi() {
        return features.getJavaAi();
    }

    public Boolean getPythonAi() {
        return features.getPythonAi();
    }

    public String getEmailType() {
        return features.getEmailType();
    }

    // 如果为空，则使用默认值
    public String getAvatarBaseUrl() {
        if (StringUtils.hasText(features.getAvatarBaseUrl())) {
            return features.getAvatarBaseUrl();
        }
        return "https://cdn.weiyuai.cn";
    }

    public String getCorsAllowedOrigins() {
        return cors.getAllowedOrigins();
    }

    public String getJwtSecretKey() {
        return jwt.getSecretKey();
    }

    public String getJwtExpiration() {
        return jwt.getExpiration();
    }

    public String getJwtRefreshTokenExpiration() {
        return jwt.getRefreshTokenExpiration();
    }

    public Integer getCacheLevel() {
        return cache.getLevel();
    }

    public String getCachePrefix() {
        return cache.getPrefix();
    }

    public String getRedisStreamKey() {
        return cache.getRedisStreamKey();
    }

    public String getUploadType() {
        return upload.getType();
    }

    public String getUploadDir() {
        return upload.getDir();
    }

    public String getUploadUrl() {
        return upload.getUrl();
    }

    public Boolean getClusterEnabled() {
        return cluster.getEnabled();
    }

    public List<String> getClusterNodes() {
        return cluster.getNodes();
    }

    public String getClusterHost() {
        return cluster.getHost();
    }

    public int getClusterPort() {
        return cluster.getPort();
    }

    public String getKbaseTheme() {
        return kbase.getTheme();
    }

    public String getKbaseHtmlPath() {
        return kbase.getHtmlPath();
    }

    public String getKbaseApiUrl() {
        return kbase.getApiUrl();
    }

    public Boolean isAdmin(@NonNull String receiver) {
        if (receiver == null || receiver.isEmpty()) {
            return false;
        }
        return receiver.equals(admin.getMobile()) || receiver.equals(admin.getEmail());
    }

    public Boolean isInWhitelist(@NonNull String receiver) {
        if (receiver == null || receiver.isEmpty()) {
            return false;
        }
        if (admin.getMobileWhitelist() == null || admin.getEmailWhitelist() == null) {
            return false;
        }
        return admin.getMobileWhitelist().contains(receiver) || 
               admin.getEmailWhitelist().contains(receiver);
    }


}
