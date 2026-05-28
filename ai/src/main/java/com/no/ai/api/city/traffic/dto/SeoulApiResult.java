package com.no.ai.api.city.traffic.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeoulApiResult {
    @JacksonXmlProperty(localName = "CODE")
    private String code;

    @JacksonXmlProperty(localName = "MESSAGE")
    private String message;
}
