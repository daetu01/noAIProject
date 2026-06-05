# 개발 중 겪은 문제 및 해결 과정

## 1. TOPIS 돌발 정보 시간 포맷 불일치

### 문제

서울 TOPIS 돌발 정보 API를 수집하는 과정에서 시간 데이터 형식이 일정하지 않았습니다.

예시:

```text
1425
091300
925
```

일부 데이터는 `HHmm`, 일부 데이터는 `HHmmss` 형식으로 내려와 `LocalDateTime.parse()` 시 예외가 발생했습니다.

로그:

```text
Text '202605271425' could not be parsed
```

### 원인

시간 형식이 항상 `HHmmss`로 고정되어 있다고 가정하고 아래 포맷으로만 파싱하고 있었습니다.

```java
DateTimeFormatter.ofPattern(
        "yyyyMMddHHmmss"
)
```

### 해결

시간 문자열 길이에 따라 초 단위와 앞자리 `0`을 보정하도록 변경했습니다.

```java
private LocalDateTime parseDateTime(
        String date,
        String time
) {
    String d = date.trim();
    String t = time.trim();

    if (t.length() == 4) {
        t = t + "00";
    }

    if (t.length() == 3) {
        t = "0" + t + "00";
    }

    if (t.length() != 6) {
        throw new RuntimeException("지원하지 않는 시간 형식: " + time);
    }

    return LocalDateTime.parse(
            d + t,
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    );
}
```

### 결과

- 시간 파싱 실패 제거
- 돌발 정보 수집 스케줄 안정성 확보

## 2. 교통 데이터 중복 저장 문제

### 문제

스케줄러 실행 중 동일한 교통량 데이터가 반복 저장되며 Unique 제약 조건 오류가 발생했습니다.

로그:

```text
Duplicate entry
```

### 원인

동일한 `spotNum`, `ymd`, `hour` 조합이 이미 존재하는데도 새로운 `TrafficVolume` 데이터를 계속 저장하고 있었습니다.

### 해결

저장 전 존재 여부를 확인하고, 이미 존재하면 기존 데이터를 사용하도록 변경했습니다.

```java
TrafficVolume saved =
        trafficVolumeRepository
                .findBySpotNumAndYmdAndHour(
                        trafficVolume.getSpotNum(),
                        trafficVolume.getYmd(),
                        trafficVolume.getHour()
                )
                .orElseGet(() ->
                        trafficVolumeRepository.save(trafficVolume)
                );
```

### 결과

- 중복 저장 제거
- 교통량 데이터 무결성 확보

## 3. TrafficEvent 메시지 길이 초과

### 문제

돌발 정보 내용을 이벤트 메시지에 함께 저장하던 중 저장 실패가 발생했습니다.

로그:

```text
Data too long for column 'message'
```

### 원인

여러 개의 돌발 정보를 문자열에 계속 추가하면서 기본 `VARCHAR(255)` 제한을 초과했습니다.

기존 방식:

```java
message.append(
        incident.getAccInfo()
);
```

### 해결

이벤트 메시지 컬럼 타입을 `TEXT`로 변경했습니다. 또한 UI 가독성과 메시지 크기 관리를 위해 노출 개수 제한을 적용할 수 있도록 `stream().limit(3)` 방식으로 정리했습니다.

```java
@Column(
        columnDefinition = "TEXT"
)
private String message;
```

```java
incidents.stream()
        .limit(3)
        .forEach(incident ->
                message.append("\n- ")
                        .append(incident.getAccType())
                        .append(" : ")
                        .append(incident.getAccInfo())
        );
```

### 결과

- 이벤트 저장 안정성 향상
- 상세 UI 메시지 가독성 개선

## 4. WebSocket 연결 실패 (CORS + JWT 충돌)

### 문제

실시간 알림 테스트 중 WebSocket 연결이 실패했습니다.

로그:

```text
/ws/info 403
No Access-Control-Allow-Origin
```

### 원인

SockJS 내부 요청이 Spring Security 및 JWT 필터에 의해 차단되었습니다.

### 해결

WebSocket 경로를 Security permit list에 추가했습니다.

```java
.requestMatchers(
        "/ws/**"
)
.permitAll()
```

JWT 필터에서도 WebSocket 요청은 토큰 검증을 건너뛰도록 처리했습니다.

```java
if (
        path.startsWith(
                "/ws"
        )
) {
    filterChain.doFilter(
            request,
            response
    );
    return;
}
```

### 결과

- SockJS `/ws/info` 요청 정상 처리
- 실시간 위험 알림 WebSocket 연결 정상 동작

## 5. Redis 캐시 무효화 시점 문제

### 문제

위험도 조회 API에 캐시를 적용한 뒤 최신 교통 데이터가 조회 결과에 반영되지 않았습니다.

### 원인

교통량 데이터 수집 후 기존 `trafficRisks`, `trafficDetail` 캐시를 유지하고 있었습니다.

### 해결

교통량 수집 완료 시점에 위험도 관련 캐시를 제거했습니다.

```java
Cache cache =
        cacheManager.getCache(
                "trafficRisks"
        );

if (cache != null) {
    cache.clear();
}
```

상세 조회 캐시도 함께 제거하도록 분리했습니다.

```java
Optional.ofNullable(
        cacheManager.getCache("trafficDetail")
).ifPresent(Cache::clear);
```

### 결과

- 반복 조회 성능 향상
- 수집 이후 최신 위험도 데이터 반영 보장

## 6. FastAPI numpy 타입 직렬화 문제

### 문제

IsolationForest 결과 반환 시 FastAPI에서 JSON 직렬화 예외가 발생했습니다.

로그:

```text
numpy.bool object is not iterable
```

### 원인

`model.predict()`와 `model.decision_function()` 결과에 포함된 numpy 타입을 FastAPI가 JSON으로 직접 직렬화하지 못했습니다.

### 해결

응답 전에 Python 기본 타입으로 명시 변환했습니다.

```python
prediction = int(model.predict(data)[0])
score = float(model.decision_function(data)[0])
anomaly = bool(prediction == -1)

return {
    "anomaly": anomaly,
    "aiScore": score,
    "status": "ANOMALY" if anomaly else "NORMAL"
}
```

### 결과

- FastAPI JSON 응답 정상화
- Spring Boot와 FastAPI 간 AI 예측 통신 정상화
