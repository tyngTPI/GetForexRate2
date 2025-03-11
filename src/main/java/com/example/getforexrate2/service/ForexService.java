package com.example.getforexrate2.service;

import com.example.getforexrate2.dto.req.CurrencyRateReq;
import com.example.getforexrate2.dto.resp.CurrencyRateResp;
import com.example.getforexrate2.model.ForexRate;
import com.example.getforexrate2.dto.JsonDailyForexRates;
import com.example.getforexrate2.repository.ForexRateRepository;
import com.example.getforexrate2.util.DateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ForexService {

    private static final Logger logger = LoggerFactory.getLogger(ForexService.class);

    @Autowired
    private ForexRateRepository forexRateRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://openapi.taifex.com.tw/v1/DailyForeignExchangeRates";

    /**
     * 從 API 獲取外匯資料(USD/NTD)，並儲存到資料庫。
     * 此方法會定期執行或外部呼叫執行
     */
    @Scheduled(cron = "${forex.schedule.cron}")
    public void saveForexData() {
        logger.info("開始呼叫外匯API");
        String jsonResp = restTemplate.getForObject(API_URL, String.class);

        if (jsonResp != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // 將 JSON 字串解析為 List<JsonDailyForexRates>
                List<JsonDailyForexRates> listJsonRate = objectMapper.readValue(jsonResp, new TypeReference<>() {
                });

                // 取得API中所有日期的列表
                List<Date> dates = listJsonRate.stream()
                        .map(JsonDailyForexRates::getDateConvertDBFormat)
                        .collect(Collectors.toList());

                // 查詢資料庫中已存在的日期
                List<ForexRate> existingRates = forexRateRepository.findDatesByDateIn(dates);
                List<Date> existingDates = existingRates.stream()
                        .map(ForexRate::getDate)
                        .collect(Collectors.toList());

                // 過濾API中已存在資料庫日期的資料
                List<ForexRate> newRates = listJsonRate.stream()
                        .filter(jsonRate -> !existingDates.contains(jsonRate.getDateConvertDBFormat()))
                        .map(jsonRate -> {
                            ForexRate forexRate = new ForexRate();
                            forexRate.setDate(jsonRate.getDateConvertDBFormat());
                            forexRate.setCurrencyPair("USD/NTD");
                            forexRate.setRate(jsonRate.getUsdToNtdRate());
                            return forexRate;
                        })
                        .collect(Collectors.toList());

                // 批次寫入新資料
                if (!newRates.isEmpty()) {
                    forexRateRepository.saveAll(newRates);
                    for(ForexRate r : newRates) {
                        logger.info("資料寫入 日期: {}", DateUtil.dateToStr(r.getDate()));
                    }
                } else {
                    logger.info("無資料寫入");
                }
            } catch (Exception e) {
                logger.error("獲取或寫入外匯資料時發生錯誤", e);
            }
        } else {
            logger.warn("外匯API回傳的資料為空");
        }
    }

    /**
     * 依據請求參數，查詢指定日期區間的匯率資料
     */
    public CurrencyRateResp getCurrencyRate(CurrencyRateReq req) {
        CurrencyRateResp resp = new CurrencyRateResp();

        // 驗證日期格式 yyyy/MM/dd
        if (!DateUtil.isValidReqDate(req.getStartDate())||!DateUtil.isValidReqDate(req.getEndDate())) {
            resp.setError(new CurrencyRateResp.ErrorMsg("E001", "日期格式不符"));
            logger.warn("日期格式不符: startDate={}, endDate={}", req.getStartDate(), req.getEndDate());
            return resp;
        }

        Date startDate = DateUtil.reqToDbDateFormat(req.getStartDate());
        Date endDate = DateUtil.reqToDbDateFormat(req.getEndDate());

        // 驗證日期區間
        if (!DateUtil.isDateRangeValid(startDate, endDate)) {
            resp.setError(new CurrencyRateResp.ErrorMsg("E001", "日期區間不符"));
            logger.warn("日期區間不符: startDate={}, endDate={}", req.getStartDate(), req.getEndDate());
            return resp;
        }

        // 驗證幣別
        if (!"usd".equalsIgnoreCase(req.getCurrency())) {
            resp.setError(new CurrencyRateResp.ErrorMsg("E002", "僅可查詢美元 (USD)"));
            logger.warn("幣別不符: currency={}", req.getCurrency());
            return resp;
        }

        logger.info("查詢日期起迄: startDate={}, endDate={}", req.getStartDate(), req.getEndDate());

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
        logger.info("查詢成功，返回 {} 筆資料", currencyDataList.size());
        return resp;
    }

}