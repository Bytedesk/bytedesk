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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.bytedesk.core.utils.LicenseValidator;

@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(BytedeskProperties.CONFIG_PREFIX)
public class BytedeskProperties implements EnvironmentAware {

    public static final String CONFIG_PREFIX = "bytedesk";
    private static final String APPLICATION_VERSION_KEY = "application.version";

    private static volatile BytedeskProperties instance; // 使用volatile关键字确保可见性

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

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
                            this.custom.setDefaultLlmPrompt(handleChineseText(this.custom.getDefaultLlmPrompt(), "BYTEDESK_CUSTOM_DEFAULT_LLM_PROMPT"));
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
                        log.error("初始化 BytedeskProperties 时出错", e);
                    }
                    instance = this;
                }
            }
        }
    }

    /**
     * 许可证载荷缓存 (避免重复验签)
     */
    private transient volatile LicenseValidator.LicenseInfo cachedLicenseInfo;
    private transient volatile String cachedLicenseInfoFor;

    /**
     * 验证许可证并返回解析后的信息
     * 结果会被缓存，仅在 licenseKey 变化时重新验证
     *
     * @return LicenseInfo 验证成功返回许可证信息，失败返回 null
     */
    public LicenseValidator.LicenseInfo validateLicense() {
        String currentKey = this.licenseKey;
        if (cachedLicenseInfo != null && currentKey != null && currentKey.equals(cachedLicenseInfoFor)) {
            return cachedLicenseInfo;
        }
        synchronized (this) {
            if (cachedLicenseInfo != null && currentKey != null && currentKey.equals(cachedLicenseInfoFor)) {
                return cachedLicenseInfo;
            }
            cachedLicenseInfo = LicenseValidator.validate(currentKey);
            cachedLicenseInfoFor = currentKey;
            return cachedLicenseInfo;
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

    public static BytedeskProperties getInstance() {
        return instance;
    }

    private Boolean debug;

    private String version;

    private String licenseKey;

    // 许可证配置
    private License license = new License();

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

    // 呼叫中心配置
    private Call call = new Call();

    // 微信支付配置
    private WechatPay wechatPay = new WechatPay();

    public static enum Edition {
        COMMUNITY, // 社区版-不限人，免费, 功能受限
        ENTERPRISE, // 企业版-不限人，付费，功能不限
        PLATFORM // 平台版-不限人数，付费，功能不限
    }

    @Getter
    @Setter
    public static class License {
        /** RSA 私钥文件路径，用于签发许可证签名 */
        private String privateKeyPath;
    }

    @Getter
    @Setter
    public static class Custom {
        private Boolean enabled = false;
        private String name;
        private String logo;
        private String favicon;
        private String description;
        /**
         * 外网可访问的上传 API 地址（完整URL，不带上传路径），用于反向代理/多节点场景。
         * 例如：https://upload.example.com
         */
        private String uploadApiUrl;
        /**
         * 外网可访问的 MQTT WebSocket 地址（完整URL），用于反向代理场景。
         * 例如：wss://api.example.com/websocket
         */
        private String mqttWebsocketUrl;
        /**
         * 外网可访问的访客工单页面地址（完整URL，不带末尾斜杠），用于邮件中的工单会话直达链接。
         * 例如：https://support.example.com/ticket
         */
        private String ticketHtmlUrl;
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
        // 手机/邮箱验证码登录时，未注册用户是否允许自动创建账号
        private Boolean autoRegisterOnLogin = true;
        private Boolean forceValidateMobile = false;
        private Boolean forceValidateEmail = false;
        private Boolean forceVisitorAuth = false; // 是否强制访客认证，默认false
        private Boolean wechatMpSubscribePromptEnabled = false;
        private String wechatMpSubscribePromptAppId;
        private String wechatMpLoginNoticeTemplateId;
        /**
         * 自定义默认 LLM Prompt；为空时回退到代码内置默认值。
         */
        private String defaultLlmPrompt;
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
        // when user has no organization after login, allow creating a new organization
        private Boolean allowCreateOrg = true;
        // when user has no organization after login, allow applying to join an existing organization
        private Boolean allowJoinOrg = true;
        // default vip level for new organizations
        private Integer defaultVipLevel = 0;
        // default validity period (days) for new organizations
        private Integer defaultVipDays = 365;
        // default limits for new organizations
        private Integer defaultMaxMembers = 20;
        private Integer defaultMaxAgents = 20;
        private Integer defaultMaxWorkgroups = 20;
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

    @Getter
    @Setter
    public static class Call {
        private Freeswitch freeswitch = new Freeswitch();

        @Getter
        @Setter
        public static class Freeswitch {
            private String recordingsBaseUrl;
        }
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

    public Boolean isAdminIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return false;
        }
        return identifier.equals(admin.getMobile()) || identifier.equals(admin.getEmail());
    }

    public Boolean isInWhitelist(String user) {
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
     * 获取 licenseKey（新格式为 RSA 签名，旧格式为 Base64 编码）
     * 不再进行 AES 加密，前端通过 RSA 验签或 Base64 解码获取信息。
     *
     * @return licenseKey 字符串
     */
    public String getLicenseKey() {
        return this.licenseKey;
    }

    /**
     * 获取原始licenseKey（仅用于内部使用）
     * @return 原始licenseKey字符串
     */
    public String getOriginalAppkey() {
        return this.licenseKey;
    }

    public String getVersion() {
        if (StringUtils.hasText(this.version)) {
            return this.version;
        }
        return environment != null ? environment.getProperty(APPLICATION_VERSION_KEY) : null;
    }

}
