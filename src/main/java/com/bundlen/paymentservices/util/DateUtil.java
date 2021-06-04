package com.bundlen.paymentservices.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static Date nextDate(Date currentDate, int numberOfDays) {
        Calendar seekedDate = Calendar.getInstance();
        seekedDate.setTime(currentDate);
        seekedDate.set(Calendar.DAY_OF_MONTH, seekedDate.get(Calendar.DAY_OF_MONTH)+numberOfDays);
        return seekedDate.getTime();
    }
}
