package xdt.quickpay.jbb.entity;

import com.huateng.test.BaseObject;


/**
 * @param <T> T
 * @param <L> List
 * @param <S> String 
 * @类说�?
 * @创建�?111111
 * @创建日期:2012-7-20
 */
public abstract  class  XmlEntity<T, L, S> extends BaseObject {
	
	/**
	 * 
	 * @函数说明:获取url
	 * @创建�?zxb
	 * @创建日期:2012-11-12
	 * @return String
	 */
	public abstract String getUrl();
	/**
	 * @函数说明:创建xml
	 * @创建�?zxb
	 * @创建日期:2012-11-12
	 * @return String
	 */
	public abstract String createXml();
	
	/**
	 * @函数说明:解析xml
	 * @创建�?zxb
	 * @创建日期:2012-11-12
	 * @param xml String
	 */
	public abstract void parseXml(String xml);
	
	/**
	 * @函数说明:获取应答处理状�?
	 * @创建�?zxb
	 * @创建日期:2012-11-12
	 * @return boolean
	 */
	public abstract boolean getStatus();


}
