# Bytedesk - 채팅 서비스

AI 기반 옴니채널 고객 서비스와 팀 협업 플랫폼

## 언어

- [English](./README.md)
- [中文](./README.zh.md)
- [한국어](./README.ko.md)

## 관리자 대시보드

![statistics](./images/admin/statistics.png)

## 관리자 채팅

![chat](./images/admin/chat.png)

## 관리자 LLM + Agent

![llm_agent](./images/admin/llm_agent.png)

## 채널 센터

![channel](./images/admin/channel.png)

## 상담원 워크벤치

![agent](./images/agent/agent_chat.png)

## 소개

### [TeamIM](./modules/team/readme.md)

- 다단계 조직 구조와 부서 관리
- 역할/권한 제어 및 모니터링
- 기록 및 감사 로그
- ...

### [고객 서비스](./modules/service/readme.md)

- 웹/앱/소셜/쇼핑몰 등 멀티채널 연동
- 지능형 라우팅 전략과 KPI 지표
- 상담원 통합 데스크
- ...

### [지식 베이스](./modules/kbase/readme.md)

- 내부 문서와 헬프센터
- FAQ 및 RAG 지식 연동
- AI 에이전트와 실시간 동기화
- ...

### [티켓 시스템](./modules/ticket/readme.md)

- 티켓 라이프사이클 관리
- SLA 정책 및 알림
- 통계/리포트 대시보드
- ...

### [AI Agent](./modules/ai/readme.md)

- Ollama / DeepSeek / ZhipuAI / ... 연동
- 지식 기반(RAG) 챗봇
- Function Calling · MCP
- ...

### [워크플로우](./modules/core/readme.workflow.md)

- 커스텀 폼과 프로세스 디자이너
- 시각적 플로우 구성
- 티켓 자동화 시나리오
- ...

### [VOC](./modules/voc/readme.md)

- 고객 피드백/설문/불만 접수
- 서비스 품질 지표 추적
- ...

### [콜센터](./plugins/freeswitch/readme.zh.md)

- FreeSwitch 기반 전문 음성 플랫폼
- 착신 팝업, 자동 배분, 통화 녹음
- 음성/텍스트 하이브리드 운영

### [영상 상담](./plugins/webrtc/readme.zh.md)

- WebRTC 기반 HD 화상 상담
- 원클릭 영상/화면 공유
- 고부가가치 상담 시나리오에 최적

### [오픈 플랫폼](./plugins/readme.md)

- 완전한 RESTful API와 다국어 SDK
- 써드파티 시스템과 손쉬운 연동
- 개발/통합 속도 향상

## 빠른 시작

```bash
git clone https://github.com/Bytedesk/bytedesk.git
cd bytedesk/deploy/docker
# AI 기능 없이 실행
docker compose -p bytedesk -f docker-compose-noai.yaml up -d
# ZhipuAI 기본 (API Key 필요)
docker compose -p bytedesk -f docker-compose.yaml up -d
# 로컬 Ollama 사용
docker compose -p bytedesk -f docker-compose-ollama.yaml up -d
```

- [Docker 배포](https://www.weiyuai.cn/docs/docs/deploy/docker/)
- [Baota 배포](https://www.weiyuai.cn/docs/docs/deploy/baota)
- [소스 코드 실행](https://www.weiyuai.cn/docs/docs/deploy/source)

## 데모 접속

```bash
# 127.0.0.1을 서버 IP로 교체
http://127.0.0.1:9003/
# 오픈 포트: 9003, 9885
기본 계정: admin@email.com
기본 비밀번호: admin
```

## 프로젝트 구조

루트 `pom.xml`을 가진 Maven 기반 모노레포로, 다양한 모듈/배포 자산을 포함합니다.

```text
bytedesk/
├─ channels/           # 채널 통합 (Douyin, 스토어, 소셜, WeChat)
├─ demos/              # 데모 프로젝트 및 샘플 코드
├─ deploy/             # Docker, K8s, 서버 설정 자료
├─ enterprise/         # 엔터프라이즈 기능 (ai, call, core, kbase, service, ticket)
├─ images/             # 문서/화면 캡처 리소스
├─ jmeter/             # 성능 테스트 스크립트
├─ logs/               # 로컬/개발 로그
├─ modules/            # 핵심 모듈 (TeamIM, Service, KBase, Ticket, AI ...)
├─ plugins/            # 선택형 플러그인 (freeswitch, webrtc, open platform)
├─ projects/           # 커스텀 프로젝트
├─ starter/            # 스타터/엔트리 프로젝트
```

## 아키텍처

- [아키텍처 다이어그램](https://www.weiyuai.cn/architecture.html)

## 오픈소스 클라이언트

- [데스크톱](https://github.com/Bytedesk/bytedesk-desktop)
- [모바일](https://github.com/Bytedesk/bytedesk-mobile)
- [SipPhone](https://github.com/Bytedesk/bytedesk-phone)
- [Conference](https://github.com/Bytedesk/bytedesk-conference)
- [FreeSwitch Docker](https://github.com/Bytedesk/bytedesk-freeswitch)
- [Jitsi Docker](https://github.com/Bytedesk/bytedesk-jitsi)

## 오픈소스 데모 & SDK

| 프로젝트 | 설명 | Forks | Stars |
|----------|------|-------|-------|
| [iOS](https://github.com/bytedesk/bytedesk-swift) | 네이티브 iOS 앱 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-swift) | ![GitHub Repo stars](https://img.shields.io/github/stars/Bytedesk/bytedesk-swift) |
| [Android](https://github.com/bytedesk/bytedesk-android) | 네이티브 Android 앱 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-android) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-android) |
| [Flutter](https://github.com/bytedesk/bytedesk-flutter) | Flutter SDK | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-flutter) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-flutter) |
| [UniApp](https://github.com/bytedesk/bytedesk-uniapp) | UniApp 패키지 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-uniapp) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-uniapp) |
| [Web](https://github.com/bytedesk/bytedesk-web) | Vue/React/Angular/Next.js 프런트엔드 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-web) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-web) |
| [WordPress](https://github.com/bytedesk/bytedesk-wordpress) | WordPress 플러그인 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-wordpress) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-wordpress) |
| [WooCommerce](https://github.com/bytedesk/bytedesk-woocommerce) | WooCommerce 연동 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-woocommerce) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-woocommerce) |
| [Magento](https://github.com/bytedesk/bytedesk-magento) | Magento 확장 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-magento) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-magento) |
| [PrestaShop](https://github.com/bytedesk/bytedesk-prestashop) | PrestaShop 모듈 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-prestashop) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-prestashop) |
| [Shopify](https://github.com/bytedesk/bytedesk-shopify) | Shopify 앱 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-shopify) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-shopify) |
| [OpenCart](https://github.com/bytedesk/bytedesk-opencart) | OpenCart 플러그인 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-opencart) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-opencart) |
| [Laravel](https://github.com/bytedesk/bytedesk-laravel) | Laravel 패키지 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-laravel) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-laravel) |
| [Django](https://github.com/bytedesk/bytedesk-django) | Django 앱 | ![GitHub forks](https://img.shields.io/github/forks/bytedesk/bytedesk-django) | ![GitHub Repo stars](https://img.shields.io/github/stars/bytedesk/bytedesk-django) |

## 링크

- [다운로드](https://www.weiyuai.cn/download.html)
- [문서](https://www.weiyuai.cn/docs/)

## 라이선스

Copyright (c) 2013-2025 Bytedesk.com.

GNU AFFERO GENERAL PUBLIC LICENSE (AGPL v3) 조항에 따라 배포됩니다:

<https://www.gnu.org/licenses/agpl-3.0.html>

소프트웨어는 명시적/묵시적 보증 없이 "있는 그대로" 제공됩니다.

## 이용 약관

- **허용 용도**: 상업적 사용 가능, 무단 재판매 금지
- **금지 용도**: 악성코드, 도박, 사기 등 불법 목적 금지
- **면책 조항**: 사용자는 모든 법적 책임과 위험을 스스로 부담합니다