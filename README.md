## Client Architecture
<img width="6220" height="4604" alt="System Architecture" src="https://github.com/user-attachments/assets/72b429d2-8f6f-414f-8153-5a2aa676eee5" />

MingleDay Android 클라이언트는 **MVVM(Model-View-ViewModel) 아키텍처**를 기반으로 구성되어 있습니다.
화면을 담당하는 View 계층, UI 상태와 이벤트를 관리하는 ViewModel 계층, 데이터 처리를 담당하는 Repository 계층을 분리하여 유지보수성과 확장성을 높였습니다.

서버와의 통신은 **Retrofit2**와 **OkHttp**를 사용하며, JWT Access Token은 **DataStore**에 저장한 뒤 API 요청 시 `Authorization Header`에 포함하여 전달합니다.

---

## Architecture Overview

```text
View(Activity / Fragment)
        ↓
ViewModel
        ↓
Repository
        ↓
Retrofit API Interface
        ↓
OkHttp Client
        ↓
Spring Boot Server
```

---

## Applied Architecture & Pattern

| Pattern                    | Description                             |
| -------------------------- | --------------------------------------- |
| MVVM                       | View와 비즈니스 로직을 분리하여 화면 상태를 관리           |
| Repository Pattern         | 데이터 요청 로직을 Repository로 분리               |
| Client-Server Architecture | Android 앱과 Spring Boot 서버가 REST API로 통신 |
| REST API                   | HTTP Method 기반의 서버 통신 구조                |
| JWT Authentication         | Access Token을 이용한 인증 방식                 |
| Single Activity + Fragment | 하나의 Activity 안에서 여러 Fragment 화면을 전환     |

---

## Client Layer

### View Layer

사용자에게 보이는 화면을 담당하는 계층입니다.
Activity와 Fragment는 ViewModel을 통해 데이터를 요청하고, 응답 결과를 화면에 반영합니다.

| Component      | Role                           |
| -------------- | ------------------------------ |
| Activity       | 앱의 기본 화면 컨테이너 역할               |
| Fragment       | 모임, 일정, 카테고리, 멤버 권한 등 주요 화면 구성 |
| XML Layout     | 화면 UI 구조 정의                    |
| ViewBinding    | XML View를 안전하게 참조              |
| RecyclerView   | 목록 형태의 데이터 표시                  |
| DialogFragment | 날짜, 시간, 선택 화면 등 팝업 UI 처리       |

---

### ViewModel Layer

ViewModel은 화면에 필요한 데이터와 UI 상태를 관리합니다.
View에서 직접 서버 API를 호출하지 않도록 중간에서 Repository와 연결됩니다.

| Responsibility | Description             |
| -------------- | ----------------------- |
| UI 상태 관리       | 화면에 표시할 데이터와 상태 관리      |
| 사용자 이벤트 처리     | 버튼 클릭, 입력값 변경 등의 이벤트 처리 |
| Repository 호출  | 필요한 데이터를 Repository에 요청 |
| 화면 갱신 데이터 제공   | API 결과를 View에 전달        |
| 생명주기 대응        | 화면 회전 등에도 데이터 유지 가능     |

---

### Repository Layer

Repository는 데이터 처리 로직을 담당합니다.
ViewModel은 Repository를 통해 서버 데이터를 요청하며, Repository는 Retrofit API를 호출하여 결과를 반환합니다.

| Responsibility | Description                          |
| -------------- | ------------------------------------ |
| API 호출 관리      | Retrofit API Interface 호출            |
| 데이터 요청 분리      | ViewModel에서 네트워크 로직 분리               |
| 응답 데이터 전달      | 서버 응답을 ViewModel로 반환                 |
| 에러 처리 보조       | API 실패 시 ViewModel에서 처리할 수 있도록 결과 전달 |

---

### Network Layer

서버와의 HTTP 통신을 담당하는 계층입니다.
Retrofit2를 사용하여 REST API를 호출하고, OkHttp Interceptor를 통해 인증 토큰을 요청 헤더에 추가합니다.

| Component       | Role                                   |
| --------------- | -------------------------------------- |
| Retrofit2       | REST API 통신 처리                         |
| OkHttp          | HTTP Client 역할                         |
| AuthInterceptor | Access Token을 Authorization Header에 추가 |
| API Interface   | 서버 API Endpoint 정의                     |
| DTO             | 요청/응답 데이터 객체                           |

---

### Local Storage Layer

앱 내부에 필요한 데이터를 저장하는 계층입니다.
MingleDay에서는 Access Token과 앱 설정값을 DataStore에 저장합니다.

| Storage           | Data             |
| ----------------- | ---------------- |
| TokenDataStore    | Access Token 저장  |
| SettingsDataStore | 언어 설정 등 앱 설정값 저장 |

---

## Tech Stack

### Language & Platform

| Category   | Technology | Description         |
| ---------- | ---------- | ------------------- |
| Language   | Kotlin     | Android 클라이언트 개발 언어 |
| Platform   | Android    | 모바일 애플리케이션 실행 환경    |
| Build Tool | Gradle     | 프로젝트 빌드 및 의존성 관리    |

---

### Architecture

| Category     | Technology                 | Description                   |
| ------------ | -------------------------- | ----------------------------- |
| Architecture | MVVM                       | View, ViewModel, Model 역할 분리  |
| Data Pattern | Repository Pattern         | 데이터 접근 로직 분리                  |
| UI Structure | Single Activity + Fragment | Activity를 중심으로 Fragment 화면 전환 |

---

### UI

| Category | Technology     | Description            |
| -------- | -------------- | ---------------------- |
| Layout   | XML Layout     | Android 화면 구성          |
| Binding  | ViewBinding    | View 참조 안정성 향상         |
| List UI  | RecyclerView   | 모임, 일정, 멤버, 카테고리 목록 표시 |
| Image    | Glide          | 이미지 URL 로딩 및 표시        |
| Dialog   | DialogFragment | 날짜, 시간, 선택 팝업 처리       |

---

### Network

| Category    | Technology         | Description          |
| ----------- | ------------------ | -------------------- |
| API Client  | Retrofit2          | REST API 요청 처리       |
| HTTP Client | OkHttp             | HTTP 통신 처리           |
| Interceptor | OkHttp Interceptor | 요청 전 Access Token 추가 |
| Data Format | JSON               | 서버와 데이터 교환 형식        |

---

### Async & Local Data

| Category      | Technology           | Description   |
| ------------- | -------------------- | ------------- |
| Async         | Coroutine            | 비동기 API 요청 처리 |
| Local Storage | DataStore            | 토큰 및 설정값 저장   |
| Date / Time   | LocalDate, LocalTime | 일정 날짜 및 시간 처리 |

---

## Request Flow

```text
1. 사용자가 화면에서 기능을 실행한다.
2. Fragment 또는 Activity가 사용자 이벤트를 감지한다.
3. ViewModel이 필요한 데이터를 Repository에 요청한다.
4. Repository가 Retrofit API Interface를 호출한다.
5. OkHttp Interceptor가 Access Token을 Header에 추가한다.
6. 서버에 HTTP 요청을 보낸다.
7. 서버 응답을 Repository가 전달받는다.
8. ViewModel이 응답 데이터를 화면 상태로 변환한다.
9. Fragment 또는 Activity가 화면을 갱신한다.
```

---

## Authentication Flow

```text
Login Success
        ↓
Access Token 발급
        ↓
TokenDataStore에 저장
        ↓
API 요청 발생
        ↓
AuthInterceptor에서 Token 조회
        ↓
Authorization Header 추가
        ↓
서버에서 JWT 검증
```

### Authorization Header

```http
Authorization: Bearer {accessToken}
```

---

## Main Screens

| Screen              | Description         |
| ------------------- | ------------------- |
| Login / Sign Up     | 로그인 및 회원가입 화면       |
| Mingle List         | 사용자가 참여 중인 모임 목록 화면 |
| Mingle Detail       | 모임 정보 및 설정 화면       |
| Monthly Calendar    | 월간 일정 조회 화면         |
| Daily Schedule      | 특정 날짜의 일정 목록 화면     |
| Schedule Add / Edit | 일정 생성 및 수정 화면       |
| Category            | 일정 카테고리 관리 화면       |
| Member Permission   | 모임 구성원 권한 관리 화면     |
| Invitation          | 모임 초대 및 참여 처리 화면    |

---

## Main Features

| Feature | Description                    |
| ------- | ------------------------------ |
| 회원 인증   | 로그인, 회원가입, JWT 기반 인증           |
| 모임 관리   | 모임 생성, 조회, 수정                  |
| 일정 관리   | 일정 생성, 수정, 삭제, 월간/일간 조회        |
| 반복 일정   | 매일, 매주, 매월, 사용자 지정 반복 일정 처리    |
| 카테고리    | 모임별 일정 카테고리 표시 및 관리            |
| 권한 관리   | 구성원별 초대, 추방, 일정 생성/수정/삭제 권한 설정 |
| 이미지 처리  | 모임 이미지 및 프로필 이미지 로딩            |
| 초대 기능   | 초대 링크를 통한 모임 참여                |
| 설정 관리   | 언어 설정 등 로컬 설정 저장               |

---

## Package Structure

```text
returns.mingleday.app
├── data
│   ├── local
│   │   ├── TokenDataStore
│   │   └── SettingsDataStore
│   ├── remote
│   │   ├── api
│   │   ├── dto
│   │   └── interceptor
│   └── repository
│
├── ui
│   ├── login
│   ├── main
│   ├── mingle
│   ├── schedule
│   ├── category
│   └── permission
│
├── viewmodel
│
└── util
```

---

## Package Description

| Package                   | Description                |
| ------------------------- | -------------------------- |
| `data.local`              | DataStore를 이용한 로컬 데이터 저장   |
| `data.remote.api`         | Retrofit API Interface 정의  |
| `data.remote.dto`         | 서버 요청/응답 DTO 관리            |
| `data.remote.interceptor` | 인증 토큰 Header 추가 처리         |
| `data.repository`         | API 호출 및 데이터 처리 로직 관리      |
| `ui`                      | Activity, Fragment 등 화면 구성 |
| `viewmodel`               | UI 상태 및 이벤트 처리             |
| `util`                    | 공통 유틸 함수 및 확장 기능 관리        |

---

## Summary

MingleDay Android 클라이언트는 MVVM 아키텍처를 기반으로 화면과 데이터 처리 로직을 분리하였습니다.
Retrofit2와 OkHttp를 통해 Spring Boot 서버와 REST API 방식으로 통신하며, JWT Access Token은 DataStore에 저장하여 인증이 필요한 요청에 사용합니다.
Fragment 기반 화면 구성, Repository Pattern, Coroutine 비동기 처리, Glide 이미지 로딩을 적용하여 일정 공유 앱에 필요한 기능을 구조적으로 구현하였습니다.
