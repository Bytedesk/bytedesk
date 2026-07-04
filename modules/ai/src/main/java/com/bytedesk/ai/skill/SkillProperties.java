package com.bytedesk.ai.skill;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bytedesk.ai.skill")
public class SkillProperties {

    /**
     * External skill root directory. Expected layout: <root>/<skill-directory>/SKILL.md
     */
    private String externalDirectory;
}