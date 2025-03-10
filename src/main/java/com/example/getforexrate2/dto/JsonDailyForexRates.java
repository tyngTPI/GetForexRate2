package com.example.getforexrate2.dto;

import com.example.getforexrate2.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class JsonDailyForexRates {
    @JsonProperty("Date")
    private String date;

    @JsonProperty("USD/NTD")
    private Double usdToNtdRate;

    @JsonProperty("RMB/NTD")
    private Double rmbToNtdRate;

    @JsonProperty("EUR/USD")
    private Double eurToUsdRate;

    @JsonProperty("USD/JPY")
    private Double usdToJpyRate;

    @JsonProperty("GBP/USD")
    private Double gbpToUsdRate;

    @JsonProperty("AUD/USD")
    private Double audToUsdRate;

    @JsonProperty("USD/HKD")
    private Double usdToHkdRate;

    @JsonProperty("USD/RMB")
    private Double usdToRmbRate;

    @JsonProperty("USD/ZAR")
    private Double usdToZarRate;

    @JsonProperty("NZD/USD")
    private Double nzdToUsdRate;

    public Date getDate_ConvertDBFormat() {
        return DateUtil.apiToDbDateFormat(date);
    }
}
