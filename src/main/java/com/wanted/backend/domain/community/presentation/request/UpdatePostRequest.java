package com.wanted.backend.domain.community.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;


public record UpdatePostRequest(

        Long subjectId,

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 300, message = "제목은 300자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용은 필수입니다.")
        String content,

        // 수정 시 유지할 기존 첨부 이미지 URL 목록 (null/빈 목록이면 기존 전부 삭제)
        List<String> keepFileUrls
) {

}