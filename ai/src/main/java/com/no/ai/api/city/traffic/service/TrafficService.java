package com.no.ai.api.city.traffic.service;

import com.no.ai.api.ApiConfig;
import com.no.ai.api.city.traffic.domain.*;
import com.no.ai.api.city.traffic.dto.*;
import com.no.ai.api.city.traffic.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.sql.DataSource;



@Service
@Slf4j
@RequiredArgsConstructor
public class TrafficService {
    private final ApiConfig apiConfig;

    @Qualifier("topisRestClient")
    private final RestClient topisRestClient;
    private final CacheManager cacheManager;
    private final DataSource dataSource;

    @Qualifier("taskExecutor")
    private final Executor executor;

    private final TrafficVolumeRepository trafficVolumeRepository;
    private final TrafficSpotRepository trafficSpotRepository;
    private final TrafficRiskRepository trafficRiskRepository;
    private final TrafficEventRepository trafficEventRepository;
    private final TrafficIncidentRepository trafficIncidentRepository;

    private final SimpMessagingTemplate messagingTemplate;
    /**
     * 교통 지점 가져오는 정보
     * @return
     */
    public List<TrafficVolumeDto.Response> getTrafficInfo(String ymd, String hour) {
        try {
            SpotInfoResponse response = topisRestClient.get()
                    .uri("/{key}/xml/SpotInfo/1/1000",
                            apiConfig.getCity().getServiceKey())
                    .retrieve()
                    .body(SpotInfoResponse.class);

            validateSpotResponse(response);

            log.info(
                    "교통 지점 정보 수집 rows={}, resultCode={}",
                    response.getRows().size(),
                    response.getResult() != null ? response.getResult().getCode() : null
            );

            List<TrafficSpot> spots =
                    response.getRows()
                            .stream()
                            .map(this::toTrafficSpot)
                            .filter(Objects::nonNull)
                            .toList();

            trafficSpotRepository.saveAllAndFlush(spots);

            log.info(
                    "교통 지점 DB 저장 완료 jdbcUrl={}, saved={}, totalCount={}",
                    currentJdbcUrl(),
                    spots.size(),
                    trafficSpotRepository.count()
            );



            return response.getRows()
                    .stream()
                    .map(row ->
                            {
                                try {
                                    return syncVolInfo(
                                            row.getSpotNum(),
                                            ymd,
                                            hour
                                    );
                                } catch (DataIntegrityViolationException e) {
                                    log.warn("중복 데이터 스킵: spotNum={}, ymd={}, hour={}",
                                            row.getSpotNum(), ymd, hour);
                                    return null;
                                } catch (Exception e) {
                                    log.warn("교통량 수집 실패: spotNum={}, ymd={}, hour={}",
                                            row.getSpotNum(), ymd, hour);
                                    return null;
                                }
                            }
                    )
                    .filter(Objects::nonNull)
                    .toList();
            // 여기서
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private TrafficSpot toTrafficSpot(
            SpotRow row
    ) {
        try {
            TrafficSpot trafficSpot =
                    trafficSpotRepository
                            .findBySpotNum(row.getSpotNum())
                            .orElse(
                                    TrafficSpot.builder()
                                            .spotNum(row.getSpotNum())
                                            .build()
                            );

            trafficSpot.update(
                    row.getSpotName(),
                    row.getTmX(),
                    row.getTmY()
            );

            return trafficSpot;
        } catch (Exception e) {
            log.warn(
                    "spot 저장 대상 변환 실패/스킵: spotNum={}, spotName={}, tmX={}, tmY={}, reason={}",
                    row.getSpotNum(),
                    row.getSpotName(),
                    row.getTmX(),
                    row.getTmY(),
                    e.getMessage(),
                    e
            );

            return null;
        }
    }

    private String currentJdbcUrl() {
        try (var connection = dataSource.getConnection()) {
            return connection.getMetaData().getURL();
        } catch (Exception e) {
            return "unknown(" + e.getMessage() + ")";
        }
    }

    private void validateSpotResponse(
            SpotInfoResponse response
    ) {
        if (response == null) {
            throw new RuntimeException("교통 지점 응답이 없습니다.");
        }

        if (response.getResult() != null &&
                !"INFO-000".equals(response.getResult().getCode())) {
            throw new RuntimeException(
                    "교통 지점 API 오류: " + response.getResult().getMessage()
            );
        }

        if (response.getRows() == null ||
                response.getRows().isEmpty()) {
            throw new RuntimeException(
                    "교통 지점 정보 없음"
            );
        }
    }


    /**
     * spotNum 바탕으로 교통량 리턴해주는
     * @param spotNum, ymd
     * @return
     */
    public VolInfoResponse fetchVolInfo(String spotNum, String ymd, String hour) {
        try {
            VolInfoResponse response = topisRestClient.get()
                    .uri("/{key}/xml/VolInfo/1/100/{spotNum}/{ymd}/{hour}",
                            apiConfig.getCity().getServiceKey(), spotNum, ymd, hour)
                    .retrieve()
                    .body(VolInfoResponse.class);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private TrafficVolume aggregateToTrafficVolume(
            VolInfoResponse response,
            String spotNum,
            String ymd,
            String hour
    ) {
        Map<String,Integer> grouped =
                response.getRows()
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        VolInfoRow::getIoType,
                                        Collectors.summingInt(
                                                VolInfoRow::getVolume
                                        )
                                )
                        );

        Integer inVolume =
                grouped.getOrDefault("1", 0);

        Integer outVolume =
                grouped.getOrDefault("2", 0);

        Integer totalVolume =
                inVolume + outVolume;

        return TrafficVolume.builder()
                .spotNum(spotNum)
                .ymd(
                        LocalDate.parse(
                                ymd,
                                DateTimeFormatter.BASIC_ISO_DATE
                        )
                )
                .hour(Integer.parseInt(hour))
                .inVolume(inVolume)
                .outVolume(outVolume)
                .totalVolume(totalVolume)
                .build();
    }

    public TrafficVolumeDto.Response syncVolInfo(
            String spotNum,
            String ymd,
            String hour
    ) {
        try {
            VolInfoResponse response =
                    fetchVolInfo(
                            spotNum,
                            ymd,
                            hour
                    );

            validateResponse(response);

            TrafficVolume trafficVolume =
                    aggregateToTrafficVolume(
                            response,
                            spotNum,
                            ymd,
                            hour
                    );

            TrafficVolume saved =
                    trafficVolumeRepository
                            .findBySpotNumAndYmdAndHour(
                                    trafficVolume.getSpotNum(),
                                    trafficVolume.getYmd(),
                                    trafficVolume.getHour()
                            )
                            .orElseGet(() ->
                                    trafficVolumeRepository.saveAndFlush(trafficVolume)
                            );

            log.info(
                    "교통량 DB 저장/조회 완료 jdbcUrl={}, spotNum={}, ymd={}, hour={}, volumeId={}, volumeTotalCount={}",
                    currentJdbcUrl(),
                    saved.getSpotNum(),
                    saved.getYmd(),
                    saved.getHour(),
                    saved.getId(),
                    trafficVolumeRepository.count()
            );

            saveRiskSnapshot(saved);

            return TrafficVolumeDto.Response.from(saved);

        } catch (Exception e) {
            throw new RuntimeException("교통량 동기화 실패", e);
        }
    }

    private void validateResponse(
            VolInfoResponse response
    ) {

        if(response == null){
            throw new RuntimeException(
                    "응답이 없습니다."
            );
        }

        if(response.getResult() == null){
            throw new RuntimeException(
                    "결과 정보 없음"
            );
        }

        if(!"INFO-000".equals(
                response.getResult().getCode()
        )){
            throw new RuntimeException(
                    response.getResult().getMessage()
            );
        }

        if(response.getRows() == null ||
                response.getRows().isEmpty()){

            throw new RuntimeException(
                    "교통량 데이터 없음"
            );
        }
    }

    @Scheduled(cron = "0 30 * * * *")
        public void collectTrafficData() {
            List<TrafficSpot> spots =
                    trafficSpotRepository.findAll();

        String today =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.BASIC_ISO_DATE
                        );


        LocalDateTime targetTime =
                LocalDateTime.now()
                        .minusHours(1);

        String hour =
                String.valueOf(
                        targetTime.getHour()
                );

        log.info(
                "교통 수집 시작 ymd={}, hour={}",
                today,
                hour
        );

        collect(spots, today, hour) ;
        clearRiskCache();
    }

    public void collect (
            List<TrafficSpot> spots,
            String ymd,
            String hour
    ){
        List<CompletableFuture<Void>> futures =
                spots.stream()
                        .map(
                                spot ->
                                        CompletableFuture.runAsync(
                                                () ->
                                                        syncVolInfo(
                                                                spot.getSpotNum(),
                                                                ymd,
                                                                hour
                                                        ),
                                                executor
                                        )
                        )
                        .toList();

        CompletableFuture.allOf(
                futures.toArray(
                        CompletableFuture[]::new
                )
        ).join();
    }

    public List<TrafficVolumeDto.VolumeResponse> getTrafficVolume(String spotNum, String ymd) {
        LocalDate date =
                LocalDate.parse(
                        ymd,
                        DateTimeFormatter.BASIC_ISO_DATE
                );
        List<TrafficVolume> trafficVolumes =
                trafficVolumeRepository
                        .findAllBySpotNumAndYmd(
                                spotNum,
                                date
                        )
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "교통량 없음"
                                        )
                        );
        if (trafficVolumes.isEmpty()) {
            throw new RuntimeException("교통량 없음");
        }

        return trafficVolumes.stream()
                .map(TrafficVolumeDto.VolumeResponse::from)
                .toList();
    }

    public TrafficVolumeDto.RiskResponse getRiskInfo(String spotNum, String ymd, int hour) {
        LocalDate date =
                LocalDate.parse(
                        ymd,
                        DateTimeFormatter.BASIC_ISO_DATE
                );

        TrafficVolume current =
                trafficVolumeRepository
                        .findBySpotNumAndYmdAndHour(
                                spotNum,
                                date,
                                hour
                        )
                        .orElseThrow();

        TrafficRisk trafficRisk =
                trafficRiskRepository
                        .findBySpotNum(spotNum)
                        .orElseThrow();

        TrafficVolumeDto.RiskResponse response = TrafficVolumeDto.RiskResponse
                .builder()
                .hour(hour)
                .riskScore(trafficRisk.getRiskScore())
                .averageVolume(trafficRisk.getAverageVolume())
                .spotNum(spotNum)
                .currentVolume(Double.parseDouble(current.getTotalVolume().toString()))
                .status(trafficRisk.getStatus())
                .build();

        return response;
    }

    @Cacheable(
            value = "trafficRisks",
            key = "#ymd + ':' + #hour",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<TrafficVolumeDto.RiskResponses> getRisksInfo(String ymd, int hour) {
        LocalDate date = LocalDate.parse(ymd, DateTimeFormatter.BASIC_ISO_DATE);

        List<TrafficRisk> risks = trafficRiskRepository.findByYmdAndHour(date, hour);
        if (risks.isEmpty()) return Collections.emptyList();

        List<TrafficVolumeDto.RiskResponses> responseList = new ArrayList<>();
        for (TrafficRisk risk : risks) {
            TrafficSpot spot = trafficSpotRepository.findBySpotNum(risk.getSpotNum()).orElse(null);
            if (spot == null) continue;

            responseList.add(TrafficVolumeDto.RiskResponses.builder()
                    .spotNum(risk.getSpotNum())
                    .spotName(spot.getSpotName())
                    .riskScore(risk.getRiskScore())
                    .status(risk.getStatus())
                    .tmX(spot.getTmX())
                    .tmY(spot.getTmY())
                    .build());
        }
        return responseList;
    }

    @Cacheable(
            value = "trafficDetail",
            key = "#spotNum + ':' + #ymd + ':' + #hour",
            unless = "#result == null || #result.riskScore == null || #result.todayVolumes == null || #result.todayVolumes.isEmpty()"
    )
    public TrafficVolumeDto.Detail getDetailInfo(String spotNum, String ymd, int hour) {
        LocalDate date =
                LocalDate.parse(
                        ymd,
                        DateTimeFormatter.BASIC_ISO_DATE
                );

        List<TrafficVolume> trafficVolumes = trafficVolumeRepository.findAllBySpotNumAndYmd(spotNum, date)
                .orElseThrow();

        // trafficVolume을 바꿔주면 된다.
        List<TrafficVolumeDto.VolumeResponse> volumes =
                trafficVolumes.stream()
                        .map(
                                TrafficVolumeDto
                                        .VolumeResponse::from
                        )
                        .toList();

        TrafficSpot spot =
                trafficSpotRepository
                        .findBySpotNum(
                                spotNum
                        )
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "지점 없음"
                                        )
                        );

        TrafficRisk trafficRisk = trafficRiskRepository.findBySpotNumAndYmdAndHour(spotNum, date, hour)
                .orElseThrow();

        String message = trafficEventRepository.findLatestBySpotAndHourAndDate(
                spotNum,
                hour,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay()
        ).map(TrafficEvent::getMessage).orElse(null);

        return TrafficVolumeDto.Detail.builder()
                .spotName(spot.getSpotName())
                .spotNum(spotNum)
                .riskScore(trafficRisk.getRiskScore())
                .status(trafficRisk.getStatus())
                .todayVolumes(volumes)
                .aiAnomaly(trafficRisk.getAiAnomaly())
                .aiScore(trafficRisk.getAiScore())
                .aiStatus(trafficRisk.getAiStatus())
                .message(message)
                .build();
    }

    private Status getStatus(
            Double riskScore
    ) {

        if (riskScore >= 180) {
            return Status.DANGER;
        }

        if (riskScore >= 120) {
            return Status.WARNING;
        }

        return Status.NORMAL;
    }

    private void saveRiskSnapshot(
            TrafficVolume saved
    ){

        Double average =
                trafficVolumeRepository
                        .findAverageVolume(
                                saved.getSpotNum()
                        );

        Double riskScore =
                (average == null || average == 0)
                        ? 100.0
                        : saved.getTotalVolume()
                          / average
                          *100;

        Status status =
                getStatus(
                        riskScore
                );

        AiPredictResponse aiResponse =
                requestAiPrediction(saved);

        TrafficRisk risk =
                trafficRiskRepository
                        .findBySpotNumAndYmdAndHour(
                                saved.getSpotNum(),
                                saved.getYmd(),
                                saved.getHour()
                        )
                        .orElse(
                                TrafficRisk.builder()
                                        .spotNum(
                                                saved.getSpotNum()
                                        )
                                        .ymd(
                                                saved.getYmd()
                                        )
                                        .hour(
                                                saved.getHour()
                                        )
                                        .build()
                        );


        risk.update(
                saved.getTotalVolume()
                        .doubleValue(),
                average,
                riskScore,
                status,
                aiResponse.getAnomaly(),
                aiResponse.getAiScore(),
                aiResponse.getStatus()
        );

        trafficRiskRepository.save(
                risk
        );

        trafficRiskRepository.flush();

        log.info(
                "교통 위험도 DB 저장 완료 jdbcUrl={}, spotNum={}, ymd={}, hour={}, riskId={}, riskTotalCount={}",
                currentJdbcUrl(),
                risk.getSpotNum(),
                risk.getYmd(),
                risk.getHour(),
                risk.getId(),
                trafficRiskRepository.count()
        );

        // 일정 수준 이상으로 위험도가 높아진다면 ? 이벤트로 저장 .
        if (!status.equals(Status.NORMAL)) {
            LocalDateTime targetTime =
                    risk.getYmd().atTime(risk.getHour(), 0);

            LocalDateTime startTime =
                    targetTime.minusHours(1);

            LocalDateTime endTime =
                    targetTime.plusHours(1);

            TrafficSpot spot = trafficSpotRepository.findBySpotNum(saved.getSpotNum())
                    .orElseThrow();


            String keyword =
                    extractRoadKeyword(
                            spot.getSpotName()
                    );

            // 시작시간 ~ 끝 시간 사이에 도로명이 속해있는 incident 데이터 갖고 오기
            List<TrafficIncident> incidents =
                    trafficIncidentRepository.findMatchedIncidents(
                            startTime,
                            endTime,
                            keyword
                    );

            String message =
                    buildEventMessage(
                            riskScore,
                            incidents,
                            aiResponse.getAnomaly()
                    );

            TrafficEvent trafficEvent =
                    trafficEventRepository
                            .findLatestBySpotAndHourAndDate(
                                    saved.getSpotNum(),
                                    saved.getHour(),
                                    saved.getYmd().atStartOfDay(),
                                    saved.getYmd().plusDays(1).atStartOfDay()
                            )
                            .orElseGet(() ->
                                    TrafficEvent.create(
                                            saved.getSpotNum(),
                                            saved.getHour(),
                                            riskScore,
                                            message,
                                            status
                                    )
                            );

            trafficEvent.update(
                    riskScore,
                    message,
                    status
            );
            trafficEventRepository.save(trafficEvent);

            TrafficAlertDto alert =
                    TrafficAlertDto.builder()
                            .spotNum(
                                    trafficEvent.getSpotNum()
                            )
                            .hour(
                                    trafficEvent.getHour()
                            )
                            .riskScore(
                                    trafficEvent.getRiskRate()
                            )
                            .status(
                                    trafficEvent.getStatus()
                                            .name()
                            )
                            .message(
                                    trafficEvent.getMessage()
                            )
                            .build();

            messagingTemplate.convertAndSend(
                    "/topic/traffic-alerts",
                    alert
            );
        }
    }

    public List<TrafficEventDto.Response> getEventsInfo() {
        return trafficEventRepository.findAll()
                .stream()
                .map(
                        TrafficEventDto.Response::from
                )
                .toList();
    }

    public List<TrafficDatasetDto.Response> getDataset() {
        return trafficVolumeRepository
                .findAll()
                .stream()
                .map(
                        TrafficDatasetDto.Response::from
                )
                .toList();
    }

    private AiPredictResponse requestAiPrediction(TrafficVolume saved) {
        try {
            AiPredictRequest request =
                    AiPredictRequest.builder()
                            .hour(saved.getHour())
                            .inVolume(saved.getInVolume())
                            .outVolume(saved.getOutVolume())
                            .totalVolume(saved.getTotalVolume())
                            .dayOfWeek(
                                    saved.getYmd()
                                            .getDayOfWeek()
                                            .getValue()
                            )
                            .build();

            AiPredictResponse response = topisRestClient.post()
                    .uri("http://localhost:8000/predict")
                    .body(request)
                    .retrieve()
                    .body(AiPredictResponse.class);

            return response != null ? response : fallbackAiResponse();
        } catch (Exception e) {
            log.warn("AI 예측 서버 연결 실패, 기본값 사용: spotNum={}, error={}", saved.getSpotNum(), e.getMessage());
            return fallbackAiResponse();
        }
    }

    private AiPredictResponse fallbackAiResponse() {
        AiPredictResponse fallback = new AiPredictResponse();
        fallback.setAnomaly(false);
        fallback.setAiScore(null);
        fallback.setStatus("UNAVAILABLE");
        return fallback;
    }

    // 실시간 돌발 교통 정보 갖고 오는 함수 (5분마다)
    @Scheduled(cron = "0 */5 * * * *")
    public void collectIncidentInfo() {
        log.info("실시간 돌발 교통 정보 수집 시작");

        try {
            IncidentDto.Response response = topisRestClient.get()
                    .uri("/{key}/xml/AccInfo/1/100",
                            apiConfig.getCity().getServiceKey())
                    .retrieve()
                    .body(IncidentDto.Response.class);

            if (response == null || response.getRows() == null) {
                log.warn("돌발 정보 없음");
                return ;
            }

            response.getRows().forEach(row -> {
                try {
                    LocalDateTime occurTime =
                            parseDateTime(
                                    row.getOccurDate(),
                                    row.getOccurTime()
                            );

                    LocalDateTime expectedClearTime =
                            parseDateTime(
                                    row.getExpectedClearDate(),
                                    row.getExpectedClearTime()
                            );

                    TrafficIncident incident =
                            trafficIncidentRepository
                                    .findByAccId(row.getAccId())
                                    .orElse(
                                            TrafficIncident.create(
                                                    row.getAccId(),
                                                    occurTime,
                                                    expectedClearTime,
                                                    row.getAccType(),
                                                    row.getAccDType(),
                                                    row.getLinkId(),
                                                    row.getGrs80tmX(),
                                                    row.getGrs80tmY(),
                                                    row.getAccInfo()
                                            )
                                    );
                    trafficIncidentRepository.save(
                            incident
                    );
                } catch (Exception e) {
                    log.warn(
                            "돌발 정보 저장 실패/스킵: accId={}, date={}, time={}, reason={}",
                            row.getAccId(),
                            row.getOccurDate(),
                            row.getOccurTime(),
                            e.getMessage()
                    );
                }
            });

            log.info("실시간 돌발 교통 정보 수집 완료");

            log.info("돌발 교통 정보 업데이트");
            updateEventIncidents();
            clearDetailCache();
        } catch (Exception e) {
            log.error("돌발 교통 정보 수집 실패", e);
        }
    }

    private void updateEventIncidents() {
        LocalDateTime now = LocalDateTime.now();

        // 최근 1시간 이벤트
        List<TrafficEvent> events =
                trafficEventRepository
                        .findRecentEvents(
                                now.minusHours(1)
                                );

        for (TrafficEvent event : events) {
            TrafficSpot spot =
                    trafficSpotRepository
                            .findBySpotNum(
                                    event.getSpotNum()
                            )
                            .orElse(null);

            if (spot == null) continue;

            String keyword =
                    extractRoadKeyword(
                            spot.getSpotName()
                    );

            LocalDateTime target =
                    now.withHour(
                                    event.getHour()
                            )
                            .withMinute(0);

            List<TrafficIncident> incidents =
                    trafficIncidentRepository
                                    .findMatchedIncidents(
                                            target.minusHours(1),
                                            target.plusHours(1),
                                            keyword
                                    );

            if (incidents.isEmpty()) continue;

            String message =
                    buildEventMessage(
                            event.getRiskRate(),
                            incidents,
                            false
                    );

            event.updateMessage(
                    message
            );
            trafficEventRepository.save(
                    event
            );
        }
    }

    private String buildEventMessage(
            Double riskScore,
            List<TrafficIncident> incidents,
            Boolean aiAnomaly
    ) {
        StringBuilder message =
                new StringBuilder(
                        "위험 : " + riskScore
                );

        if (!incidents.isEmpty()) {
            message.append("\n원인 추정:");

            incidents.forEach(
                    incident ->
                            message.append("\n- ")
                                    .append(incident.getAccType())
                                    .append(" : ")
                                    .append(incident.getAccInfo())
            );

            return message.toString();
        }

        if (Boolean.TRUE.equals(aiAnomaly)) {
            message.append(
                    "\n원인 미확인 (AI 이상 패턴 감지)"
            );
        } else {
            message.append(
                    "\n원인 미확인 (교통량 위험 증가)"
            );
        }

        return message.toString();
    }

    private String extractRoadKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "";
        }

        int idx =
                keyword.indexOf("(");

        return idx == -1
                ? keyword.trim()
                : keyword.substring(
                0,
                idx
        ).trim();
    }


    private LocalDateTime parseDateTime(
            String date,
            String time
    ) {
        String d = date.trim();
        String t = time.trim();

        // 1425 -> 142500
        if (t.length() == 4) {
            t = t + "00";
        }

        // 925 -> 092500
        if (t.length() == 3) {
            t = "0" + t + "00";
        }

        // 091300 -> 그대로
        if (t.length() == 6) {
            // 그대로 사용
        }

        if (t.length() != 6) {
            throw new RuntimeException("지원하지 않는 시간 형식: " + time);
        }

        return LocalDateTime.parse(
                d + t,
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );
    }

    private void clearRiskCache() {
        Cache cache =
                cacheManager.getCache("trafficRisks");

        if (cache != null) {
            cache.clear();
        }

        clearDetailCache();

    }
    private void clearDetailCache() {
        Optional.ofNullable(
                cacheManager.getCache("trafficDetail")
        ).ifPresent(Cache::clear);
    }
}
