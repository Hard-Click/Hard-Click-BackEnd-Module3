package com.wanted.backend.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${community.image.post-dir}")
    private String postDir;

    @Value("${community.image.comment-dir}")
    private String commentDir;

    @Value("${identity.profile-image.upload-dir}")
    private String profileDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 썸네일, 영상 등 uploads/ 하위 파일
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");

        // 커뮤니티 게시글 이미지 (application.yml의 community.image.post-dir)
        registry.addResourceHandler("/community/post/**")
                .addResourceLocations("file:" + postDir);

        // 커뮤니티 댓글 이미지 (application.yml의 community.image.comment-dir)
        registry.addResourceHandler("/community/comment/**")
                .addResourceLocations("file:" + commentDir);

        // 프로필 이미지 (application.yml의 identity.profile-image.upload-dir)
        registry.addResourceHandler("/identity/profile/**")
                .addResourceLocations("file:" + profileDir);
    }
}
