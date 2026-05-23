package com.no.ai.api.city.service;

import com.no.ai.api.ApiConfig;
import com.no.ai.api.city.dto.SpotInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


@RequiredArgsConstructor
@Service
public class TrafficService {
    private final ApiConfig apiConfig;
    private final RestClient restClient;

    /**
     * 교통 지점 가져오는 정보
     * @return
     */
    public SpotInfoResponse getTrafficInfo() {
        try {
            SpotInfoResponse response = restClient.get()
                    .uri("/{key}/xml/SpotInfo/1/5",
                            apiConfig.getCity().getServiceKey())
                    .retrieve()
                    .body(SpotInfoResponse.class);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
