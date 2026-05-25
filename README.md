# Hard-Click-BackEnd
# 📚 FLOWN BackEnd
> 온라인 학습 플랫폼 FLOWN의 백엔드 프로젝트입니다.
>
> 강의 수강, 학습 관리, 커뮤니티, 리뷰 및 결제 기능을 제공하는 교육 플랫폼 API 서버를 구현하였습니다.
---
## 📖 프로젝트 소개
FLOWN은 사용자가 온라인 강의를 수강하고 학습 현황을 관리할 수 있는 온라인 학습 플랫폼입니다.
클린 아키텍처(Hexagonal Architecture)와 도메인 주도 설계(DDD)를 기반으로
강의 수강, 결제, 커뮤니티, 리뷰, 학습 통계 기능을 제공하는 RESTful API 서버입니다.
---
## 🛠️ 기술 스택
| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8.0 |
| Security | Spring Security / JWT |
| Build | Gradle |
| Logging | Logback / MDC |
---
## 🏗️ 아키텍처
클린 아키텍처 (Hexagonal Architecture) + DDD
com.wanted.backend
├── global/          # 공통 설정, 예외처리, 보안, AOP
└── domain/
├── identity/    # 회원/인증 도메인
├── community/   # 커뮤니티 도메인 (게시글, 댓글, 리뷰, 스터디)
├── course/      # 강좌/강의 도메인
├── enrollment/  # 수강신청 도메인
├── payment/     # 결제/구독 도메인
└── learning/    # 학습활동/통계 도메인
각 도메인은 4개의 계층으로 분리됩니다.
presentation  →  application  →  domain  →  infrastructure
---
## 📋 Code Convention
### Branch Naming
| Branch | Description |
|--------|-------------|
| main | 배포 브랜치 |
| develop | 개발 통합 브랜치 |
| feature/* | 기능 개발 |
| fix/* | 버그 수정 |
| refactor/* | 리팩토링 |
| docs/* | 문서 작업 |
---
### Commit Convention
bash
[type]: subject

#### Type
- feature : 기능 추가
- fix : 버그 수정
- refactor : 코드 리팩토링
- docs : 문서 수정
- test : 테스트 코드 추가/수정
- chore : 빌드 설정, 의존성 변경
---
### API Response 형식
json
{
  "httpStatus": 200,
  "message": "정상처리되었습니다.",
  "data": { }
}

---
### 패키지 구조 규칙
- 도메인 패키지명: 소문자 (예: community, identity)
- Command: 상태를 변경하는 작업 (Create, Update, Delete)
- Query: 상태를 조회하는 작업 (Read)
- Port: 다른 BC 또는 외부 시스템과의 통신 인터페이스
- Adapter: Port 구현체, JPA Entity, Repository 구현
---
## 🔐 인증 방식
- JWT Access Token / Refresh Token 방식
- Access Token: 요청 헤더 Authorization: Bearer {token}
- Refresh Token: 토큰 재발급 시 사용
---
## 📁 로그 정책
- MDC를 활용한 요청별 고유 traceId 부여
- 환경별 로그 분리 (dev / prod)
- 로그 파일: logs/hard-click.log, logs/hard-click-error.log
---
> 📝 Last Update: 2026.05.25
