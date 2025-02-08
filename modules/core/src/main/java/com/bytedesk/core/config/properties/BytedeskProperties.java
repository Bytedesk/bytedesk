/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-30 09:14:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-08 10:03:20
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
        // 这里我们使用双重检查锁定来确保线程安全地初始化instance变量
        if (instance == null) {
            synchronized (BytedeskProperties.class) {
                if (instance == null) {
                    instance = this;
                }
            }
        }
    }

    public static BytedeskProperties getInstance() {
        // 如果instance尚未初始化，将调用init方法进行初始化（由Spring的@PostConstruct保证）
        return instance;
    }

    private Boolean debug;

    private String edition = Edition.COMMUNITY.name().toLowerCase();

    private String name;

    private String version;

    private String logo;

    private String description;

    // 管理员配置
    private Admin admin = new Admin();
    
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
        COMMUNITY, // 社区版-免费, 功能受限，人数不限
        ENTERPRISE, // 企业版-200人，付费，功能不限
        ULTIMATE // 旗舰版-不限人数，付费，功能不限
    }

    @Data
    public static class Admin {
        private String email;
        private String password;
        private String passwordDefault;
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
        private int port = 6781;
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
        return admin.getPasswordDefault();
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

    public String getAvatarUrl() {
        return features.getAvatarBaseUrl();
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
