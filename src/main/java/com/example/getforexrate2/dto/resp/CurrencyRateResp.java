package com.example.getforexrate2.dto.resp;

import lombok.Data;
import java.util.List;

@Data
public class CurrencyRateResp {
    private ErrorMsg error; // 錯誤訊息
    private List<CurrencyRateData> currency; // 幣別資料

    @Data
    public static class ErrorMsg {
        private String code;    // 錯誤代碼
        private String message; // 錯誤訊息

        public ErrorMsg(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    @Data
    public static class CurrencyRateData {
        private String date; // 日期
        private String usd;  // 美元匯率
    }
}
