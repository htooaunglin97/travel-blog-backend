package com.hal.travelapp.v1.dto;

import java.util.List;

public record CursorPageResult<T>(
        List<T> content,
        String nextCursor,
        boolean hasNext,
        int pageSize
) {
    public static <T> CursorPageResult<T> of(List<T> content, String nextCursor, boolean hasNext, int pageSize) {
        return new CursorPageResult<>(content, nextCursor, hasNext, pageSize);
    }
}

