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
public class AudioContent implements Serializable {
    private String url;
    private String duration;
    private String format;
    private String label;
} 