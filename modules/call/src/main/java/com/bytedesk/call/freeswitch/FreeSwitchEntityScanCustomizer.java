/*
 * 目的：当 bytedesk.datasource.freeswitch.enabled 未开启时，将 com.bytedesk.call.freeswitch
 *      从主库 entityManagerFactory 的 packagesToScan 中剔除，避免主库对 freeswitch 实体建表/写入。
 * 位置：freeswitch 模块内部；不修改全局 starter 配置，完全满足“在 freeswitch 文件夹里面配置”。
 */
package com.bytedesk.call.freeswitch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(
    prefix = "bytedesk.datasource.freeswitch",
    name = "enabled",
    havingValue = "false",
    matchIfMissing = true
)
public class FreeSwitchEntityScanCustomizer {

    private static final String PRIMARY_EMF_BEAN_NAME = "entityManagerFactory";
    private static final String PACKAGES_TO_SCAN_PROP = "packagesToScan";
    private static final String FREESWITCH_PACKAGE = "com.bytedesk.call.freeswitch";

    @Bean
    public static BeanFactoryPostProcessor freeSwitchEntityExclusionPostProcessor() {
        return new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                if (!beanFactory.containsBeanDefinition(PRIMARY_EMF_BEAN_NAME)) {
                    return;
                }
                BeanDefinition bd = beanFactory.getBeanDefinition(PRIMARY_EMF_BEAN_NAME);
                PropertyValue pv = bd.getPropertyValues().getPropertyValue(PACKAGES_TO_SCAN_PROP);
                if (pv == null) {
                    // 主库未显式设置 packagesToScan，Spring Boot 通常会回退为应用主包；
                    // 此处尽量安全处理：不做任何变更，避免误伤。
                    return;
                }
                Object value = pv.getValue();
                List<String> packages = new ArrayList<>();
                if (value instanceof String[]) {
                    for (String s : (String[]) value) {
                        if (StringUtils.hasText(s)) packages.add(s);
                    }
                } else if (value instanceof List<?>) {
                    for (Object o : (List<?>) value) {
                        if (o instanceof String && StringUtils.hasText((String) o)) {
                            packages.add((String) o);
                        } else if (o instanceof TypedStringValue) {
                            String s = ((TypedStringValue) o).getValue();
                            if (StringUtils.hasText(s)) packages.add(s);
                        }
                    }
                } else if (value instanceof TypedStringValue) {
                    String s = ((TypedStringValue) value).getValue();
                    if (StringUtils.hasText(s)) packages.add(s);
                }

                if (packages.isEmpty()) {
                    return;
                }

                // 过滤掉 freeswitch 包及其子包
                List<String> filtered = packages.stream()
                        .filter(p -> !isSameOrChildPackage(p, FREESWITCH_PACKAGE))
                        .toList();

                if (!filtered.equals(packages)) {
                    bd.getPropertyValues().add(PACKAGES_TO_SCAN_PROP, filtered.toArray(String[]::new));
                }
            }

            private boolean isSameOrChildPackage(String pkg, String target) {
                if (!StringUtils.hasText(pkg) || !StringUtils.hasText(target)) return false;
                if (Objects.equals(pkg, target)) return true;
                return pkg.startsWith(target + ".");
            }
        };
    }
}
