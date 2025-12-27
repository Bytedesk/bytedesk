package com.bytedesk.kbase.trigger;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TriggerResponseSimple implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid;

    private String triggerKey;

    private Boolean enabled;

    private String type;

    private String name;

    private String description;

    private String config;
}
