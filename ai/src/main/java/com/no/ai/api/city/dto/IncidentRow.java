package com.no.ai.api.city.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentRow {
    @JacksonXmlProperty(localName = "acc_id")
    private String accId;

    @JacksonXmlProperty(localName = "occr_date")
    private String occurDate;

    @JacksonXmlProperty(localName = "occr_time")
    private String occurTime;

    @JacksonXmlProperty(localName = "exp_clr_date")
    private String expectedClearDate;

    @JacksonXmlProperty(localName = "exp_clr_time")
    private String expectedClearTime;

    @JacksonXmlProperty(localName = "acc_type")
    private String accType;

    @JacksonXmlProperty(localName = "acc_dtype")
    private String accDType;

    @JacksonXmlProperty(localName = "link_id")
    private String linkId;

    @JacksonXmlProperty(localName = "grs80tm_x")
    private Double grs80tmX;

    @JacksonXmlProperty(localName = "grs80tm_y")
    private Double grs80tmY;

    @JacksonXmlProperty(localName = "acc_info")
    private String accInfo;
}
