package xdt.pufa;

import java.beans.IntrospectionException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import xdt.pufa.base.Body;
import xdt.pufa.base.Head;
import xdt.pufa.base.Root;
import xdt.util.BeanToMapUtil;
import xdt.util.XMLUtil;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.NodeList;
public class Test {

//spring.xml,classpath:spring-mybatis.xml,classpath:spring-quartz.xml

	/**
	 * @param args
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, IntrospectionException, ClassNotFoundException, UnsupportedEncodingException {

//		BeanToMapUtil beanUtil = new BeanToMapUtil();
//		Root root = new Root();
//		
//		Head head = new Head();
//		Body body =new Body();
//		Map headMap = beanUtil.convertBean(head);
//		Map bodyMap = beanUtil.convertBean(body);
//		
//		root.setHEAD(headMap);
//		root.setBODY(bodyMap);
//		
//		
//		Map rootMap = beanUtil.convertBean(root);
//		XMLUtil xmlUtil =new XMLUtil();
//		xmlUtil.callMapToXML(rootMap);
//		String s  = "<ROOT><HEAD><tran_cd>1131</tran_cd><version>1.1</version><prod_cd>1151</prod_cd><biz_cd>0000008</biz_cd><tran_dt_tm>20160901165501</tran_dt_tm><signed_str></signed_str></HEAD><BODY><order_id>120002</order_id><ins_id_cd>63110000</ins_id_cd><mchnt_cd>631411645110000</mchnt_cd><auth_code>281146214438429055</auth_code><tran_amt>1</tran_amt></BODY></ROOT>";
//		System.out.println(s.length());
//		String s2 = "<ROOT><HEAD><tran_cd>1131</tran_cd><version>1.1</version><prod_cd>1151</prod_cd><biz_cd>0000008</biz_cd><tran_dt_tm>20160909222313</tran_dt_tm><signed_str></signed_str></HEAD><BODY><order_id>120002</order_id><ins_id_cd>63110000</ins_id_cd><mchnt_cd>631411645110000</mchnt_cd><auth_code>281146214438429055</auth_code><tran_amt>1</tran_amt></BODY></ROOT>";
//		System.out.println(s2.length());
//		
//		System.out.println(s2.length()/2);
//		Map<String, String> m = new HashMap<String, String>();
//		m.put("c1", "");
//		m.put("c2", null);
//		
//		System.out.println(m.get("c1"));
//		System.out.println(m.get("c2"));
//		System.out.println(m.get("c13"));
//		System.out.println(StringUtils.isEmpty(m.get("c1")));
//		System.out.println(StringUtils.isEmpty(m.get("c2")));
//		System.out.println(StringUtils.isEmpty(m.get("c13")));
//		byte[] sss = {60, 63, 120, 109, 108, 32, 118, 101, 114, 115, 105, 111, 110, 61, 34, 49, 46, 48, 34, 32, 101, 110, 99, 111, 100, 105, 110, 103, 61, 34, 71, 66, 75, 34, 63, 62, 10, 60, 82, 79, 79, 84, 62, 10, 32, 32, 60, 72, 69, 65, 68, 62, 10, 32, 32, 32, 32, 60, 116, 114, 97, 110, 95, 99, 100, 62, 49, 49, 51, 50, 60, 47, 116, 114, 97, 110, 95, 99, 100, 62, 10, 32, 32, 32, 32, 60, 118, 101, 114, 115, 105, 111, 110, 62, 49, 46, 49, 60, 47, 118, 101, 114, 115, 105, 111, 110, 62, 10, 32, 32, 32, 32, 60, 112, 114, 111, 100, 95, 99, 100, 62, 49, 49, 53, 49, 60, 47, 112, 114, 111, 100, 95, 99, 100, 62, 10, 32, 32, 32, 32, 60, 98, 105, 122, 95, 99, 100, 62, 48, 48, 48, 48, 48, 48, 56, 60, 47, 98, 105, 122, 95, 99, 100, 62, 10, 32, 32, 32, 32, 60, 116, 114, 97, 110, 95, 100, 116, 95, 116, 109, 62, 50, 48, 49, 54, 48, 57, 49, 48, 49, 55, 51, 50, 53, 49, 60, 47, 116, 114, 97, 110, 95, 100, 116, 95, 116, 109, 62, 10, 32, 32, 32, 32, 60, 115, 105, 103, 110, 101, 100, 95, 115, 116, 114, 62, 70, 72, 54, 120, 122, 83, 115, 88, 100, 57, 82, 79, 77, 97, 50, 76, 88, 89, 81, 69, 117, 99, 76, 43, 48, 52, 47, 121, 90, 84, 53, 76, 120, 98, 83, 98, 99, 117, 52, 77, 74, 74, 53, 87, 104, 83, 67, 116, 114, 43, 48, 113, 98, 75, 82, 76, 72, 88, 101, 102, 53, 81, 103, 79, 80, 104, 101, 70, 65, 68, 90, 83, 119, 103, 54, 101, 86, 119, 118, 81, 78, 101, 51, 83, 49, 87, 79, 55, 122, 68, 54, 54, 74, 111, 80, 74, 85, 43, 78, 81, 68, 83, 118, 80, 97, 87, 71, 98, 102, 87, 83, 109, 87, 66, 97, 116, 122, 103, 55, 73, 120, 98, 87, 109, 43, 102, 115, 108, 67, 99, 80, 70, 113, 56, 73, 47, 100, 117, 84, 77, 99, 83, 106, 75, 70, 47, 81, 71, 54, 72, 52, 55, 105, 113, 102, 84, 114, 116, 69, 51, 56, 50, 52, 121, 106, 70, 102, 106, 109, 108, 57, 121, 97, 114, 78, 47, 57, 116, 120, 75, 104, 116, 119, 112, 111, 103, 115, 99, 47, 115, 70, 68, 54, 106, 72, 70, 105, 76, 51, 89, 104, 47, 101, 50, 89, 104, 112, 98, 101, 82, 86, 102, 43, 118, 79, 116, 75, 72, 119, 75, 72, 69, 79, 119, 68, 73, 65, 111, 114, 73, 68, 99, 48, 114, 111, 113, 65, 51, 102, 117, 79, 90, 77, 86, 113, 74, 120, 120, 112, 54, 77, 105, 49, 98, 74, 122, 116, 83, 49, 57, 53, 48, 111, 81, 112, 75, 122, 82, 112, 80, 65, 48, 66, 54, 90, 76, 67, 43, 52, 105, 83, 66, 71, 73, 88, 54, 48, 88, 117, 70, 108, 119, 90, 115, 72, 75, 121, 43, 117, 78, 100, 108, 84, 81, 43, 97, 50, 43, 50, 53, 120, 67, 83, 52, 54, 114, 74, 69, 50, 99, 56, 51, 111, 67, 118, 72, 50, 110, 113, 98, 119, 117, 84, 103, 75, 88, 47, 80, 110, 98, 90, 65, 61, 61, 60, 47, 115, 105, 103, 110, 101, 100, 95, 115, 116, 114, 62, 10, 32, 32, 60, 47, 72, 69, 65, 68, 62, 10, 32, 32, 60, 66, 79, 68, 89, 62, 10, 32, 32, 32, 32, 60, 115, 121, 115, 95, 111, 114, 100, 101, 114, 95, 105, 100, 62, 60, 47, 115, 121, 115, 95, 111, 114, 100, 101, 114, 95, 105, 100, 62, 10, 32, 32, 32, 32, 60, 97, 117, 116, 104, 95, 99, 111, 100, 101, 62, 50, 56, 49, 49, 52, 54, 50, 49, 52, 52, 51, 56, 52, 50, 57, 48, 53, 53, 60, 47, 97, 117, 116, 104, 95, 99, 111, 100, 101, 62, 10, 32, 32, 32, 32, 60, 105, 110, 115, 95, 105, 100, 95, 99, 100, 62, 54, 51, 49, 49, 48, 48, 48, 48, 60, 47, 105, 110, 115, 95, 105, 100, 95, 99, 100, 62, 10, 32, 32, 32, 32, 60, 109, 99, 104, 110, 116, 95, 99, 100, 62, 54, 51, 49, 52, 49, 49, 54, 52, 53, 49, 49, 48, 48, 48, 48, 60, 47, 109, 99, 104, 110, 116, 95, 99, 100, 62, 10, 32, 32, 32, 32, 60, 114, 101, 116, 95, 99, 100, 62, 65, 48, 60, 47, 114, 101, 116, 95, 99, 100, 62, 10, 32, 32, 32, 32, 60, 114, 101, 116, 95, 109, 115, 103, 62, -79, -88, -50, -60, -47, -23, -57, -87, -54, -89, -80, -36, 60, 47, 114, 101, 116, 95, 109, 115, 103, 62, 10, 32, 32, 32, 32, 60, 98, 117, 121, 101, 114, 95, 117, 115, 101, 114, 62, 60, 47, 98, 117, 121, 101, 114, 95, 117, 115, 101, 114, 62, 10, 32, 32, 32, 32, 60, 112, 97, 121, 95, 116, 105, 109, 101, 62, 60, 47, 112, 97, 121, 95, 116, 105, 109, 101, 62, 10, 32, 32, 60, 47, 66, 79, 68, 89, 62, 10, 60, 47, 82, 79, 79, 84, 62, 10};
//		String xml = new String(sss, "GBK").trim();
//		
//		System.out.println(xml);
//		
//		Map<String, Object> xml2map = XMLUtil.xml2MAP(xml);
//		System.out.println(xml2map);
//		
//		String body = XMLUtil.getElement(xml, "BODY");
//        System.out.println(body);
//        body= body.replace("\n", "").replace("\b", "").replace("\t", "").replace(" ", "");
//        System.out.println(body);
//		String signHead = XMLUtil.getElementChild(xml, "HEAD", "signed_str");
//		String ss = "0388<?xml version=\"1.0\" encoding=\"GBK\"?><ROOT><HEAD><tran_cd>1131</tran_cd><version>1.1</version><prod_cd>1151</prod_cd><biz_cd>0000008</biz_cd><tran_dt_tm>20160910170615</tran_dt_tm><signed_str></signed_str></HEAD><BODY><order_id>120002</order_id><ins_id_cd>63110000</ins_id_cd><mchnt_cd>631411645110000</mchnt_cd><auth_code>281146214438429055</auth_code><tran_amt>1</tran_amt></BODY></ROOT>";
//		byte[] ssbytes= ss.getBytes();
//		int sslen =ss.length();
//		byte[] ssbytess =new byte[sslen-4];; 
//		System.arraycopy(ssbytes, 4, ssbytess, 0, sslen-4);
//		System.out.println(new String(ssbytess));
		int reqlen=123;
		
		String len = "000000" + reqlen;
		len = len.substring(len.length() - 6);
		System.out.println(len);
	}
	
//	public void parse(){
//		// 假设我们打算遍历一个元素nodes的所有子节点，每次获取的都是一个Node类型的节点对象node
//		NodeList childs = node.getChildNodes();
//		for(int i = 0; i < childs.getLength(); i++){
//		    Node node = childs.item(i);
//		    if(node.getNodeType == Node.ELEMENT_NODE) {
//		        // 强转为Element类型
//		        Element element = (Element)node;
//		        // 获取元素中的文本值
//		        String val = element.getTextContent();
//		    }
//		    if(node.getNodeType() == Node.TEXT_NODE) {
//		        //这里很可能就是我们不想处理的换行和空格，你可以打印出来看看； ）
//		        Text text = (Text)node;
//		        String txt = text.getWholeText();
//		    }
//		}
//	}

}
