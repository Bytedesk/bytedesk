/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-08 23:49:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 15:57:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.kaptcha;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import static com.google.code.kaptcha.Constants.*;

@Configuration
public class KaptchaConfig {

    @Bean(name = "captchaProducer")
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框 默认为true 我们可以自己设置yes，no
        properties.setProperty(KAPTCHA_BORDER, "no");
        // 验证码文本字符颜色 默认为Color.BLACK
        // properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "white");
        // properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
        // 验证码图片宽度 默认为200
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "100");
        // 验证码图片高度 默认为50
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "40");
        // 验证码文本字符大小 默认为40
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "35");
        // KAPTCHA_SESSION_KEY
        // properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");
        // 验证码文本字符长度 默认为5
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "4");
        // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        // properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 图片样式 水纹com.google.code.kaptcha.impl.WaterRipple
        // 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy
        // 阴影com.google.code.kaptcha.impl.ShadowGimpy
        // properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        // 
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    // @Bean(name = "captchaProducerMath")
    // public DefaultKaptcha getKaptchaBeanMath() {
    //     DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
    //     Properties properties = new Properties();
    //     // 是否有边框 默认为true 我们可以自己设置yes，no
    //     properties.setProperty(KAPTCHA_BORDER, "yes");
    //     // 边框颜色 默认为Color.BLACK
    //     properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
    //     // 验证码文本字符颜色 默认为Color.BLACK
    //     properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "blue");
    //     // 验证码图片宽度 默认为200
    //     properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
    //     // 验证码图片高度 默认为50
    //     properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
    //     // 验证码文本字符大小 默认为40
    //     properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "35");
    //     // KAPTCHA_SESSION_KEY
    //     properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCodeMath");
    //     // 验证码文本生成器
    //     properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "com.ruoyi.framework.config.KaptchaTextCreator");
    //     // 验证码文本字符间距 默认为2
    //     properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "3");
    //     // 验证码文本字符长度 默认为5
    //     properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "6");
    //     // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
    //     properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
    //     // 验证码噪点颜色 默认为Color.BLACK
    //     properties.setProperty(KAPTCHA_NOISE_COLOR, "white");
    //     // 干扰实现类
    //     properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
    //     // 图片样式 水纹com.google.code.kaptcha.impl.WaterRipple
    //     // 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy
    //     // 阴影com.google.code.kaptcha.impl.ShadowGimpy
    //     properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
    //     Config config = new Config(properties);
    //     defaultKaptcha.setConfig(config);
    //     return defaultKaptcha;
    // }
}
