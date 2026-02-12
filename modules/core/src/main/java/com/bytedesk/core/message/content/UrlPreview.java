package com.bytedesk.core.message.content;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * URL preview metadata for TextContent.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlPreview implements Serializable {

    private static final long serialVersionUID = 1L;

    private String url;

    private String title;

    private String description;

    private String imageUrl;

    private String siteName;

    /** ISO-8601 string or timestamp-like string; optional. */
    private String fetchedAt;
}
