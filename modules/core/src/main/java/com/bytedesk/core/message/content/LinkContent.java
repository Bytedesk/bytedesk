package com.bytedesk.core.message.content;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LinkContent implements Serializable {
    private String url;
    private String title;
    private String description;
    private String imageUrl;
    private String label;
} 