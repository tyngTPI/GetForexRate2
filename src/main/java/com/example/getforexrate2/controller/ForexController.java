package com.example.getforexrate2.controller;

import com.example.getforexrate2.dto.req.CurrencyRateReq;
import com.example.getforexrate2.dto.resp.CurrencyRateResp;
import com.example.getforexrate2.service.ForexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/forex")
public class ForexController {

    private static final Logger logger = LoggerFactory.getLogger(ForexController.class);

    @Autowired
    private ForexService forexService;

    @PostMapping("/saveRateRightNow")
    public ResponseEntity<String> getRateRightNow() {
        try {

            forexService.saveForexData();
            logger.info("API觸發外匯資料寫入成功");
            return ResponseEntity.ok("外匯資料已成功獲取並存入資料庫。");

        } catch (Exception e) {
            logger.error("API觸發外匯資料寫入時發生錯誤", e);
            return ResponseEntity.internalServerError().body("獲取外匯資料時發生錯誤：" + e.getMessage());
        }
    }

    @PostMapping("/getCurrRate")
    public ResponseEntity<CurrencyRateResp> getCurrencyRate(@RequestBody CurrencyRateReq req) {
        CurrencyRateResp resp = forexService.getCurrencyRate(req);
        return ResponseEntity.ok(resp);
    }
}