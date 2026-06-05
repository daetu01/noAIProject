package com.no.ai.api.city.traffic.dto;

import lombok.Builder;

@Builder
public record SpotInfoRequest(
        Integer page,
        Integer size
) {

}
