# Bytedesk - 채팅 서비스

AI 기반 옴니채널 고객 서비스 및 팀 협업

:::tip
Bytedesk는 현재 빠른 개발 단계에 있어 문서가 개발을 따라가지 못할 수 있으며, 기능 설명이 최신 소프트웨어 버전과 일치하지 않을 수 있습니다.
:::

## 주요 기능

### 팀 메신저

- 다층 조직 구조
- 역할 관리
- 권한 관리
- 기타

### 고객 서비스

- 다중 채널 지원
- 다양한 라우팅 전략 및 상세 평가 지표
- 상담원 워크벤치
- 기타

### 지식 베이스

- 내부 문서
- 헬프 센터
- 기타

### 티켓 관리

- 티켓 관리
- 티켓 SLA 관리
- 티켓 통계 및 보고서
- 기타

### AI 채팅

- LLM과의 채팅
- 지식 베이스와의 채팅(RAG)
- 기타

## Docker 빠른 시작

### 프로젝트 클론 및 Docker 컨테이너 시작

```bash
git clone https://github.com/Bytedesk/bytedesk.git && cd bytedesk/deploy/docker && docker compose -p bytedesk -f docker-compose.yaml up -d
```

### 컨테이너 중지

```bash
docker compose -p bytedesk -f docker-compose.yaml stop
```

## 미리보기

로컬 미리보기

```bash
http://127.0.0.1:9003/dev
```

- [온라인 미리보기](https://www.weiyuai.cn/admin/)

## 오픈소스 클라이언트

- [데스크톱](https://github.com/Bytedesk/bytedesk-desktop)
- [모바일](https://github.com/Bytedesk/bytedesk-mobile)

## 라이선스

- [Apache License 2.0](./LICENSE.txt) 