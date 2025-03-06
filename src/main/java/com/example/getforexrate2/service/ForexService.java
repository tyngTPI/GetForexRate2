package com.example.getforexrate2.service;

import com.example.getforexrate2.dto.req.CurrencyRateReq;
import com.example.getforexrate2.dto.resp.CurrencyRateResp;
import com.example.getforexrate2.model.ForexRate;
import com.example.getforexrate2.model.JsonDailyForexRates;
import com.example.getforexrate2.repository.ForexRateRepository;
import com.example.getforexrate2.util.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForexService {

    @Autowired
    private ForexRateRepository forexRateRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";

    //@Scheduled(cron = "0 0 18 * * ?") // 每天18:00執行
    @Scheduled(cron = "0 0 * * * *") // 每小時執行一次
    public void saveForexData() {
        String jsonResp = restTemplate.getForObject(API_URL, String.class);

        if (jsonResp != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 將 JSON 字串解析為 List<JsonDailyForexRates>
                List<JsonDailyForexRates> listJsonRate = objectMapper.readValue(jsonResp, new TypeReference<>() {
                });

                // 取得所有日期的列表
                List<String> dates = listJsonRate.stream()
                        .map(JsonDailyForexRates::getDate_ConvertDBFormat)
                        .collect(Collectors.toList());

                // 查詢資料庫中已存在的日期
                List<ForexRate> existingRates = forexRateRepository.findDatesByDateIn(dates);
                List<String> existingDates = existingRates.stream()
                        .map(ForexRate::getDate)
                        .collect(Collectors.toList());

                // 過濾已存在日期的資料
                List<ForexRate> newRates = listJsonRate.stream()
                        .filter(jsonRate -> !existingDates.contains(jsonRate.getDate_ConvertDBFormat()))
                        .map(jsonRate -> {
                            ForexRate forexRate = new ForexRate();
                            forexRate.setDate(jsonRate.getDate_ConvertDBFormat());
                            forexRate.setCurrencyPair("USD/NTD");
                            forexRate.setRate(jsonRate.getUsdToNtdRate());
                            return forexRate;
                        })
                        .collect(Collectors.toList());

                // 批次寫入新資料
                if (!newRates.isEmpty()) {
                    forexRateRepository.saveAll(newRates);
                    //System.out.println("批次寫入成功。筆數: " + newRates.size());
                    for(ForexRate r : newRates) {
                        System.out.println("資料寫入 日期: " + r.getDate());
                    }
                } else {
                    System.out.println("無資料寫入");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CurrencyRateResp getCurrencyRate(CurrencyRateReq req) {
        CurrencyRateResp resp = new CurrencyRateResp();

        // 驗證日期格式 yyyy/MM/dd
        if (!DateUtil.isValidReqDate(req.getStartDate())||!DateUtil.isValidReqDate(req.getEndDate())) {
            resp.setError(new CurrencyRateResp.ErrorMsg("E001", "日期格式不符"));
            return resp;
        }

        String startDate = DateUtil.reqToDbDateFormat(req.getStartDate());
        String endDate = DateUtil.reqToDbDateFormat(req.getEndDate());

        // 驗證日期區間
        if (!DateUtil.isDateRangeValid(startDate, endDate)) {
            resp.setError(new CurrencyRateResp.ErrorMsg("E001", "日期區間不符"));
            return resp;
        }

        // 驗證幣別
        if (!"usd".equalsIgnoreCase(req.getCurrency())) {
            resp.setError(new CurrencyRateResp.ErrorMsg("E002", "僅可查詢美元 (USD)"));
            return resp;
        }

        System.out.println("查詢日期起: " + startDate);
        System.out.println("查詢日期迄: " + endDate);
        // 查詢資料庫
        List<ForexRate> forexRates = forexRateRepository.findByDateRange(startDate, endDate);

        // RESP資料產出
        List<CurrencyRateResp.CurrencyRateData> currencyDataList = new ArrayList<>();
        for (ForexRate rate : forexRates) {
            CurrencyRateResp.CurrencyRateData data = new CurrencyRateResp.CurrencyRateData();
            data.setDate(DateUtil.dbToRespDateFormat(rate.getDate()));
            data.setUsd(rate.getRate().toString()); // 匯率
            currencyDataList.add(data);
        }

        resp.setError(new CurrencyRateResp.ErrorMsg("0000", "成功"));
        resp.setCurrency(currencyDataList);
        return resp;
    }

}