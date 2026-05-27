package com.no.ai.api.city.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
public class IncidentDto {
    @Getter
    @NoArgsConstructor
    @JacksonXmlRootElement(localName = "AccInfo")
    public static class Response {

        @JacksonXmlProperty(localName = "list_total_count")
        private Integer listTotalCount;

        @JacksonXmlProperty(localName = "RESULT")
        private SeoulApiResult result;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "row")
        private List<IncidentRow> rows;
    }
}
