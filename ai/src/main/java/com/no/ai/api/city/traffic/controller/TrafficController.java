package com.no.ai.api.city.traffic.controller;

import com.no.ai.api.city.traffic.dto.TrafficDatasetDto;
import com.no.ai.api.city.traffic.dto.TrafficEventDto;
import com.no.ai.api.city.traffic.dto.TrafficVolumeDto;
import com.no.ai.api.city.traffic.service.TrafficService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
@RequiredArgsConstructor
@Tag(
        name = "Traffic API",
        description = "교통 정보 및 위험도 관련 API"
)
public class TrafficController {
    private final TrafficService trafficService;

    @Operation(
            summary = "교통 지점별 교통량 수집 및 조회",
            description = "서울시 교통 지점 정보를 동기화하고, 요청한 날짜와 시간의 지점별 교통량을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "교통 지점별 교통량 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrafficVolumeDto.Response.class)))
            )
    })
    @GetMapping("/spot")
    public List<TrafficVolumeDto.Response> getTrafficSpot(
            @Parameter(description = "조회 날짜(yyyyMMdd)", example = "20260527", required = true)
            @RequestParam String ymd,
            @Parameter(description = "조회 시간(0~23)", example = "9", required = true)
            @RequestParam String hour
    ) {
        return trafficService.getTrafficInfo(ymd, hour);
    }

    @Operation(
            summary = "특정 지점의 일별 시간대 교통량 조회",
            description = "특정 교통 지점의 하루 시간대별 총 교통량을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "시간대별 교통량 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrafficVolumeDto.VolumeResponse.class)))
            )
    })
    @GetMapping("/volumes")
    public List<TrafficVolumeDto.VolumeResponse> getTrafficInfoByYmd(
            @Parameter(description = "교통 지점 번호", example = "A-01", required = true)
            @RequestParam String spotNum,
            @Parameter(description = "조회 날짜(yyyyMMdd)", example = "20260527", required = true)
            @RequestParam String ymd
    ) {
        return trafficService.getTrafficVolume(spotNum, ymd);
    }

    @Operation(
            summary = "특정 지점 위험도 조회",
            description = "특정 지점, 날짜, 시간에 대한 교통 위험도와 AI 이상 탐지 결과를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "위험도 조회 성공",
                    content = @Content(schema = @Schema(implementation = TrafficVolumeDto.RiskResponse.class))
            )
    })
    @GetMapping("/risk")
    public TrafficVolumeDto.RiskResponse getRiskInfoByYmd(
            @Parameter(description = "교통 지점 번호", example = "A-01", required = true)
            @RequestParam String spotNum,
            @Parameter(description = "조회 날짜(yyyyMMdd)", example = "20260527", required = true)
            @RequestParam String ymd,
            @Parameter(description = "조회 시간(0~23)", example = "9", required = true)
            @RequestParam int hour
    ){
        return trafficService.getRiskInfo(spotNum, ymd, hour);
    }

    @Operation(
            summary = "전체 지점 위험도 조회",
            description = "지도 표시에 필요한 전체 교통 지점의 위험도, 지점명, 좌표를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 지점 위험도 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrafficVolumeDto.RiskResponses.class)))
            )
    })
    @GetMapping("/risks")
    public List<TrafficVolumeDto.RiskResponses> getRisksInfoByYmd(
            @Parameter(description = "조회 날짜(yyyyMMdd)", example = "20260527", required = true)
            @RequestParam String ymd,
            @Parameter(description = "조회 시간(0~23)", example = "9", required = true)
            @RequestParam int hour
    ){
        return trafficService.getRisksInfo(ymd, hour);
    }

    @Operation(
            summary = "교통 지점 상세 조회",
            description = "특정 지점의 위험도, AI 분석 결과, 시간대별 교통량, 위험 원인 메시지를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "교통 지점 상세 조회 성공",
                    content = @Content(schema = @Schema(implementation = TrafficVolumeDto.Detail.class))
            )
    })
    @GetMapping("/details/{spotNum}")
    public TrafficVolumeDto.Detail getDetail(
            @Parameter(description = "교통 지점 번호", example = "A-01", required = true)
            @PathVariable String spotNum,
            @Parameter(description = "조회 날짜(yyyyMMdd)", example = "20260527", required = true)
            @RequestParam String ymd,
            @Parameter(description = "조회 시간(0~23)", example = "9", required = true)
            @RequestParam int hour
    ) {
        return trafficService.getDetailInfo(spotNum, ymd, hour);
    }

    @Operation(
            summary = "교통 위험 이벤트 목록 조회",
            description = "저장된 교통 위험 이벤트와 원인 추정 메시지를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "교통 위험 이벤트 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrafficEventDto.Response.class)))
            )
    })
    @GetMapping("/events")
    public List<TrafficEventDto.Response> getEvents() {
        return trafficService.getEventsInfo();
    }

    @Operation(
            summary = "AI 학습/분석용 교통 데이터셋 조회",
            description = "AI 이상 탐지 모델 입력에 사용할 수 있는 교통량 데이터셋을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "교통 데이터셋 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrafficDatasetDto.Response.class)))
            )
    })
    @GetMapping("/dataset")
    public List<TrafficDatasetDto.Response>
    getDataset() {
        return trafficService.getDataset();
    }
}
