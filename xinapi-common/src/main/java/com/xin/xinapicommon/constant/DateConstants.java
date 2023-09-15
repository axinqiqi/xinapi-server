package com.xin.xinapicommon.constant;

/**
 * @Description DateConstants
 * @Author sjl
 * @Date 2022/6/13 11:16
 * @Version 1.0
 **/
public interface DateConstants {

    String TIME_ZONE_GMT8 = "GMT+8";
    String DATE_PATTERN = "yyyy-MM-dd";
    String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    String SECONDS_PATTERN = "yyyyMMddHHmmss";
    String MILLIS_PATTERN = "yyyyMMddHHmmssSSS";

    String DATE_REGEXP = "\\\\d{4}-\\d{\\1-\\12}-\\d{\\1-\\31}?";

}
