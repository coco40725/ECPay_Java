## ECPay 透過Java API 實作信用卡一次付清


### 1. 取得 ECPay 所需要的檔案
github: https://github.com/ECPay/ECPayAIO_Java
下載該檔案，並將 ecpay這個資料夾放至 `src/main/java`。

### (或者) 使用我傳的 ECPayProject Marven
由於是用 Intellij 創立的，因此Eclipse的使用者可能直接把 src 裡面的資料放進去會比較不會有問題。
裡面已包含 ECPay 所需要的檔案與待會要使用的 Servlet (com/test/home/EcpayController.java)、test.jsp 與  payReturn.jsp。

### 2. 安裝 jar
請檢視你的專案是否需要安裝它的 jar 檔，此 API 僅需要 servlet, 與 log4j。( note: 我們的專案 pom.xml 已經包含了必須的 jar，直接用我們的pom.xml即可)

### 4. Servlet example
整體流程:
1. 我們可以從servlet得到前端傳來的資料 (可能用 form 或 ajax 傳來的等等)，包含 價格(TotalAmount)、商品名稱(ItemName) 等等。本次範例是直接在寫死在 servlet 中，可自行調整。

2. 將資料 set 到 `domain` 物件，注意! 這裡是最容易導致CheckMacValue error 的問題，務必將ECPay 要求的必填的欄位填好，參考: https://developers.ecpay.com.tw/?p=2866，
近期 github程式碼 有更新，因此有些必填欄位會被移到`xml`檔案。
另外，**目前不可以輸入中文字**，會導致 CheckMacValue error，目前還在研究中...

3. 檢查 paymanet_config，確認 `<HashKey>` 與 `<HashIV>` 值是否正確，查詢HashKey與 HashIV 請前往: 綠界廠商後台 (https://vendor-stage.ecpay.com.tw/User/LogOn_Step1) -系統開發管理-系統介接設定，查看你需要的 HashKey 與 HashIV。

```xml
<MerchantInfo>
        <MInfo name="Production_Account">
            <MerchantID>2000132</MerchantID>
            <HashKey>5294y06JbISpM5x9</HashKey>
            <HashIV>v77hoKGq4kWxNNIS</HashIV>
        </MInfo>
        <MInfo name="Stage_Account">
            <MerchantID>2000132</MerchantID>
            <HashKey>5294y06JbISpM5x9</HashKey>
            <HashIV>v77hoKGq4kWxNNIS</HashIV>
        </MInfo>
    </MerchantInfo>
```
4. 廠商建議EcpayPayment.xml 與 paymanet_config 放置於 `src/main/resources` ，Intellji 測試沒問題，Eclipse 可能要自行注意是否有讀不到的問題。

5. 測試用資料:
    - 信用卡卡號: 4311-9522-2222-2222
    - 安全碼: 222
    - 有效月年: MM/YY 值請大於現在當下時間的月年

6. Servlet 撰寫請看如下: 
(注意! OrderResultURL 你需要重新寫URL，`http://localhost:8081/payReturn.jsp` 是我的環境下，payReturn.jsp 的路徑，請更改為你自己的路徑。)

```java
@WebServlet("/ECpay")
public class EcpayController extends HttpServlet {
    public static AllInOne domain;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 根據表單建立收款連結 (中文編碼有問題)
        domain = new AllInOne("");
        AioCheckOutOneTime obj = new AioCheckOutOneTime();

        // 從 view 獲得資料，依照 https://developers.ecpay.com.tw/?p=2866 獲得必要的參數
        // MerchantTradeNo  : 必填 特店訂單編號 (不可重複，因此需要動態產生)
        obj.setMerchantTradeNo(new String("salon" + System.currentTimeMillis()));
        // MerchantTradeDate  : 必填 特店交易時間 yyyy/MM/dd HH:mm:ss
        obj.setMerchantTradeDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date()));
        // TotalAmount  : 必填 交易金額
        obj.setTotalAmount("1900");
        // TradeDesc  : 必填 交易描述
        obj.setTradeDesc("Thank you");
        // ItemName  : 必填 商品名稱
        obj.setItemName("Salon Service");
        // ReturnURL   : 必填  我用不到所以是隨便填一個英文字
        obj.setReturnURL("a");
        // OrderResultURL   : 選填 消費者完成付費後。重新導向的位置
        obj.setOrderResultURL("http://localhost:8081/payReturn.jsp");
        obj.setNeedExtraPaidInfo("N");


        // 回傳form訂單 並自動將使用者導到 綠界
        String form = domain.aioCheckOut(obj, null);
        System.out.println(form);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print("<html><body>" + form + "</body></html>");
    }
}

```

7. 打開 tomcat，開啟 test.jsp。

8. 點選按鈕 **綠界線上支付** ， 再次提醒，test.jsp 框中的文字與數值僅供參考，本範例的 Servlet 並沒有使用前端傳來數值。

9. 成功的話，會進入綠界付費頁面。

10. 填寫測試用信用卡卡號安全碼等等。

11. 手機請輸入正確，因為會傳驗證碼。

12. 送出的按鈕要按兩次，第一次會跳出提醒告知這是測試，關閉後，再按一次即可傳出。

13. 付費成功後，會跳轉到 OrderResultURL 所設定的位置。

14. 去綠界後台檢查是否有該筆資料 (https://vendor-stage.ecpay.com.tw/User/LogOn_Step1): 信用卡收單 - 交易明細 - 查詢

