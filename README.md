<H1> 需求建置Spring-boot 專案 ， 資料庫使用Mongo </H1>
<H4> 1.提供一批次每日18:00呼叫API。 </H4>
<H4> 2.提供一forex API(./forex/getCurrRate)，從DB取出日期區間內美元/台幣的歷史資料。 </H4> 

---
[_單元測試與實際測試報告_](https://docs.google.com/document/d/1aK-UXU6YDLdi8GJCtX6O3qGS7acj_5SGtWZTz8ucmnQ/edit?usp=sharing)
- 單元測試
  1. 預期結果(外匯API正常且存取正常、Forex API正常收發)
  2. 非預期結果(外匯API有誤或無回應、Forex API傳入request內容不符)
- 加上log以利檢視程式內容
- 定義Error Code

| code | message |
|------|---------|
| E001 | 日期區間不符 |
| E002 | 日期格式不符 |
| E003 | 僅可查詢美元 (USD) |


\
[_Code Review對談內容_](https://docs.google.com/document/d/1Ey7pXwDYz56bWWjeX6FlroPb2QnEo-kRn7sjyuL8_J0/edit?usp=sharing)\
此專案已自行以 ****Google Gemini**** 進行Code review<br/>
總體來說，程式碼具有良好的程式碼風格、合理的程式碼結構和較高的程式碼品質。
