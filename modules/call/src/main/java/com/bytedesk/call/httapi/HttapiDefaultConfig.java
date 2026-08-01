package com.bytedesk.call.httapi;

import com.bytedesk.call.config.CallConstants;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class HttapiDefaultConfig {

    @Bean
    @ConditionalOnMissingBean(HttapiMrcpProfileResolver.class)
    public HttapiMrcpProfileResolver httapiMrcpProfileResolver() {
        return new EnvironmentHttapiMrcpProfileResolver();
    }

    private static final class EnvironmentHttapiMrcpProfileResolver implements HttapiMrcpProfileResolver {

        @Override
        public String resolveProfile(Map<String, String> vars) {
            String explicitProfile = HttapiMrcpProfileOverrideSupport.resolveExplicitProfile(vars);
            if (explicitProfile != null) {
                return explicitProfile;
            }
            return System.getenv().getOrDefault(
                    CallConstants.ENV_HTTAPI_MRCP_PROFILE,
                    CallConstants.DEFAULT_HTTAPI_MRCP_PROFILE);
        }
    }
}