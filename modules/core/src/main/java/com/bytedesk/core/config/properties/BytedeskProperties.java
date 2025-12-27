/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-30 09:14:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-01 09:13:24
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
import java.util.List;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(BytedeskProperties.CONFIG_PREFIX)
public class BytedeskProperties {

    
    public static final String CONFIG_PREFIX = "bytedesk";
    private static final String ENCRYPTION_KEY = "bytedesk_license"; // 16字节密钥

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

                        // 验证 licenseKey 解密状态
                        // if (StringUtils.hasText(this.licenseKey)) {
                        //     if (this.licenseKey.startsWith("ENC(")) {
                        //         log.warn("⚠️  licenseKey 未被正确解密，仍为加密格式！");
                        //         log.warn("   请确保已设置环境变量: export JASYPT_ENCRYPTOR_PASSWORD=xxx");
                        //         log.warn("   或在启动命令中添加: -Djasypt.encryptor.password=xxx");
                        //     } else {
                        //         String maskedKey = maskSensitiveValue(this.licenseKey);
                        //         String firstPart = this.licenseKey.length() > 8 ? this.licenseKey.substring(0, 8) : this.licenseKey;
                        //         String lastPart = this.licenseKey.length() > 8 ? this.licenseKey.substring(this.licenseKey.length() - 8) : this.licenseKey;
                        //         log.info("✓ licenseKey 已被成功解密 (长度: {})", this.licenseKey.length());
                        //         log.debug("  脱敏显示: {}", maskedKey);
                        //         log.debug("  首部: {}, 末尾: {}", firstPart, lastPart);
                        //         // Debug 模式下打印完整值用于验证
                        //         if (this.debug != null && this.debug) {
                        //             log.debug("  [DEBUG] 完整解密值: {}", this.licenseKey);
                        //         }
                        //     }
                        // }

                    } catch (Exception e) {
                        log.error("初始化 BytedeskProperties 时出错", e);
                    }
                    instance = this;
                }
            }
        }
    }

    /**
     * AES加密字符串
     * @param plainText 明文
     * @return 加密后的Base64字符串
     */
    public static String encryptString(String plainText) {
        try {
            if (!StringUtils.hasText(plainText)) {
                return plainText;
            }
            
            SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("encryptString failed", e);
            return plainText; // 加密失败时返回原值
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
            log.error("处理中文文本时出错: {}", envKey, e);
            return text;
        }
    }

    /**
     * 脱敏敏感信息用于日志输出
     * @param value 敏感信息
     * @return 脱敏后的字符串
     */
    // private static String maskSensitiveValue(String value) {
    //     if (!StringUtils.hasText(value)) {
    //         return value;
    //     }
        
    //     if (value.length() <= 16) {
    //         return "*".repeat(value.length());
    //     }
        
    //     // 显示前8个和后8个字符，便于对比验证
    //     return value.substring(0, 8) + "***[" + (value.length() - 16) + " chars]***" + value.substring(value.length() - 8);
    // }

    public static BytedeskProperties getInstance() {
        return instance;
    }

    private Boolean debug;

    private String version;

    private String licenseKey;

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

    // MinIO 配置
    private Minio minio = new Minio();

    // 微信支付配置
    private WechatPay wechatPay = new WechatPay();

    public static enum Edition {
        COMMUNITY, // 社区版-不限人，免费, 功能受限
        ENTERPRISE, // 企业版-不限人，付费，功能不限
        PLATFORM // 平台版-不限人数，付费，功能不限
    }

    @Getter
    @Setter
    public static class Custom {
        private Boolean enabled = false;
        private String name;
        private String logo;
        private String description;
        private Boolean showRightCornerChat = true;
        private String rightCornerChatPlacement = "bottom-right"; // 位置：bottom-right / bottom-left
        private Boolean showDemo = true; // 是否显示演示
        private String privacyPolicyUrl;
        private String termsOfServiceUrl;
        private Boolean loginUsernameEnable = true;
        private Boolean login2faEnable = false;
        private Integer loginMaxRetryCount = 3;
        private Integer loginMaxRetryLockTime = 10;
        private Boolean loginMobileEnable = true;
        // private Boolean loginEmailEnable = false;
        private Boolean loginScanEnable = false;
        // enable wechat login
        private Boolean loginWechatEnable = false;
        private Boolean loginGithubEnable = false;
        private Boolean loginFacebookEnable = false;
        private Boolean loginGoogleEnable = false;
        private Boolean docUrlShow = true;
        private String docUrl;
        // default lang: en-US, zh-CN, zh-TW
        private String lang = "zh-CN";
        // 
        private Boolean allowRegister = false;
        private Boolean forceValidateMobile = false;
        private Boolean forceValidateEmail = false;
        private Boolean forceVisitorAuth = false; // 是否强制访客认证，默认false
    }

    @Getter
    @Setter
    public static class Admin {
        private String email;
        private String password;
        // private String passwordDefault;
        private String nickname;
        private String mobile;
        private List<String> mobileWhitelist = new ArrayList<>();
        private List<String> emailWhitelist = new ArrayList<>();
        private String validateCode;
    }

    @Getter
    @Setter
    public static class Member {
        private String password;
    }

    @Getter
    @Setter
    public static class Testing {
        private Boolean enabled = false;
        // private Integer accountCount = 300;
        // private String accountUsername = "test_user";
        // private String accountPassword = "password";
        private Boolean disableCaptcha = false;
        private Boolean disableIpFilter = false;
        // whitelist ip for performance testing
        private List<String> ipWhitelist = new ArrayList<>();
    }
    @Getter
    @Setter 
    public static class Organization {
        private String name;
        private String code;
    }

    @Getter
    @Setter
    public static class Features {
        private Boolean javaAi = false;
        // private Boolean pythonAi = true;
        private String emailType = "javamail";
        // private Date startDate;
        // private Integer freeDays = 30;
        private String avatarBaseUrl;
    }

    @Getter
    @Setter
    public static class Cors {
        private String allowedOrigins;
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secretKey;
        private String expiration;
        private String refreshTokenExpiration;
    }

    @Getter
    @Setter
    public static class Cache {
        private Integer level;
        private String prefix;
        private String redisStreamKey;
    }

    @Getter
    @Setter
    public static class Upload {
        private String type;
        private String dir;
        private String url;
    }

    @Getter
    @Setter
    public static class Cluster {
        private Boolean enabled;
        private List<String> nodes = new ArrayList<>();
        private String host = "230.0.0.0";
        private Integer port = 6781;
    }

    @Getter
    @Setter
    public static class Kbase {
        private String theme;
        private String htmlPath;
        private String apiUrl;
    }

    @Getter
    @Setter
    public static class Aliyun {
        private String accessKeyId;
        private String accessKeySecret;
        private Oss oss = new Oss();
        private Sms sms = new Sms();

        @Getter
    @Setter
        public static class Oss {
            private String endpoint;
            private String baseUrl;
            private String bucketName;
        }

        @Getter
    @Setter
        public static class Sms {
            private String signName;
            private String templateCode;
        }
    }

    @Getter
    @Setter
    public static class Tencent {
        private String appId;
        private String secretId;
        private String secretKey;
        private Bucket bucket = new Bucket();

        @Getter
        @Setter
        public static class Bucket {
            private String location;
            private String name;
            private String domain;
        }
    }

    @Getter
    @Setter
    public static class WechatPay {
        private Boolean enabled = false;
        private String certPath;
        private String appId;
        private String mchId;
        private String key;
        private String notifyUrl;
    }

    @Getter
    @Setter
    public static class Minio {
        private Boolean enabled = false;
        private String endpoint = "http://127.0.0.1:19000";
        private String accessKey = "minioadmin";
        private String secretKey = "minioadmin123";
        private String bucketName = "bytedesk";
        private String region = "us-east-1";
        private Boolean secure = false;
    }

    // 为了保持向后兼容,添加getter方法
    public String getEmail() {
        return admin.getEmail();
    }

    public String getPassword() {
        return admin.getPassword();
    }

    // 导入成员默认密码
    public String getMemberDefaultPassword() {
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

    public Boolean getMinioEnabled() {
        return minio.getEnabled();
    }

    public String getMinioEndpoint() {
        return minio.getEndpoint();
    }

    public String getMinioAccessKey() {
        return minio.getAccessKey();
    }

    public String getMinioSecretKey() {
        return minio.getSecretKey();
    }

    public String getMinioBucketName() {
        return minio.getBucketName();
    }

    public String getMinioRegion() {
        return minio.getRegion();
    }

    public Boolean getMinioSecure() {
        return minio.getSecure();
    }

    /**
     * 检查是否禁用IP过滤
     * @return 如果禁用IP过滤，返回true；否则返回false
     */
    public boolean isDisableIpFilter() {
        return testing != null && 
               Boolean.TRUE.equals(testing.getEnabled()) && 
               Boolean.TRUE.equals(testing.getDisableIpFilter());
    }

    /**
     * 检查是否禁用验证码
     * @return 如果禁用验证码，返回true；否则返回false
     */
    public boolean isDisableCaptcha() {
        return testing != null && 
               Boolean.TRUE.equals(testing.getEnabled()) && 
               Boolean.TRUE.equals(testing.getDisableCaptcha());
    }

    public Boolean isSuperUser(@NonNull String user) {
        if (user == null || user.isEmpty()) {
            return false;
        }
        return user.equals(admin.getMobile()) || user.equals(admin.getEmail());
    }

    public Boolean isInWhitelist(@NonNull String user) {
        if (user == null || user.isEmpty()) {
            return false;
        }
        if (admin.getMobileWhitelist() == null || admin.getEmailWhitelist() == null) {
            return false;
        }
        return admin.getMobileWhitelist().contains(user) || 
               admin.getEmailWhitelist().contains(user);
    }

    /**
     * 获取加密后的licenseKey
     * @return 加密后的licenseKey字符串
     */
    public String getLicenseKey() {
        // 原始licenseKey已经是Base64编码的许可证信息，直接AES加密
        return encryptString(this.licenseKey);
    }

    /**
     * 获取原始licenseKey（仅用于内部使用）
     * @return 原始licenseKey字符串
     */
    public String getOriginalAppkey() {
        return this.licenseKey;
    }

}
