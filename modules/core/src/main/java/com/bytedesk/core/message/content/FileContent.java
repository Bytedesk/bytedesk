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
public class FileContent implements Serializable {
    private String url;
    private String name;
    private String size;
    private String type;
    private String label;
} 