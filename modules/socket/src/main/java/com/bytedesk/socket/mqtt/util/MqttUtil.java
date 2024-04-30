/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-13 15:47:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.socket.mqtt.util;

import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.springframework.util.StringUtils;

public class MqttUtil {

    private MqttUtil() {
    }

    public static boolean validTopicFilter(List<MqttTopicSubscription> topicSubscriptions) {
        for (MqttTopicSubscription topicSubscription : topicSubscriptions) {
            String topicFilter = topicSubscription.topicName();
            if (StringUtils.hasText(topicFilter)) {
                // 以#或+符号开头的、以/符号结尾的订阅按非法订阅处理, 这里没有参考标准协议
                if (topicFilter.endsWith("+") || topicFilter.endsWith("/")) {
                    return false;
                }
                if (topicFilter.contains("#")) {
                    // 如果出现多个#符号的订阅按非法订阅处理
                    if (StringUtils.countOccurrencesOf(topicFilter, "#") > 1) {
                        return false;
                    }
                }
                if (topicFilter.contains("+")) {
                    // 如果+符号和/+字符串出现的次数不等的情况按非法订阅处理
                    if (StringUtils.countOccurrencesOf(topicFilter, "+") != StringUtils.countOccurrencesOf(topicFilter, "/+")) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    // https://www.cnblogs.com/qingyibusi/p/8572783.html
    // SSLEngine sslEngine = sslContext.createSSLEngine();
    // sslEngine.setUseClientMode(false); //服务器端模式
    // sslEngine.setNeedClientAuth(false); //不需要验证客户端
    // socketChannel.pipeline().addLast("ssl", new SslHandler(sslEngine)); //搞定

    private static volatile SSLContext sslContext = null;

    public static SSLContext createSSLContext(String type, String path, String password) throws Exception {
        if (null == sslContext) {
            synchronized (MqttUtil.class) {
                if (null == sslContext) {
                    KeyStore ks = KeyStore.getInstance(type); /// "JKS"
                    InputStream ksInputStream = new FileInputStream(path); /// 证书存放地址
                    ks.load(ksInputStream, password.toCharArray());
                    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    kmf.init(ks, password.toCharArray());
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(kmf.getKeyManagers(), null, null);
                }
            }
        }
        return sslContext;
    }

    
}
