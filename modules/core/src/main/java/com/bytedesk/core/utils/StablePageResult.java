package com.bytedesk.core.utils;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class StablePageResult<T> {

    private final List<T> content;

    private final PageMetadata page;

    private final boolean empty;

    private final boolean first;

    private final boolean last;

    private final int number;

    private final int numberOfElements;

    private final long totalElements;

    private final int totalPages;

    private final int size;

    private StablePageResult(Page<T> source) {
        this.content = source.getContent();
        this.page = new PageMetadata(source);
        this.empty = source.isEmpty();
        this.first = source.isFirst();
        this.last = source.isLast();
        this.number = source.getNumber();
        this.numberOfElements = source.getNumberOfElements();
        this.totalElements = source.getTotalElements();
        this.totalPages = source.getTotalPages();
        this.size = source.getSize();
    }

    public static <T> StablePageResult<T> from(Page<T> source) {
        return new StablePageResult<>(source);
    }

    @Getter
    public static class PageMetadata {

        private final int size;

        private final int number;

        private final long totalElements;

        private final int totalPages;

        private PageMetadata(Page<?> source) {
            this.size = source.getSize();
            this.number = source.getNumber();
            this.totalElements = source.getTotalElements();
            this.totalPages = source.getTotalPages();
        }
    }
}