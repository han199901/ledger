package com.han.zhangben.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormat {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");

    public static String date2Str(Date date) {
        return simpleDateFormat.format(date);
    }

}
