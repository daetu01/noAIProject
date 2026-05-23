package com.no.ai.api.city.dto;

import lombok.Builder;

@Builder
public record SpotInfoRequest(
        Integer page,
        Integer size
) {

}
