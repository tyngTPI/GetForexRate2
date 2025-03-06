<H1> Spring-boot 專案 ， 資料庫使用Mongo </H1>
<H4> 1.提供一批次每日18:00呼叫API(./forex/saveRateRightNow)，取得外匯成交資料，並將每日的美元/台幣欄位(USD/NTD)資料與
日期(yyyy-MM-dd HH:mm:ss) insert 至 table/collection </H4>
<H4> 2.提供一forex API(./forex/getCurrRate)，從DB取出日期區間內美元/台幣的歷史資料，並針對API功能寫Unit test。日期區間僅限1年前~當下日期-1天，
若日期區間不符規則，response 需回error code E001，一次僅查詢一種幣別，如：美元usd。 </H4> 
