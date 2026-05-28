package com.no.ai.api.city.traffic.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JacksonXmlRootElement(localName = "SpotInfo")
public class SpotInfoResponse {
    @JacksonXmlProperty(localName = "list_total_count")
    private Integer listTotalCount;

    @JacksonXmlProperty(localName = "RESULT")
    private SeoulApiResult result;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "row")
    private List<SpotRow> rows;
}
