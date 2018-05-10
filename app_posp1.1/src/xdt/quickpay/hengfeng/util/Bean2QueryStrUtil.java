package xdt.quickpay.hengfeng.util;

import java.lang.reflect.Field;
import java.util.UUID;

import xdt.quickpay.hengfeng.comm.Constant;
import xdt.quickpay.hengfeng.entity.PayRequestEntity;
import xdt.util.UtilDate;

/**
 * @ClassName: Bean2QueryStrUtil
 * @Description: Bean转查询串数据
 * @author LiShiwen
 * @date 2016年6月20日 下午5:40:36
 *
 */
public class Bean2QueryStrUtil{
	
	/**
	 * bean 转查询串
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public  String bean2QueryStr(Object obj) throws IllegalArgumentException, IllegalAccessException{
		StringBuffer sb=new StringBuffer();
		if(obj==null){
			return null;
		}
		
		System.out.println(obj);
		
		Class clazz=obj.getClass();
		System.out.println(obj.getClass().getName());
		Field[] fields=clazz.getDeclaredFields();
		System.out.println("字段个数："+fields.length);
		for (Field field : fields) {
			field.setAccessible(true);
			if(field.get(obj)==null){
				sb.append(field.getName()+"=&");
			}else{
				sb.append(field.getName()+"="+field.get(obj)+"&");
			}
			
		}
		return sb.substring(0, sb.toString().length()-1);
	}
	
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		PayRequestEntity param = new PayRequestEntity();

		param.setPageurl("http://389f63c0.ngrok.natapp.cn/app_posp/hf/pagePayResult.action");
		param.setBgurl("http://389f63c0.ngrok.natapp.cn/app_posp/hf/bgPayResult.action");

		param.setPid(Constant.MERCHANT_NO);
		param.setTransactionid(HFUtil.randomOrder());
		param.setOrderamount("0.01");
		param.setOrdertime(UtilDate.getOrderNum());
		param.setProductname("测试商品");
		param.setProductnum("1");
		param.setProductdesc("测试商品");
		param.setBankid("SLT");
		param.setPaytype("13");
		Bean2QueryStrUtil queryUtil=new Bean2QueryStrUtil();
		System.out.println(queryUtil.bean2QueryStr(param));
	
		
	}
}
