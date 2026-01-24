/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-23
 * @Description: File chunking configuration
 */
package com.bytedesk.kbase.llm_file;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileChunkingConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_CHUNK_SIZE = 1000;
    public static final int DEFAULT_CHUNK_OVERLAP = 100;

    @Builder.Default
    private FileChunkingStrategyEnum strategy = FileChunkingStrategyEnum.TOKEN;

    @Builder.Default
    private Integer chunkSize = DEFAULT_CHUNK_SIZE;

    @Builder.Default
    private Integer chunkOverlap = DEFAULT_CHUNK_OVERLAP;

    public static FileChunkingConfig normalize(FileChunkingConfig config) {
        if (config == null) {
            return FileChunkingConfig.builder().build();
        }

        FileChunkingStrategyEnum strategy = config.getStrategy() == null
                ? FileChunkingStrategyEnum.TOKEN
                : config.getStrategy();

        int size = config.getChunkSize() == null || config.getChunkSize() <= 0
                ? DEFAULT_CHUNK_SIZE
                : config.getChunkSize();

        int overlap = config.getChunkOverlap() == null || config.getChunkOverlap() < 0
                ? DEFAULT_CHUNK_OVERLAP
                : config.getChunkOverlap();

        // clamp overlap
        if (overlap >= size) {
            overlap = Math.max(0, size / 5);
        }

        return FileChunkingConfig.builder()
                .strategy(strategy)
                .chunkSize(size)
                .chunkOverlap(overlap)
                .build();
    }

    public static FileChunkingConfig fromFileResponse(FileResponse fileResponse) {
        if (fileResponse == null) {
            return FileChunkingConfig.builder().build();
        }
        return normalize(FileChunkingConfig.builder()
                .strategy(FileChunkingStrategyEnum.fromString(fileResponse.getChunkingStrategy()))
                .chunkSize(fileResponse.getChunkSize())
                .chunkOverlap(fileResponse.getChunkOverlap())
                .build());
    }
}
