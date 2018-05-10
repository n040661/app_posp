package xdt.baidu;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import xdt.model.BaiduSdkOrder;
import xdt.model.PmsAppTransInfo;
import xdt.model.ViewKyChannelInfo;
import xdt.service.impl.BaseServiceImpl;
import xdt.servlet.AppPospContext;
import xdt.util.UtilDate;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 公共方法             百度
 * 生成百付宝即时到账支付接口对应的URL
 * 生成签名字符串
 */
public class BDUtil<T> extends BaseServiceImpl{

    private static   Logger logger=Logger.getLogger(BDUtil.class);
	/**
	 * 生成百付宝即时到账支付接口对应的URL
	 *
	 * @param ary $params	生成订单的参数数组，具体参数的取值参见接口文档
	 * @param ary1 $url   百付宝即时到账支付接口URL
	 * @param oAgentNo 
	 * @return url 返回生成的百付宝即时到账支付接口URL
	 * @throws UnsupportedEncodingException 
	 */
	public String   create_baifubao_pay_order_url(String[] ary,String[] ary1,String url, String oAgentNo) throws UnsupportedEncodingException
	{
		//首先判断传入的必选参数是否为空
		//调用make_sign方法，返回签名结果sign，给参数排序和签名 
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ary1.length; i++)
		{ 
			//判断属性的值是否为空,值为空的不用参与拼接
			String strTemp=ary1[i].substring(ary1[i].indexOf("=")+1, ary1[i].length());
			if(!strTemp.equals("") && strTemp!=null)
			{
               sb. append(ary1[i]+"&");
            }	
		} 
		String newStr =sb.toString();
        String sign=make_sign(ary,oAgentNo);
        //组 URL get串提交串参数  //提交
        String params=url+"?"+newStr+"sign="+sign;
		return  params;
	}
	/**
	 * 计算数组的签名，传入参数为数组，算法如下：
	 * 1.
	 * 对数组按KEY进行升序排序
	 * 2. 在排序后的数组中添加商户密钥，键名为key，键值为商户密钥
	 * 3. 将数组拼接成字符串，以key=value&key=value的形式进行拼接，注意这里不能直接调用
	 * http_build_query方法，因为该方法会对参数进行URL编码
	 * 4. 要所传入数组中的$params ['sign_method']定义的加密算法，对拼接好的字符串进行加密，生成的便是签名。
	 * $params ['sign_method']等于1使用md5加密，等于2使用sha-1加密
	 *
	 * @param ary $params 生成签名的数组
	 * @return string | boolean 成功返回生成签名，失败返回false
	 */
	public String   make_sign(String[] ary,String oAgentNo){
		Arrays.sort(ary,String.CASE_INSENSITIVE_ORDER);   
		//对参数数组进行按key升序排列,然后拼接，最后调用5签名方法
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < ary.length; i++){  
				sb. append(ary[i]+"&");	
		}
		ViewKyChannelInfo channelInfo = AppPospContext.context.get(BAIDU+BAIDUKEY);
		String newStrTemp = sb.toString()+"key="+channelInfo.getChannelPwd().trim();
		//获取sign_method
		String signmethod= GetMethodSign(newStrTemp);
		//根据sign_method选择使用MD5签名1，还是哈希签名2
		String sign=null;
		if(signmethod.equals("1")){
		  sign = BDMD5.md5Digest(newStrTemp); 
		}else if(signmethod.equals("2")){
	      sign =new BDSHA1().Digest(newStrTemp,"gbk").toLowerCase();
		}
		logger.info("str待签名串: " + newStrTemp + ";签名串 sign=" + sign);
		return sign;
	}
	

	
	/**
	 * 从待签名字符串中拿出签名类型
	 * @return string MD5签名1，哈希签名2
	 */
	private String GetMethodSign(String  sb){	
		int aa=sb.indexOf("sign_method=");
	    String signmethod= sb.substring(aa+12,aa+13);
		return signmethod;
	}
	
	/**
	 * 字符串编码 gbk
	 * @param  str 待转码字符串
	 * @return result 转码后的字符串
	 */
	public static String encoder(String str){
		String result =null;
		try {
		    result = new String(str.toString().getBytes("gbk"),"gbk");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		return result;
	}


    /**
     * 将传入对象的所有非空字段组成数据（key，value）
     * @author Jeff
     * @param t
     * @param filterNullFlag 是否过滤空值  1为过滤  其他为不过率
     * @return
     */
    public String [] getClassFieldsArry(T t,Integer filterNullFlag){

        Field[] files = t.getClass().getDeclaredFields();
        List<String> args = new ArrayList<String>();
        for(Field f : files){
            f.setAccessible(true);
            try {
                Object val  = f.get(t);

                if(filterNullFlag != null && filterNullFlag == 1){//过滤空值

                    if(val != null && StringUtils.isNotBlank(val.toString())){
                        if(!f.getName().equals("sign")){  //sign不做签名处理
                            String arg = f.getName()+ "=" + val.toString();
                            args.add(arg);
                        }
                    }
                }else{

                        if(!f.getName().equals("sign")){  //sign不做签名处理
                            String value = "";
                            if(val != null ){
                                value =   val.toString();
                            }
                            String arg = f.getName()+ "=" + value;
                            args.add(arg);

                        }

                }




            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        String [] a  = null;
        if(args.size() > 0){
           a = args.toArray(new String[args.size()]);
        }
        return a;
    }



    /**
     * 根据订单，生成百度SDK调用串
     * @author Jeff
     * @param appTransInfo
     * @return
     */
    public static String generalBDSDKCallStr(PmsAppTransInfo appTransInfo) {
        String result = "";
        if(appTransInfo != null && StringUtils.isNotBlank(appTransInfo.getOrderid()) && StringUtils.isNumeric(appTransInfo.getOrderamount())){

            BaiduSdkOrder sdkOrder = new BaiduSdkOrder();
            sdkOrder.setService_code(1);
            ViewKyChannelInfo channelInfo = AppPospContext.context.get( BAIDU+BAIDUCALLBACKURL);
            sdkOrder.setSp_no(channelInfo.getChannelNO());
            sdkOrder.setOrder_create_time(UtilDate.getOrderNum());
            sdkOrder.setOrder_no(appTransInfo.getOrderid());
            sdkOrder.setGoods_desc(appTransInfo.getTradetype());
            sdkOrder.setGoods_name(appTransInfo.getTradetype());
            sdkOrder.setCurrency(1);
            sdkOrder.setTotal_amount((int)Math.ceil(Double.parseDouble(appTransInfo.getFactamount())));
            sdkOrder.setReturn_url(channelInfo.getCallbackurl());
            sdkOrder.setPay_type(2);
            sdkOrder.setInput_charset("1");
            sdkOrder.setVersion("2");
            sdkOrder.setSign_method("1");

            BDUtil<BaiduSdkOrder> bdUtil = new BDUtil<BaiduSdkOrder>();

            String [] signArray = bdUtil.getClassFieldsArry(sdkOrder,1); //生成签名
            try {
                sdkOrder.setGoods_desc(URLEncoder.encode(sdkOrder.getGoods_desc(), "gbk"));
                sdkOrder.setGoods_name(URLEncoder.encode(sdkOrder.getGoods_name(),"gbk"));
            } catch (UnsupportedEncodingException e) {
                logger.info("生成百度SDK调用串失败， 订单号："+appTransInfo.getOrderid() +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+e.getMessage());
                e.printStackTrace();
            }

            String [] signArray1 = bdUtil.getClassFieldsArry(sdkOrder,1); //用于传递参数
            try {
                result = bdUtil.create_baifubao_pay_order_url(signArray,signArray1, "",appTransInfo.getoAgentNo());
            } catch (UnsupportedEncodingException e) {
                logger.info("生成百度SDK调用串失败， 订单号："+appTransInfo.getOrderid() +"，结束时间："+ UtilDate.getDateFormatter()+"。详细信息："+e.getMessage());
                e.printStackTrace();
            }

            if(StringUtils.isNotBlank(result) && result.charAt(0) == '?'){
                result = result.replaceFirst("\\?","");
            }
            logger.info("生成百度SDK调用串成功， 订单号："+appTransInfo.getOrderid() +"，结束时间："+ UtilDate.getDateFormatter()+",串："+result);
        }
        return result;
    }
    

}
