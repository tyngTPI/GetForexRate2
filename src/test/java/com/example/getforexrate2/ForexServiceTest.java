package com.example.getforexrate2;

import com.example.getforexrate2.dto.req.CurrencyRateReq;
import com.example.getforexrate2.dto.resp.CurrencyRateResp;
import com.example.getforexrate2.model.ForexRate;
import com.example.getforexrate2.repository.ForexRateRepository;
import com.example.getforexrate2.service.ForexService;
import com.example.getforexrate2.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ForexServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ForexRateRepository forexRateRepository;

    @InjectMocks
    private ForexService forexService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveForexData() {
        // 模擬 API 回傳的 JSON 資料
        String jsonResponse = "[{\"Date\":\"20250102\",\"USD/NTD\":\"32.868\",\"RMB/NTD\":\"4.486909\",\"EUR/USD\":\"1.03575\""+
                ",\"USD/JPY\":\"156.695\",\"GBP/USD\":\"1.25105\",\"AUD/USD\":\"0.62065\""+
                ",\"USD/HKD\":\"7.77565\",\"USD/RMB\":\"7.3253\",\"USD/ZAR\":\"18.7963\",\"NZD/USD\":\"0.5608\"}]";

        // 模擬 RestTemplate
        when(restTemplate.getForObject(anyString(), any())).thenReturn(jsonResponse);

        // 模擬資料庫查詢（資料庫無資料）
        when(forexRateRepository.findDatesByDateIn(anyList())).thenReturn(Collections.emptyList());

        // 執行測試方法
        forexService.saveForexData();

        // 驗證 RestTemplate 是否被呼叫
        verify(restTemplate, times(1)).getForObject(anyString(), any());

        // 驗證 Repository 的 save 方法是否被呼叫
        verify(forexRateRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testSaveForexData_DataExists() {
        // 模擬 API 回傳的 JSON 資料
        String jsonResponse = "[{\"Date\":\"20250102\",\"USD/NTD\":\"32.868\",\"RMB/NTD\":\"4.486909\",\"EUR/USD\":\"1.03575\""+
                ",\"USD/JPY\":\"156.695\",\"GBP/USD\":\"1.25105\",\"AUD/USD\":\"0.62065\""+
                ",\"USD/HKD\":\"7.77565\",\"USD/RMB\":\"7.3253\",\"USD/ZAR\":\"18.7963\",\"NZD/USD\":\"0.5608\"}]";

        // 模擬 RestTemplate
        when(restTemplate.getForObject(anyString(), any())).thenReturn(jsonResponse);

        // 模擬資料庫查詢（資料已存在）
        ForexRate forexRate = new ForexRate();
        forexRate.setDate(DateUtil.apiToDbDateFormat("20250102"));
        when(forexRateRepository.findDatesByDateIn(anyList())).thenReturn(Arrays.asList(forexRate));

        // 執行測試方法
        forexService.saveForexData();

        // 驗證 RestTemplate 是否被呼叫
        verify(restTemplate, times(1)).getForObject(anyString(), any());

        // 驗證 Repository 的 saveAll 方法沒有被呼叫（因為資料存在）
        verify(forexRateRepository, never()).saveAll(anyList());
    }

    @Test
    void testFetchAndSaveForexDataWithEmptyResponse() {
        // 模擬 API 回傳空資料
        when(restTemplate.getForObject(anyString(), any())).thenReturn(null);

        // 執行測試方法
        forexService.saveForexData();

        // 驗證 Repository 的 saveAll 方法沒有被呼叫
        verify(forexRateRepository, never()).saveAll(anyList());
    }

    @Test
    void testSaveForexData_ApiNoResponse() {
        // 模擬 RestTemplate 拋出 RestClientException（API無回應）
        when(restTemplate.getForObject(anyString(), any())).thenThrow(new RestClientException("API無回應"));

        // 執行測試方法
        forexService.saveForexData();

        // 驗證 RestTemplate 是否被呼叫
        verify(restTemplate, times(1)).getForObject(anyString(), any());

        // 驗證 Repository 的 saveAll 方法沒有被呼叫
        verify(forexRateRepository, never()).saveAll(anyList());
    }

    @Test
    void testSaveForexData_InvalidJsonFormat() {
        // 模擬 API 回傳的 JSON 格式不符
        String invalidJsonResponse = "[InvalidJsonFormat]";

        // 模擬 RestTemplate
        when(restTemplate.getForObject(anyString(), any())).thenReturn(invalidJsonResponse);

        // 執行測試方法
        forexService.saveForexData();

        // 驗證 RestTemplate 是否被呼叫
        verify(restTemplate, times(1)).getForObject(anyString(), any());

        // 驗證 Repository 的 saveAll 方法沒有被呼叫
        verify(forexRateRepository, never()).saveAll(anyList());
    }

    @Test
    void testGetCurrencyRate_Success() {
        // 模擬Request
        CurrencyRateReq request = new CurrencyRateReq();
        request.setStartDate("2025/02/01");
        request.setEndDate("2025/02/03");
        request.setCurrency("usd");

        // 模擬資料庫查詢結果
        ForexRate rate1 = new ForexRate();
        rate1.setDate(DateUtil.reqToDbDateFormat("2025/02/01"));
        rate1.setCurrencyPair("USD/NTD");
        rate1.setRate(31.01);

        ForexRate rate2 = new ForexRate();
        rate2.setDate(DateUtil.reqToDbDateFormat("20240202"));
        rate2.setCurrencyPair("USD/NTD");
        rate2.setRate(31.02);

        when(forexRateRepository.findByDateRange(any(), any())).thenReturn(Arrays.asList(rate1, rate2));

        // 執行測試方法
        CurrencyRateResp response = forexService.getCurrencyRate(request);

        // 驗證回應
        assertEquals("0000", response.getError().getCode());
        assertEquals("成功", response.getError().getMessage());
        assertEquals(2, response.getCurrency().size());
    }

    @Test
    void testGetCurrencyRate_InvalidDateFormat() {
        // 模擬請求（日期格式不符）
        CurrencyRateReq request = new CurrencyRateReq();
        request.setStartDate("20220101");
        request.setEndDate("20220103");
        request.setCurrency("usd");

        // 執行測試方法
        CurrencyRateResp response = forexService.getCurrencyRate(request);

        // 驗證回應
        assertEquals("E002", response.getError().getCode());
        assertEquals("日期格式不符", response.getError().getMessage());
    }

    @Test
    void testGetCurrencyRate_InvalidDateRange() {
        // 模擬請求（日期區間不符）
        CurrencyRateReq request = new CurrencyRateReq();
        request.setStartDate("2022/01/01");
        request.setEndDate("2022/01/03");
        request.setCurrency("usd");

        // 執行測試方法
        CurrencyRateResp response = forexService.getCurrencyRate(request);

        // 驗證回應
        assertEquals("E001", response.getError().getCode());
        assertEquals("日期區間不符", response.getError().getMessage());
    }

    @Test
    void testGetCurrencyRate_InvalidCurrency() {
        // 模擬請求（幣別不符）
        CurrencyRateReq request = new CurrencyRateReq();
        request.setStartDate("2025/02/01");
        request.setEndDate("2025/02/03");
        request.setCurrency("eur");

        // 執行測試方法
        CurrencyRateResp response = forexService.getCurrencyRate(request);

        // 驗證回應
        assertEquals("E003", response.getError().getCode());
        assertEquals("僅可查詢美元 (USD)", response.getError().getMessage());
    }

}