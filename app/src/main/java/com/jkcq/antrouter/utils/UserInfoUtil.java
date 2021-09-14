package com.jkcq.antrouter.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by peng on 2018/5/9.
 */

public class UserInfoUtil {

    public static int parseAge(String birthday, long systemTime) throws ParseException {
        Long l = Long.parseLong(birthday);
        Date date = new Date();
        date.setTime(l);


        Date currentDate = new Date();
        if (systemTime == 0) {
            currentDate.setTime(System.currentTimeMillis());
        } else {
            currentDate.setTime(systemTime);
        }

        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        Calendar currentcalendar = Calendar.getInstance();
        currentcalendar.setTime(currentDate);
        return currentcalendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
    }
}
