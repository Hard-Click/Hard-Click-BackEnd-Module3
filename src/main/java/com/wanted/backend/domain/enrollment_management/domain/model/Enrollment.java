package com.wanted.backend.domain.enrollment_management.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Enrollment {

    private Long id;
    private Long userId;
    private Long courseId;
    private String paymentType;
    private EnrollmentStatus status;
    private BigDecimal progressRate;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;

    private Enrollment() {}

    public static Enrollment create(Long userId, Long courseId, String paymentType, LocalDateTime now) {
        Enrollment enrollment = new Enrollment();
        enrollment.userId = userId;
        enrollment.courseId = courseId;
        enrollment.paymentType = paymentType;
        enrollment.status = EnrollmentStatus.IN_PROGRESS;
        enrollment.progressRate = BigDecimal.ZERO;
        enrollment.createdAt = now;
        return enrollment;
    }

    public static Enrollment restore(Long id, Long userId, Long courseId, String paymentType,
                                     EnrollmentStatus status, BigDecimal progressRate,
                                     LocalDateTime expiredAt, LocalDateTime createdAt) {
        Enrollment enrollment = new Enrollment();
        enrollment.id = id;
        enrollment.userId = userId;
        enrollment.courseId = courseId;
        enrollment.paymentType = paymentType;
        enrollment.status = status;
        enrollment.progressRate = progressRate;
        enrollment.expiredAt = expiredAt;
        enrollment.createdAt = createdAt;
        return enrollment;
    }

    /**
     * 만료일 기준 상태 자동 계산:
     * - expiredAt이 현재 시각 이전이면 EXPIRED
     * - 그 외 저장된 status 반환
     */
    public EnrollmentStatus getEffectiveStatus() {
        if (expiredAt != null && expiredAt.isBefore(LocalDateTime.now())) {
            return EnrollmentStatus.EXPIRED;
        }
        return status;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getCourseId() { return courseId; }
    public String getPaymentType() { return paymentType; }
    public EnrollmentStatus getStatus() { return status; }
    public BigDecimal getProgressRate() { return progressRate; }
    public LocalDateTime getExpiredAt() { return expiredAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
