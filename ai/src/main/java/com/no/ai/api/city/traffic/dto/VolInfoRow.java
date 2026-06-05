package com.no.ai.api.city.traffic.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VolInfoRow {

    @JacksonXmlProperty(localName = "spot_num")
    private String spotNum;

    @JacksonXmlProperty(localName = "ymd")
    private String ymd;

    @JacksonXmlProperty(localName = "hh")
    private String hour;

    @JacksonXmlProperty(localName = "io_type")
    private String ioType;

    @JacksonXmlProperty(localName = "lane_num")
    private Integer laneNum;

    @JacksonXmlProperty(localName = "vol")
    private Integer volume;

}
