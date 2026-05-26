package com.wanted.backend.domain.enrollment_management.presentation.api.response;

import com.wanted.backend.domain.enrollment_management.application.dto.MyEnrollmentResult;
import com.wanted.backend.domain.enrollment_management.domain.model.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record MyEnrollmentResponse(
        Long enrollmentId,
        Long courseId,
        String courseTitle,
        EnrollmentStatus status,
        BigDecimal progressRate,
        LocalDateTime expiredAt,
        LocalDateTime createdAt
) {
    public static MyEnrollmentResponse from(MyEnrollmentResult result) {
        return new MyEnrollmentResponse(
                result.enrollmentId(),
                result.courseId(),
                result.courseTitle(),
                result.status(),
                result.progressRate(),
                result.expiredAt(),
                result.createdAt()
        );
    }

    public static List<MyEnrollmentResponse> from(List<MyEnrollmentResult> results) {
        return results.stream().map(MyEnrollmentResponse::from).toList();
    }
}
