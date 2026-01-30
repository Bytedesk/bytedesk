package com.bytedesk.service.customer_custom_field_settings;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.model.CustomFieldItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerCustomFieldSettingsRequest {

    // for compatibility with common frontend request payloads
    private String channel;

    private String orgUid;

    @Builder.Default
    private List<CustomFieldItem> customFieldList = new ArrayList<>();
}
