# 🏕️ 주말 아빠 코스 (Daddy Weekend)

[![Platform](https://img.shields.io/badge/platform-Android-3DDC84.svg?style=flat-square&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF.svg?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Min SDK](https://img.shields.io/badge/minSdk-24-orange.svg?style=flat-square)](https://developer.android.com/about/versions)

<div align="center">
  <img src="docs/assets/icon.png" width="120" alt="주말 아빠 코스 아이콘" />
</div>

**40~50대 아빠를 위한 현실적이고 부담 없는 주말 자녀 맞춤 코스 추천·관리 앱입니다.**

자녀의 연령대와 아빠의 오늘 체력 상태만 고르면, 거창한 여행이 아닌 집 앞 놀이터·동네 도서관·시장 투어처럼 아빠 관점에서 실용적이고 접근하기 쉬운 리얼 라이프 코스를 즉석에서 추천합니다. 모든 데이터는 기기 안 로컬 DB에만 저장되어 인터넷 없이 동작합니다.

🔗 **소개 페이지**: <https://jeiel85.github.io/daddy-weekend/>

---

## 📸 대표 이미지

<div align="center">
  <img src="docs/assets/feature-graphic.png" width="800" alt="주말 아빠 코스 대표 이미지" />
</div>

---

## ✨ 주요 기능

* **⚡ 초간단 2-Step 맞춤 코스 매칭** — 아이의 나이대(영유아·유치원·초등 저학년·초등 고학년)와 아빠의 현재 체력(피곤함·보통·에너지 있음) 두 가지만 골라 즉시 코스를 추천합니다.
* **🎒 현실적인 짐꾸리기 & 실용 가이드** — 물티슈, 동전, 비상 스티커 등 아빠표 준비물 리스트와 현실적인 예상 비용을 함께 제공해 시행착오를 줄입니다.
* **💾 Local-First 안전 보관** — 일정 저장, 평점, 다이어리 메모는 오직 기기 내부 Room 데이터베이스에만 기록되며 인터넷 연결 없이 완전히 독립적으로 작동합니다.
* **🛡️ Robust Fallback 추천 엔진** — 모든 조건에 100% 일치하는 코스가 없으면 아이 연령과 아빠 체력을 우선해 가장 근접한 차선책 코스를 자동으로 선별합니다.

---

## 🛠️ 기술 스택

| 영역 | 사용 기술 |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose · Material Design 3 |
| Architecture | MVVM (ViewModel + Repository) |
| Local DB | Room |
| Build | Gradle Kotlin DSL · Version Catalog |
| Test | JUnit · Robolectric · Roborazzi |

---

## 🚀 로컬 빌드

### 요구사항
* Android Studio Ladybug 이상 / JDK 17 이상
* Android SDK 24 (Android 7.0) 이상

### 빌드 및 실행
```bash
git clone https://github.com/jeiel85/daddy-weekend.git
cd daddy-weekend
./gradlew installDebug   # 연결된 기기/에뮬레이터에 디버그 설치
```

---

## 📦 릴리즈 빌드 및 서명

릴리즈 서명 키는 저장소에 포함되지 않으며(`.gitignore` 처리), 빌드 시 환경 변수로 주입합니다.

| 환경 변수 | 설명 |
| --- | --- |
| `KEYSTORE_PATH` | 업로드 키스토어 경로 (미설정 시 `./.keystore/my-upload-key.jks`) |
| `STORE_PASSWORD` | 키스토어 비밀번호 |
| `KEY_PASSWORD` | 키 비밀번호 (alias: `upload`) |

```powershell
$env:STORE_PASSWORD = "<password>"
$env:KEY_PASSWORD   = "<password>"
.\gradlew.bat :app:exportReleaseToDesktop --no-daemon
```

`exportReleaseToDesktop` 태스크는 릴리즈 번들(`.aab`)을 빌드하고, Play Console용 릴리즈 노트와 함께 `Desktop\Build\` 폴더로 내보냅니다. 릴리즈 노트는 로케일별 500자 한도를 초과하면 빌드를 중단합니다.

---

## 📂 저장소 구조

```
app/                         # 안드로이드 앱 모듈 (Compose · Room · MVVM)
docs/                        # GitHub Pages 소개 페이지 (index.html, privacy.html, assets/)
store-graphics/
  └─ play-console-current/   # Play Console 업로드용 아이콘·피처 그래픽·릴리즈 노트·스크린샷
.keystore/                   # 릴리즈 업로드 키 (로컬 전용, gitignore)
```

---

## 🔒 개인정보 처리방침

모든 데이터는 사용자 기기에만 저장되며 외부로 전송되지 않습니다. 자세한 내용은 [개인정보 처리방침](https://jeiel85.github.io/daddy-weekend/privacy.html)을 참고하세요.
