package com.no.ai.api.city.service;

import com.no.ai.api.ApiConfig;

import com.no.ai.api.city.dto.SpotInfoResponse;
import com.no.ai.api.city.repository.TrafficSpotRepository;

import com.no.ai.api.city.domain.Status;
import com.no.ai.api.city.domain.TrafficSpot;
import com.no.ai.api.city.domain.TrafficVolume;
import com.no.ai.api.city.dto.*;

import com.no.ai.api.city.repository.TrafficVolumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class TrafficService {
    private final ApiConfig apiConfig;
    private final RestClient restClient;
    private final TrafficSpotRepository trafficSpotRepository;
    private final Executor executor;
    private final TrafficVolumeRepository trafficVolumeRepository;
    /**
     * 교통 지점 가져오는 정보
     * @return
     */
    public List<TrafficVolumeDto.Response> getTrafficInfo(String ymd, String hour) {
        try {
            SpotInfoResponse response = restClient.get()
                    .uri("/{key}/xml/SpotInfo/1/1000",
                            apiConfig.getCity().getServiceKey())
                    .retrieve()
                    .body(SpotInfoResponse.class);

            if (response == null ||
                    response.getRows() == null) {
                throw new RuntimeException("교통 지점 정보 없음");
            }

            response.getRows()
                    .forEach(row -> {
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

                            trafficSpotRepository.save(trafficSpot);

                        } catch (Exception e) {
                            log.warn("spot 저장 실패/스킵: {}", row.getSpotNum());
                        }
                    });



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


    /**
     * spotNum 바탕으로 교통량 리턴해주는
     * @param spotNum, ymd
     * @return
     */
    public VolInfoResponse fetchVolInfo(String spotNum, String ymd, String hour) {
        try {
            VolInfoResponse response = restClient.get()
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
                    trafficVolumeRepository.save(
                            trafficVolume
                    );

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

    @Scheduled(cron = "0 0 * * * *")
        public void collectTrafficData() {
            List<TrafficSpot> spots =
                    trafficSpotRepository.findAll();

        String today =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.BASIC_ISO_DATE
                        );

        String hour =
                String.valueOf(
                        LocalTime.now().getHour()
                );

        collect(spots, today, hour) ;
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

        Double average = trafficVolumeRepository.findAverageVolume(spotNum);

        Double riskScore = (average == null || average == 0.0)
                ? 100.0
                : current.getTotalVolume() / average * 100;

        Status status = getStatus(riskScore);

        TrafficVolumeDto.RiskResponse response = TrafficVolumeDto.RiskResponse
                .builder()
                .hour(hour)
                .riskScore(riskScore)
                .averageVolume(average)
                .spotNum(spotNum)
                .currentVolume(Double.parseDouble(current.getTotalVolume().toString()))
                .status(status)
                .build();

        return response;
    }

    public List<TrafficVolumeDto.RiskResponses> getRisksInfo(String ymd, int hour) {
        LocalDate date =
                LocalDate.parse(
                        ymd,
                        DateTimeFormatter.BASIC_ISO_DATE
                );

        List<TrafficVolume> trafficVolumes = trafficVolumeRepository.findByYmdAndHour(date, hour)
                .orElse(Collections.emptyList());
        if (trafficVolumes.isEmpty()) return Collections.emptyList();
        List<TrafficVolumeDto.RiskResponses> responseList = new ArrayList<>();



        for (TrafficVolume trafficVolume : trafficVolumes) {
            TrafficSpot trafficSpot = trafficSpotRepository.findBySpotNum(trafficVolume.getSpotNum())
                    .orElseThrow();
            String spotName = trafficSpot.getSpotName();

            Double average = trafficVolumeRepository.findAverageVolume(trafficVolume.getSpotNum());

            Double riskScore = (average == null || average == 0.0)
                    ? 100.0
                    : trafficVolume.getTotalVolume() / average * 100;

            Status status = getStatus(riskScore);

            TrafficVolumeDto.RiskResponses riskResponse = TrafficVolumeDto.RiskResponses.builder()
                    .riskScore(riskScore)
                    .spotNum(trafficVolume.getSpotNum())
                    .spotName(spotName)
                    .status(status)
                    .tmX(trafficSpot.getTmX())
                    .tmY(trafficSpot.getTmY())
                    .build();

            responseList.add(riskResponse);
        }

        return responseList;
    }

    public TrafficVolumeDto.Detail getDetailInfo(String spotNum, String ymd) {
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
        // 최신 시간 데이터 사용
        TrafficVolume latest =
                trafficVolumes.stream()
                        .max(
                                Comparator.comparing(
                                        TrafficVolume::getHour
                                )
                        )
                        .orElseThrow();

        Double average = trafficVolumeRepository.findAverageVolume(spotNum);

        Double riskScore = (average == null || average == 0.0)
                ? 100.0
                : latest.getTotalVolume() / average * 100;

        Status status = getStatus(riskScore);


        return TrafficVolumeDto.Detail.builder()
                .spotName(
                        spot.getSpotName()
                )
                .spotNum(
                        spotNum
                )
                .riskScore(
                        riskScore
                )
                .status(
                        status
                )
                .todayVolumes(
                        volumes
                )
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


}
