package com.example.getforexrate2;

import com.example.getforexrate2.controller.ForexController;
import com.example.getforexrate2.dto.req.CurrencyRateReq;
import com.example.getforexrate2.dto.resp.CurrencyRateResp;
import com.example.getforexrate2.service.ForexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ForexControllerTest {

    @Mock
    private ForexService forexService;

    @InjectMocks
    private ForexController forexController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRateRightNow_Success() {
        // 模擬 ForexService
        doNothing().when(forexService).saveForexData();

        // 執行測試方法
        ResponseEntity<String> response = forexController.getRateRightNow();

        // 驗證回應
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("外匯資料已成功獲取並存入資料庫。", response.getBody());
    }

    @Test
    void testGetCurrencyRate_Success() {
        // 模擬請求
        CurrencyRateReq request = new CurrencyRateReq();
        request.setStartDate("2025/02/01");
        request.setEndDate("2025/02/03");
        request.setCurrency("usd");

        // 模擬 ForexService 的回應
        CurrencyRateResp mockResponse = new CurrencyRateResp();
        mockResponse.setError(new CurrencyRateResp.ErrorMsg("0000", "成功"));

        when(forexService.getCurrencyRate(request)).thenReturn(mockResponse);

        // 執行測試方法
        ResponseEntity<CurrencyRateResp> response = forexController.getCurrencyRate(request);

        // 驗證回應
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("0000", response.getBody().getError().getCode());
        assertEquals("成功", response.getBody().getError().getMessage());
    }
}
