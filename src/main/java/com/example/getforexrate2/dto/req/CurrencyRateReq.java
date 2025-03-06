package com.example.getforexrate2.dto.req;

import lombok.Data;

@Data
public class CurrencyRateReq {
    private String startDate; // 起始日期
    private String endDate;   // 結束日期
    private String currency;  // 幣別
}
