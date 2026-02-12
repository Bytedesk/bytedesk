package com.bytedesk.core.message.preview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlPreviewWritebackRequest {

    /** message uid to update */
    private String messageUid;

    /** url in text */
    private String url;

    /** optional preview fields (if absent, server may fetch) */
    private String title;
    private String description;
    private String imageUrl;
    private String siteName;
}
