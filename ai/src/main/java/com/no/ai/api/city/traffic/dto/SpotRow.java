package com.no.ai.api.city.traffic.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotRow {
    @JacksonXmlProperty(localName = "spot_num")
    private String spotNum;

    @JacksonXmlProperty(localName = "spot_nm")
    private String spotName;

    @JacksonXmlProperty(localName = "grs80tm_x")
    private Double tmX;

    @JacksonXmlProperty(localName = "grs80tm_y")
    private Double tmY;
}
