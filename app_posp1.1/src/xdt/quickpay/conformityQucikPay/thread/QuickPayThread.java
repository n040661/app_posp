package xdt.quickpay.conformityQucikPay.thread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xdt.quickpay.hengfeng.util.HttpClientUtil;

public class QuickPayThread
  extends Thread
{
  private Logger logger = LoggerFactory.getLogger(getClass());
  private String param;
  private String url;
  
  public QuickPayThread(String url, String param)
  {
    this.param = param;
    this.url = url;
  }
  
  public void run()
  {
    try
    {
      sleep(2000L);
      for (int i = 0; i < 10; i++)
      {
        String result = HttpClientUtil.post(this.url, this.param);
        this.logger.info("进入线程后下游第" + i + "次返回状态" + result);
        JSONObject ob = JSONObject.fromObject(result);
        Iterator it = ob.keys();
        Map<String, String> map = new HashMap();
        while (it.hasNext())
        {
          String keys = (String)it.next();
          if (keys.equals("success"))
          {
            String value = ob.getString(keys);
            this.logger.info("进入线程后解析下游返回的结果:\t" + value);
            map.put("success", value);
            break;
          }
        }
        sleep(5000L);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
