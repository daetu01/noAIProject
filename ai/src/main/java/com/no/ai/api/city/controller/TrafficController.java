package com.no.ai.api.city.controller;

import com.no.ai.api.city.dto.*;
import com.no.ai.api.city.service.TrafficService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
@RequiredArgsConstructor
public class TrafficController {
    private final TrafficService trafficService;

    @GetMapping("/spot")
    public List<TrafficVolumeDto.Response> getTrafficSpot(
            @RequestParam String ymd,
            @RequestParam String hour
    ) {
        return trafficService.getTrafficInfo(ymd, hour);
    }

    @GetMapping("/volumes")
    public List<TrafficVolumeDto.VolumeResponse> getTrafficInfoByYmd(
            @RequestParam String spotNum,
            @RequestParam String ymd
    ) {
        return trafficService.getTrafficVolume(spotNum, ymd);
    }

    @GetMapping("/risk")
    public TrafficVolumeDto.RiskResponse getRiskInfoByYmd(
            @RequestParam String spotNum,
            @RequestParam String ymd,
            @RequestParam int hour
    ){
        return trafficService.getRiskInfo(spotNum, ymd, hour);
    }

    @GetMapping("/risks")
    public List<TrafficVolumeDto.RiskResponses> getRisksInfoByYmd(
            @RequestParam String ymd,
            @RequestParam int hour
    ){
        return trafficService.getRisksInfo(ymd, hour);
    }

    @GetMapping("/details/{spotNum}")
    public TrafficVolumeDto.Detail getDetail(@PathVariable String spotNum, @RequestParam String ymd, @RequestParam int hour) {
        return trafficService.getDetailInfo(spotNum, ymd, hour);
    }

    @GetMapping("/events")
    public List<TrafficEventDto.Response> getEvents() {
        return trafficService.getEventsInfo();
    }

    @GetMapping("/dataset")
    public List<TrafficDatasetDto.Response>
    getDataset() {

        return trafficService.getDataset();
    }
}
