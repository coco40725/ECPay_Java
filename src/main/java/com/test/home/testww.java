package com.test.home;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * @author Yu-Jing
 * @create 2023/1/14 上午 09:49
 */
public class testww {
    public static void main(String[] args) {
        System.out.println(new String("salon" + System.currentTimeMillis()));
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date());
        System.out.println(timeStamp);
    }
}
