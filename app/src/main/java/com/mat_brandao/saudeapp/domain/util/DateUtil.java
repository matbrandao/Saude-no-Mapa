package com.mat_brandao.saudeapp.domain.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * Created by Mateus Brandão on 20/09/2016.
 */

public class DateUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

    public static String getNowDate() {
        Timber.i("timeString = " + simpleDateFormat.format(new Date()));
        return simpleDateFormat.format(new Date());
    }

    public static String getDate(long longDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
        Timber.i("timeString = " + simpleDateFormat.format(new Date(longDate)));
        return simpleDateFormat.format(new Date(longDate));
    }

    public static String getFormattedDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        SimpleDateFormat oldSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'BRST'");
        SimpleDateFormat old2SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'BRT'");
        try {
            return simpleDateFormat.format(oldSimpleDateFormat.parse(date));
        } catch (ParseException e) {
            try {
                return simpleDateFormat.format(old2SimpleDateFormat.parse(date));
            } catch (Exception e1) {}
            e.printStackTrace();
        }
        return date;
    }
}
