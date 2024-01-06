// package com.bytedesk.ai.config;

// import org.springframework.boot.context.properties.ConfigurationProperties;

// import lombok.Data;
// import redis.clients.jedis.JedisPoolConfig;
// import redis.clients.jedis.Protocol;

// import javax.net.ssl.HostnameVerifier;
// import javax.net.ssl.SSLParameters;
// import javax.net.ssl.SSLSocketFactory;

// /**
//  * https://github.com/007gzs/weixin-java-open-demo/blob/master/src/main/java/com/github/binarywang/demo/wechat/service/WxOpenServiceDemo.java
//  * 
//  * @author <a href="https://github.com/007gzs">007</a>
//  */
// @Data
// @ConfigurationProperties(prefix = "spring.data.redis")
// public class RedisProperies extends JedisPoolConfig {
//     //
//     private String host;
//     private int port;
//     private String password;
//     private int database;
//     private int timeout;
//     private int soTimeout = Protocol.DEFAULT_TIMEOUT;
//     private String clientName;
//     private boolean ssl;
//     private SSLSocketFactory sslSocketFactory;
//     private SSLParameters sslParameters;
//     private HostnameVerifier hostnameVerifier;

// }
