package com.vike.agent.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/10/25
 */
public class OrderHelp {

    private static long CURRENT_TIME_MILLIS = System.currentTimeMillis();

    private static int COUNT = 1;

    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * 当前系统使用1位标识顺序，支持10000的并发，取消原4位顺序标识位
     * private static final DecimalFormat df = new DecimalFormat("0000");
     * */


    public synchronized static String createOrderNo(){

        long currentTimeMillis = System.currentTimeMillis();

        if(currentTimeMillis == CURRENT_TIME_MILLIS){
            COUNT++;
        }else{
            CURRENT_TIME_MILLIS = currentTimeMillis;
            COUNT = 1;
        }

        return sd.format(new Date(currentTimeMillis))+COUNT;
    }

    public static void main(String [] args){
        long b = System.currentTimeMillis();
        System.out.println(b);
        for(int i=0;i<10000;i++){
            System.out.println(createOrderNo());
        }
        long e = System.currentTimeMillis();
        System.out.println(e);
        System.out.println("费时："+(e-b));

    }
}
