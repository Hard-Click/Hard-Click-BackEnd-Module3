package com.wanted.backend.domain.community.application.service;

import com.wanted.backend.domain.community.application.command.CreatePostCommand;
import com.wanted.backend.domain.community.application.command.DeletePostCommand;
import com.wanted.backend.domain.community.application.command.UpdatePostCommand;
import com.wanted.backend.domain.community.application.usecase.PostCommandUseCase;
import com.wanted.backend.domain.community.domain.model.Post;
import com.wanted.backend.domain.community.domain.model.PostFile;
import com.wanted.backend.domain.community.domain.repository.CommentRepository;
import com.wanted.backend.domain.community.domain.repository.PostFileRepository;
import com.wanted.backend.domain.community.domain.repository.PostRepository;
import com.wanted.backend.domain.community.domain.repository.ViewLogRepository;
import com.wanted.backend.domain.community.infrastructure.file.FileUploadUtils;
import com.wanted.backend.global.exception.BusinessException;
import com.wanted.backend.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PostCommandService implements PostCommandUseCase {

    @Value("${community.image.post-dir}")
    private String postDir;

    @Value("${community.image.post-url}")
    private String postUrl;

    @Value("${community.image.max-size}")
    private long maxFileSize;


    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final ViewLogRepository viewLogRepository;
    private final CommentRepository commentRepository;

    public PostCommandService(PostRepository postRepository,
                              PostFileRepository postFileRepository,
                              ViewLogRepository viewLogRepository,
                              CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.postFileRepository = postFileRepository;
        this.viewLogRepository = viewLogRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public Long create(CreatePostCommand command) {

        int fileCount = command.files() != null ? command.files().size() : 0;

        Post post = Post.create(
                command.authorId(),
                command.boardType(),
                command.subjectId(),
                command.title(),
                command.content(),
                fileCount
        );

        Post saved = postRepository.save(post);

        if (fileCount > 0) {
            List<String> savedFileNames = new ArrayList<>();
            try {
                for (int i = 0; i < command.files().size(); i++) {
                    MultipartFile file = command.files().get(i);
                    String savedFileName = FileUploadUtils.saveFile(file, postDir, maxFileSize);
                    savedFileNames.add(savedFileName);
                    String fileUrl = postUrl + savedFileName;
                    postFileRepository.save(PostFile.create(saved.getId(), fileUrl, i + 1));
                }
            } catch (IOException e) {
                savedFileNames.forEach(name -> FileUploadUtils.deleteFile(postDir, name));
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return saved.getId();
    }

    @Override
    public void delete(DeletePostCommand command) {

        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        post.validateDeletable(command.memberId());

        postFileRepository.findByPostId(command.postId())
                .forEach(file -> {

                    String fileName = file.getFileUrl()
                            .substring(file.getFileUrl().lastIndexOf("/") + 1);
                    FileUploadUtils.deleteFile(postDir, fileName);
                });


        postFileRepository.deleteByPostId(command.postId());
        viewLogRepository.deleteByPostId(command.postId());
        commentRepository.deleteByPostId(command.postId());

        postRepository.deleteById(command.postId());
    }

    @Override
    public Long update(UpdatePostCommand command) {

        // [1단계] 게시글 존재 여부 확인
        Post post = postRepository.findById(command.postId())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // [2단계] 본인 게시글 여부 + 채택글 여부 검증 → 도메인이 담당
        post.validateUpdatable(command.memberId());

        // [3단계] 파일 개수 검증 (유지할 기존 이미지 + 새 업로드 파일 합산)
        List<String> keepUrls = command.keepFileUrls() != null ? command.keepFileUrls() : List.of();
        int newFileCount = command.files() != null ? command.files().size() : 0;
        post.validateFileCount(keepUrls.size() + newFileCount);

        // [4단계] 게시글 값 수정 → 도메인이 담당
        post.update(command.subjectId(), command.title(), command.content());

        // [5단계] 변경된 게시글 DB 저장
        Post saved = postRepository.save(post);

        // [6단계] 기존 첨부파일 중 유지 목록에 없는 것만 물리 파일 삭제
        postFileRepository.findByPostId(command.postId())
                .forEach(file -> {
                    if (!keepUrls.contains(file.getFileUrl())) {
                        String fileName = file.getFileUrl()
                                .substring(file.getFileUrl().lastIndexOf("/") + 1);
                        FileUploadUtils.deleteFile(postDir, fileName);
                    }
                });

        // DB 행은 전부 삭제 후, 유지 URL + 새 파일 순서대로 재삽입
        postFileRepository.deleteByPostId(command.postId());

        int order = 1;
        // [6-1단계] 유지할 기존 이미지 먼저 재삽입 (물리 파일은 그대로 두었으므로 URL만 복원)
        for (String keepUrl : keepUrls) {
            postFileRepository.save(PostFile.create(saved.getId(), keepUrl, order++));
        }

        // [7단계] 새 파일 저장 (파일 있을 때만)
        if (newFileCount > 0) {
            List<String> savedFileNames = new ArrayList<>();
            try {
                for (MultipartFile file : command.files()) {
                    String savedFileName = FileUploadUtils.saveFile(file, postDir, maxFileSize);
                    savedFileNames.add(savedFileName);
                    String fileUrl = postUrl + savedFileName;
                    postFileRepository.save(PostFile.create(saved.getId(), fileUrl, order++));
                }
            } catch (IOException e) {
                // 새 파일 저장 실패 시 저장된 새 파일 롤백 삭제
                savedFileNames.forEach(name -> FileUploadUtils.deleteFile(postDir, name));
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return saved.getId();
    }
}