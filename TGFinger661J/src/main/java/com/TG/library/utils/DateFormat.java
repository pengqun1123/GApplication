package com.TG.library.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author wangfeng
 * @desc 处理日期格式
 */
public class DateFormat {

    private static DateFormat INSTANCE;
    private SimpleDateFormat mSdf;

    private DateFormat() {
    }

    public static DateFormat getInstance() {
        if (INSTANCE == null) {
            synchronized (DateFormat.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DateFormat();
                }
            }
        }
        return INSTANCE;
    }

    public String getDateEN() throws Exception {
        if (mSdf != null)
            mSdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        else
            mSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return mSdf.format(System.currentTimeMillis());// 2012-10-03 23:41:31
    }

    /**
     * 将自定义字符串日期解析成毫秒数日期
     *
     * @param form 自定义格式的日期类型字符串
     * @param date 与自定义格式相符的日期字符串
     * @return long long型的日期毫秒数
     * @throws Exception
     */
    public long getMSDate(String form, String date) {
        if (null == date || "".equals(date))
            return 0;
        if (mSdf != null)
            mSdf.applyPattern(form);
        else
            mSdf = new SimpleDateFormat(form, Locale.ENGLISH);
        long MS = 0;
        try {
            MS = mSdf.parse(date).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return MS;
    }

    /**
     * 将日期毫秒数转换成自定义的字符串格式日期
     *
     * @param date 日期毫秒数
     * @param form 需要转换成的格式字符串
     * @return 自定义的字符串日期
     */
    public String getStrDate(long date, String form) {
        if (mSdf != null)
            mSdf.applyPattern(form);
        else
            mSdf = new SimpleDateFormat(form, Locale.ENGLISH);
        return mSdf.format(date);
    }

}   
