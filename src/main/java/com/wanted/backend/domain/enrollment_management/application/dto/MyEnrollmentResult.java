package com.wanted.backend.domain.enrollment_management.application.dto;

import com.wanted.backend.domain.enrollment_management.domain.model.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MyEnrollmentResult(
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        EnrollmentStatus status,
        BigDecimal progressRate,
        LocalDateTime expiredAt,
        LocalDateTime createdAt
) {}
