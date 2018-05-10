package xdt.util.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

/**　格式转换工具类
 * 
 * @author XieminQuan
 */
public class Formatter {

    public static void main(String[] args) {

        Calendar c = Calendar.getInstance();
        c.set(2009, 12, 31);
        Date d = Formatter.formatSettlementDate(c.getTime(), "0101");
    }
    private static SimpleDateFormat yy = null;
    private static SimpleDateFormat yyyy_MM_dd_HH_mm_ss = null;
    private static SimpleDateFormat yyMMddHHmmss = null;
    private static SimpleDateFormat yyyyMMdd = null;
    private static SimpleDateFormat yyyy_MM_dd = null;
    private static SimpleDateFormat china_yyyy_MM_dd_HH_mm_ss = null;
    private static SimpleDateFormat HH_mm_ss_SSS = null;
    private static SimpleDateFormat HH_mm_ss = null;
    private static SimpleDateFormat yyyyMMddHHmmss = null;
    private static SimpleDateFormat MMddHHmmss = null;
    private static SimpleDateFormat yyMMdd = null;
    private static SimpleDateFormat yyyy = null;
    private static SimpleDateFormat MMdd = null;
    private static NumberFormat amountFormatter = null;
    private static NumberFormat numberFormatter = null;

    public static String MMddHHmmss(Date date) {
        if (MMddHHmmss == null) {
            MMddHHmmss = new SimpleDateFormat("MMddHHmmss");
        }
        return MMddHHmmss.format(date);
    }

    public static String MMdd(Date date) {
        if (MMdd == null) {
            MMdd = new SimpleDateFormat("MMdd");
        }
        return MMdd.format(date);
    }

    public static String yyyy(Date date) {
        if (yyyy == null) {
            yyyy = new SimpleDateFormat("yyyy");
        }
        return yyyy.format(date);
    }

    public static String yyyy_MM_dd(Date date) {
        if (yyyy_MM_dd == null) {
            yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");
        }
        return yyyy_MM_dd.format(date);
    }

    public static String yyMMdd(Date date) {
        if (yyMMdd == null) {
            yyMMdd = new SimpleDateFormat("yyMMdd");
        }
        return yyMMdd.format(date);
    }

    public static String yy(Date year) {
        if (yy == null) {
            yy = new SimpleDateFormat("yy");
        }
        return yy.format(year);
    }

    public static Date formatSettlementDate(Date transDate, String settleDate) {
        int month = Integer.parseInt(settleDate.substring(0, 2));
        int day = Integer.parseInt(settleDate.substring(2));
        String strTmp = Formatter.yyyyMMdd(transDate);
        int year = Integer.parseInt(strTmp.substring(0, 4));
        int month2 = Integer.parseInt(strTmp.substring(4, 6));

        Calendar c = Calendar.getInstance();
        if (month2 == 12 && month == 1) {
            year++;
        }
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String formatTime1(Date time) {
        if (yyyy_MM_dd_HH_mm_ss == null) {
            yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return yyyy_MM_dd_HH_mm_ss.format(time);
    }

    public static String yyyy_MM_dd_HH_mm_ss(Date time) {
        if (yyyy_MM_dd_HH_mm_ss == null) {
            yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return yyyy_MM_dd_HH_mm_ss.format(time);
    }

    public static String yyMMddHHmmss(Date time) {
        if (yyMMddHHmmss == null) {
            yyMMddHHmmss = new SimpleDateFormat("yyMMddHHmmss");
        }
        return yyMMddHHmmss.format(time);
    }

    public static String yyyyMMdd(Date time) {
        if (yyyyMMdd == null) {
            yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
        }
        return yyyyMMdd.format(time);
    }

    public static String china_yyyy_MM_dd_HH_mm_ss(Date time) {
        if (china_yyyy_MM_dd_HH_mm_ss == null) {
            china_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        }
        return china_yyyy_MM_dd_HH_mm_ss.format(time);
    }

    public static String HH_mm_ss_SSS(Date time) {
        if (HH_mm_ss_SSS == null) {
            HH_mm_ss_SSS = new SimpleDateFormat("HH:mm:ss.SSS");
        }
        return HH_mm_ss_SSS.format(time);
    }

    public static String HHmmss(Date time) {
        if (HH_mm_ss == null) {
            HH_mm_ss = new SimpleDateFormat("HHmmss");
        }
        return HH_mm_ss.format(time);
    }

    public static String yyyyMMddHHmmss(Date time) {
        if (yyyyMMddHHmmss == null) {
            yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
        }
        return yyyyMMddHHmmss.format(time);
    }
      
    public static String formatAmount(Double number) {
        if (amountFormatter == null) {
            amountFormatter = NumberFormat.getInstance();
            amountFormatter.setMaximumFractionDigits(2);
            amountFormatter.setMaximumIntegerDigits(12);
            amountFormatter.setGroupingUsed(false);
        }
        return amountFormatter.format(number);
    }

    public static String formatAmount(Double number, int fractionDigits, int integerDigits) {
        if (amountFormatter == null) {
            amountFormatter = NumberFormat.getInstance();
            amountFormatter.setMaximumFractionDigits(fractionDigits);
            amountFormatter.setMaximumIntegerDigits(integerDigits);
            amountFormatter.setGroupingUsed(false);
        }
        return amountFormatter.format(number);
    }

    public static String formatAmount(Float number, int fractionDigits, int integerDigits) {
        if (amountFormatter == null) {
            amountFormatter = NumberFormat.getInstance();
            amountFormatter.setMaximumFractionDigits(fractionDigits);
            amountFormatter.setMaximumIntegerDigits(integerDigits);
            amountFormatter.setGroupingUsed(false);
        }
        return amountFormatter.format(number);
    }

    public static String formatNumber(Double number) {
        if (numberFormatter == null) {
            numberFormatter = NumberFormat.getInstance();
            numberFormatter.setGroupingUsed(false);
        }
        return numberFormatter.format(number);
    }

    public static String formatString(String str, int beginSize, int endSize, String leftFill, String rightFill) throws Exception {

        while (beginSize > 0) {
            str = leftFill + str;
            beginSize--;
        }
        if (str.getBytes("gbk").length > endSize) {
            byte[] temp = str.getBytes();
            byte[] newbyte = new byte[endSize];
            for (int i = 0; i < newbyte.length; i++) {
                newbyte[i] = temp[i];
            }
            str = new String(newbyte);
        }

        while (str.getBytes("gbk").length < endSize) {
            str += rightFill;
        }
        return str;
    }

    public static String formatString(String str, int endSize) {
        try {
            return formatString(str, 0, endSize, " ", " ");
        } catch (Exception e) {
            return "格式化字符串出现异常:" + e;
        }
    }

    public static String formatBytes(byte[] bts) {
        String tmp = "";
        for (byte b : bts) {
            tmp += " " + b;
        }
        return tmp;
    }

    public static byte[] base64Decode(String data) throws IOException {
        if(Strings.isNullOrEmpty(data))
            return null;
        return new sun.misc.BASE64Decoder().decodeBuffer(data);
    }

    public static String base64Encode(byte[] data) throws IOException {
        if(data == null)
            return "";
        return new sun.misc.BASE64Encoder().encode(data);
    }
}
