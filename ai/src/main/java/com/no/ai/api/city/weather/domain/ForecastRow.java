package com.no.ai.api.city.weather.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ForecastRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String regId;
    private String tmFc;
    private String tmEf;
    private String mod;
    private Integer ne;
    private String stn;
    private String c;
    private String manId;
    private String manFc;

    private String windDirectionStart;
    private String windTrend;
    private String windDirectionEnd;

    private Integer temperature;
    private Integer rainProbability;
    private String skyCode;
    private Integer precipitationCode;
    private String weatherText;
}
