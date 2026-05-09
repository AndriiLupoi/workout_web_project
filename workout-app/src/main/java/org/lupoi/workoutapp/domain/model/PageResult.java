package org.lupoi.workoutapp.domain.model;/*
    @author Andrii
    @project workout
    @class PageResult
    @version 1.0.0
    @since 09.05.2026 - 13.36
*/

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize
) {}


