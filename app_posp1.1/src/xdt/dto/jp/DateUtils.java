package xdt.dto.jp;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/28.
 *
 */
public class DateUtils {
    static final double DAY_MILLIS = 8.64E7D;
    static final String standardPtn = "yyyy-MM-dd HH:mm:ss";//阮鑫
    static final String standardSPtn = "yyyy-MM-dd HH:mm:ss.SSS";
    static final String yy_MM_ddPtn = "yyyy-MM-dd";
    static final String HH_mm_ssPtn = "HH:mm:ss";
    static final String sdf_ymdPtn = "yyyy年MM月dd日";
    static final String ymPtn = "yyyyMM";
    static final String ymdPtn = "yyyyMMdd";
    static final String hmsPtn = "HHmmss";
    static final String ymdhmsPtn = "yyyyMMddHHmmss";
    static final String ymdhmsSPtn = "yyyyMMddHHmmssSSS";
    public static final SimpleFastDateFormat standard = SimpleFastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    public static final SimpleFastDateFormat standardS = SimpleFastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS");
    public static final SimpleFastDateFormat yy_MM_dd = SimpleFastDateFormat.getInstance("yyyy-MM-dd");
    public static final SimpleFastDateFormat HH_mm_ss = SimpleFastDateFormat.getInstance("HH:mm:ss");
    public static final SimpleFastDateFormat sdf_ymd = SimpleFastDateFormat.getInstance("yyyy年MM月dd日");
    public static final SimpleFastDateFormat ym = SimpleFastDateFormat.getInstance("yyyyMM");
    public static final SimpleFastDateFormat ymd = SimpleFastDateFormat.getInstance("yyyyMMdd");
    public static final SimpleFastDateFormat hms = SimpleFastDateFormat.getInstance("HHmmss");
    public static final SimpleFastDateFormat ymdhms = SimpleFastDateFormat.getInstance("yyyyMMddHHmmss");
    public static final SimpleFastDateFormat ymdhmsS = SimpleFastDateFormat.getInstance("yyyyMMddHHmmssSSS");

    public DateUtils() {
    }

    public static Date str2Date(String dateStr, String pattern) {
        if(!StringUtils.isBlank(dateStr) && !StringUtils.isBlank(pattern)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                return simpleDateFormat.parse(dateStr);
            } catch (ParseException var3) {
                throw new IllegalArgumentException(var3);
            }
        } else {
            throw new IllegalArgumentException("dateStr and pattern must not be null!");
        }
    }

    public static String date2Str(Date date, SimpleFastDateFormat date_sdf) {
        return null == date?null:date_sdf.format(date);
    }

    public static long getMillis() {
        return System.currentTimeMillis();
    }

    public static String formatDate() {
        return standard.format(System.currentTimeMillis());
    }

    public static String formatDate(Date date) {
        return standard.format(date);
    }

    public static String formatDate(long time) {
        return standard.format(time);
    }

    public static String formatDateSSS() {
        return standardS.format(System.currentTimeMillis());
    }

    public static String formatDateSSS(Date date) {
        return standardS.format(date);
    }

    public static String formatDateSSS(long timeMillis) {
        return standardS.format(timeMillis);
    }

    public static String formatYYYYMMdd() {
        return ymd.format(System.currentTimeMillis());
    }

    public static String formatYYYYMMdd(Date date) {
        return ymd.format(date);
    }

    public static String formatYYYYMMdd(long timeMillis) {
        return ymd.format(timeMillis);
    }

    public static String formatYY_MM_DD() {
        return yy_MM_dd.format(System.currentTimeMillis());
    }

    public static String formatYY_MM_DD(long timeMillis) {
        return yy_MM_dd.format(timeMillis);
    }

    public static String formatYY_MM_DD(Date date) {
        return yy_MM_dd.format(date);
    }

    public static String formatHH_mm_ss() {
        return HH_mm_ss.format(System.currentTimeMillis());
    }

    public static String formatHH_mm_ss(long timeMillis) {
        return HH_mm_ss.format(timeMillis);
    }

    public static String formatHH_mm_ss(Date date) {
        return HH_mm_ss.format(date);
    }

    public static String formatSdf_ymd() {
        return sdf_ymd.format(System.currentTimeMillis());
    }

    public static String formatSdf_ymd(long timeMillis) {
        return sdf_ymd.format(timeMillis);
    }

    public static String formatSdf_ymd(Date date) {
        return sdf_ymd.format(date);
    }

    public static String formatHHmmss() {
        return hms.format(System.currentTimeMillis());
    }

    public static String formatHHmmss(long timeMillis) {
        return hms.format(timeMillis);
    }

    public static String formatHHmmss(Date date) {
        return hms.format(date);
    }

    public static String formatYYYYMM() {
        return ym.format(System.currentTimeMillis());
    }

    public static String formatYYYYMM(long timeMillis) {
        return ym.format(timeMillis);
    }

    public static String formatYYYYMM(Date date) {
        return ym.format(date);
    }

    public static String formatYYYYMMDDHHMMSS() {
        return ymdhms.format(System.currentTimeMillis());
    }

    public static String formatYYYYMMDDHHMMSS(long timeMillis) {
        return ymdhms.format(timeMillis);
    }

    public static String formatYYYYMMDDHHMMSS(Date date) {
        return ymdhms.format(date);
    }

    public static String formatYYYYMMddHHmmssSSS() {
        return ymdhmsS.format(System.currentTimeMillis());
    }

    public static String formatYYYYMMddHHmmssSSS(long timeMillis) {
        return ymdhmsS.format(timeMillis);
    }

    public static String formatYYYYMMddHHmmssSSS(Date date) {
        return ymdhmsS.format(date);
    }

    public static String getTime(long unixTimestamp, SimpleFastDateFormat formatter) {
        return formatter.format(unixTimestamp * 1000L);
    }

    public static Long getCurrentMonthZeroTimestamp() {
        Calendar cale = Calendar.getInstance();
        cale.add(2, 0);
        cale.set(5, 1);
        cale.set(11, 0);
        cale.set(12, 0);
        cale.set(13, 0);
        cale.set(14, 0);
        return Long.valueOf(cale.getTime().getTime() / 1000L);
    }

    public static Long getNextMonthZeroTimestamp() {
        Calendar cale = Calendar.getInstance();
        cale.add(2, 1);
        cale.set(5, 1);
        cale.set(11, 0);
        cale.set(12, 0);
        cale.set(13, 0);
        cale.set(14, 0);
        return Long.valueOf(cale.getTime().getTime() / 1000L);
    }

    public static double getDateDiff(long beginTime, long endTime) {
        return Double.valueOf((double)(endTime - beginTime)).doubleValue() / 8.64E7D;
    }

    public static Date getBeforeDay(int day) {
        Calendar cale = Calendar.getInstance();
        cale.set(2, 0);
        cale.add(5, 0 - day);
        cale.set(11, 0);
        cale.set(12, 0);
        cale.set(13, 0);
        cale.set(14, 0);
        return cale.getTime();
    }
}

