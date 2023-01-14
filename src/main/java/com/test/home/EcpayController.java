package com.test.home;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutOneTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author Yu-Jing
 * @create 2023/1/13 上午 11:44
 */

@WebServlet("/ECpay")
public class EcpayController extends HttpServlet {
    public static AllInOne domain;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 根據表單建立收款連結 (中文編碼有問題)
        // 使用者跳轉至綠界的交易流程網站
        // 按照流程輸入卡號..... (中文編碼!)
            // 測試卡號: 一般信用卡測試卡號 : 4311-9522-2222-2222 安全碼 : 222
            // 信用卡測試有效月/年：輸入的 MM/YYYY 值請大於現在當下時間的月年，
            // 例如在 2016/04/20 當天作測試，請設定 05/2016(含)之後的有效月年，否則回應刷卡失敗。
            // 手機請輸入正確，因為會傳驗證碼
        // 檢查後台: 信用卡收單 - 交易明細 - 查詢
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
