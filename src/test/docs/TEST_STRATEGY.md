# Test Strategy

## 1) Strategy Summary
- Unit: Controller/Façade/Service/Factory를 mock 기반으로 빠르게 검증한다.
- Integration: Repository는 `@DataJpaTest + @Transactional`로 롤백 보장한다.
- Scenario: 인증/인가 포함 HTTP 시나리오는 `@SpringBootTest + MockMvc`로 검증한다.

## 2) Package Layout
- `src/test/java/com/Hoseo/CapstoneDesign/user/controller`
- `src/test/java/com/Hoseo/CapstoneDesign/user/facade`
- `src/test/java/com/Hoseo/CapstoneDesign/user/service`
- `src/test/java/com/Hoseo/CapstoneDesign/user/repository`
- `src/test/java/com/Hoseo/CapstoneDesign/user/factory`
- `src/test/java/com/Hoseo/CapstoneDesign/scenario/auth`
- `src/test/java/com/Hoseo/CapstoneDesign/support/builder`
- `src/test/java/com/Hoseo/CapstoneDesign/support/mother`
- `src/test/java/com/Hoseo/CapstoneDesign/support/factory`
- `src/test/java/com/Hoseo/CapstoneDesign/support/fixture`

## 3) Mandatory Scenarios
- Controller: 정상 응답 계약, GlobalExceptionResponse 오류 계약
- Facade: 유스케이스 조합, @Transactional 경계
- Service: 조회 실패 예외, 신규/기존 OAuth 사용자 분기, 경계값 닉네임
- Repository: 중복 데이터 제약, soft delete SQL 동작
- Scenario: 인증 실패 401 + GlobalExceptionResponse 포맷
