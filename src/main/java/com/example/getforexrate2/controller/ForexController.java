package com.example.getforexrate2.controller;

import com.example.getforexrate2.dto.req.CurrencyRateReq;
import com.example.getforexrate2.dto.resp.CurrencyRateResp;
import com.example.getforexrate2.service.ForexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forex")
@Slf4j
public class ForexController {

    @Autowired
    private ForexService forexService;

    /**
     * 觸發排程任務，立即執行外匯資料獲取和儲存
     */
    @PostMapping("/saveRateRightNow")
    public ResponseEntity<String> getRateRightNow() {
        try {

            forexService.saveForexData();
            log.info("API觸發外匯資料寫入成功");
            return ResponseEntity.ok("外匯資料已成功獲取並存入資料庫。");

        } catch (Exception e) {
            //擷取例外表示可能獲取外匯資料錯誤或寫入資料庫時發生錯誤
            log.error("API觸發外匯資料寫入時發生錯誤", e);
            return ResponseEntity.internalServerError().body("獲取外匯資料時發生錯誤：" + e.getMessage());
        }
    }

    /**
     * 依據請求參數，查詢指定日期區間的匯率資料
     */
    @PostMapping("/getCurrRate")
    public ResponseEntity<CurrencyRateResp> getCurrencyRate(@RequestBody CurrencyRateReq req) {
        CurrencyRateResp resp = forexService.getCurrencyRate(req);
        return ResponseEntity.ok(resp);
    }
}