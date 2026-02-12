package com.bytedesk.core.message.preview;

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
public class UrlPreviewResponse {

    private String url;

    private String title;

    private String description;

    private String imageUrl;

    private String siteName;
}
