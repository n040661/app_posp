package xdt.dto.yf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
/**
 *  唯一序列
 * @author rg.zhao
 *
 */
public class UniqueSnUtil {
	
	private static int curRandNumOffset = 0;
	
	private static Random randGenerator = new Random();
	
	public static synchronized String getUniqueSn()
	  {
	    long t = System.currentTimeMillis();
	    int randSep = randGenerator.nextInt(99);
	    long sn = getUniqueSn(new Date(t), randSep);
	    return String.valueOf(sn);
	  }
	
	private static synchronized long getUniqueSn(Date time, int randOffsetSepNum)
	  {
	    Date curTime = time;
	    if (time == null) curTime = new Date(System.currentTimeMillis());

	    int randSep = randOffsetSepNum;
	    if ((randSep < 1) || (randSep >= 100)) {
	      randSep = randGenerator.nextInt(99);
	    }

	    String timeStr = getStdTimeString(curTime, "yyMMddHHmmssSSS");

	    long randSn = String_ConvertToLong(timeStr, System.currentTimeMillis()) / 10L;

	    randSn = randSn * 100L + randSep;

	    randSn *= 1000L;
	    randSn += getRandOffset();

	    return randSn;
	  }
	
	public static synchronized int getRandOffset()
	  {
	    curRandNumOffset = (curRandNumOffset + 1) % 1000;

	    return curRandNumOffset;
	  }
	
	 public static long String_ConvertToLong(String longString, long defaultExceptionValue)
	  {
	    long num = 0L;
	    if (longString != null)
	    {
	      try
	      {
	        num = Long.parseLong(longString.trim());
	      }
	      catch (Exception ex)
	      {
	        num = defaultExceptionValue;
	      }

	    }

	    return num;
	  }
	
	public static String getStdTimeString(Date cTime, boolean hasSeperator)
	  {
	    if (cTime == null) return "";
	    DateFormat format;
	    if (hasSeperator)
	      format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    else {
	      format = new SimpleDateFormat("yyyyMMddHHmmss");
	    }

	    return format.format(cTime);
	  }

	  public static String getStdTimeString(long tTime, boolean hasSeperator)
	  {
	    Date curDate = new Date(tTime);
	    return getStdTimeString(curDate, hasSeperator);
	  }

	  public static String getStdTimeString(Date cTime, String dateFormat)
	  {
	    if (cTime == null) return "";

	    DateFormat format = new SimpleDateFormat(dateFormat);
	    return format.format(cTime);
	  }

	  public static String getStdTimeString()
	  {
	    long t = System.currentTimeMillis();

	    return getStdTimeString(t, true);
	  }

}
