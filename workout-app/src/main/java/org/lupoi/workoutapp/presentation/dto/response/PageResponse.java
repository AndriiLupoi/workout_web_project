package org.lupoi.workoutapp.presentation.dto.response;/*
    @author Andrii
    @project workout
    @class PageResponse
    @version 1.0.0
    @since 09.05.2026 - 13.36
*/

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize,
        boolean first,
        boolean last
) {
    public static <T> PageResponse<T> of(
            List<T> content,
            int currentPage,
            int totalPages,
            long totalElements,
            int pageSize
    ) {
        return new PageResponse<>(
                content,
                currentPage,
                totalPages,
                totalElements,
                pageSize,
                currentPage == 0,
                currentPage >= totalPages - 1
        );
    }
}

