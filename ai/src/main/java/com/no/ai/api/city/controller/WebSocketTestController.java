package com.no.ai.api.city.controller;
import com.no.ai.api.city.dto.TrafficAlertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebSocketTestController {

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/api/test-alert")
    public String testAlert() {

        TrafficAlertDto alert =
                TrafficAlertDto.builder()
                        .spotNum("C-02")
                        .hour(15)
                        .riskScore(188.5)
                        .status("DANGER")
                        .message("테스트 위험 알림")
                        .build();

        messagingTemplate.convertAndSend(
                "/topic/traffic-alerts",
                alert
        );

        return "sent";
    }
}