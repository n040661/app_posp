package xdt.util;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * xml字符串解析成map 根据节点拿到值
 */
public class XMLUtil {
	private static Logger logger = Logger.getLogger(XMLUtil.class);


	@SuppressWarnings("unchecked")
	public static Map<String, Object> Dom2Map(Document doc) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (doc == null)
			return map;
		Element root = doc.getRootElement();
		for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = (Element) iterator.next();
			List list = e.elements();
			if (list.size() > 0) {
				map.put(e.getName(), Dom2Map(e));
			} else
				map.put(e.getName(), e.getText());
		}
		return map;
	}
	@SuppressWarnings("unchecked")
	public static Map Dom2Map(Element e) {
		Map map = new HashMap();
		List list = e.elements();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Element iter = (Element) list.get(i);
				List mapList = new ArrayList();

				if (iter.elements().size() > 0) {
					Map m = Dom2Map(iter);
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName()
								.equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(m);
						}
						if (obj.getClass().getName()
								.equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(m);
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), m);
				} else {
					if (map.get(iter.getName()) != null) {
						Object obj = map.get(iter.getName());
						if (!obj.getClass().getName()
								.equals("java.util.ArrayList")) {
							mapList = new ArrayList();
							mapList.add(obj);
							mapList.add(iter.getText());
						}
						if (obj.getClass().getName()
								.equals("java.util.ArrayList")) {
							mapList = (List) obj;
							mapList.add(iter.getText());
						}
						map.put(iter.getName(), mapList);
					} else
						map.put(iter.getName(), iter.getText());
				}
			}
		} else
			map.put(e.getName(), e.getText());
		return map;
	}
	
	
	public static byte[] callMapToXML(Map map) {  
        logger.info("将Map转成Xml, Map：" + map.toString());  
        StringBuffer sb = new StringBuffer();  
//        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");  
      sb.append("<ROOT>");  
        mapToXML(map, sb);  
        sb.append("</ROOT>");  
        logger.info("将Map转成Xml, Xml：" + sb.toString());  
        try {  
            return sb.toString().getBytes("UTF-8");  
        } catch (Exception e) {  
            logger.error(e);  
        }  
        return null;  
    }  
    private static void mapToXML(Map map, StringBuffer sb) {  
        Set set = map.keySet();  
        for (Iterator it = set.iterator(); it.hasNext();) {  
            String key = (String) it.next();  
            Object value = map.get(key);  
            if (null == value)  
                value = "";  
            if (value.getClass().getName().equals("java.util.ArrayList")) {  
                ArrayList list = (ArrayList) map.get(key);  
                sb.append("<" + key + ">");  
                for (int i = 0; i < list.size(); i++) {  
                    HashMap hm = (HashMap) list.get(i);  
                    mapToXML(hm, sb);  
                }  
                sb.append("</" + key + ">");  
            } else {  
                if (value instanceof HashMap) {  
                    sb.append("<" + key + ">");  
                    mapToXML((HashMap) value, sb);  
                    sb.append("</" + key + ">");  
                } else {  
                    sb.append("<" + key + ">" + value + "</" + key + ">");  
                }  
            }  
  
        }  
    } 
    
    public static String getElement(String xml, String param) {
    	Document doc;
    	Element root ;
    	Element element = null;
		try {
			doc = DocumentHelper.parseText(xml);
			 root = doc.getRootElement();
	    	 element = root.element(param);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return element.asXML();
    }
    
    public static String getElementChild(String xml, String param, String child) {
    	Document doc;
    	Element root  ;
    	Element son  ;
    	Element childE  ;
    	String ret = "";
		try {
			doc = DocumentHelper.parseText(xml);
			 root = doc.getRootElement();
	    	 son = root.element(param);
	    	 childE = son.element(child);
	    	 ret = childE.getTextTrim();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return ret;
    }
    
    
    /*
     * 获得X属性结果是X值的整个标签
     */
    public static Element parse(Element node , String type ) {
        for (Iterator iter = node.elementIterator(); iter.hasNext();) {
            Element element = (Element) iter.next();
            Attribute name = element.attribute(type);
            if (name != null) {
                String value = name.getValue();
                if (value != null )
                    return element;
                else
                    parse(element , type );
            }
        }
        return null;
    }
    
 
   

	/**
	 * 测试方法 后期删除
	 */
	public static void main(String[] args) throws Exception {

		String xmlString = "<html>" + "<head>" + "<title>dom4j解析一个例子</title>"

		+ "<script>" + "<username>yangrong</username>"

		+ "<password>123456</password>" + "</script>" + "</head>"

		+ "<body>" + "<result>0</result>" + "<form>"

		+ "<banlce>1000</banlce>" + "<subID>36242519880716</subID>"

		+ "</form>" + "</body>" + "</html>";

		String s1 = "<?xml version=\"1.0\" encoding=\"GBK\"?><ROOT><HEAD><tran_cd>12</tran_cd><version>23</version><prod_cd>3R</prod_cd><biz_cd>R</biz_cd><tran_dt_tm>R</tran_dt_tm><signed_str>D</signed_str></HEAD><BODY><order_id>D</order_id><ins_id_cd>56</ins_id_cd><mchnt_cd>6</mchnt_cd><auth_code>6</auth_code><tran_amt>6</tran_amt><orig_order_id>6</orig_order_id><refund_reason></refund_reason><sys_order_id></sys_order_id><ret_cd></ret_cd><ret_msg></ret_msg><buyer_user></buyer_user><retry_flag></retry_flag><pay_time></pay_time></BODY></ROOT>";

		Document doc = DocumentHelper.parseText(s1);

		Map<String, Object> map = XMLUtil.Dom2Map(doc);
		
		Map<String, Object> headmap =new HashMap<String, Object>();
		headmap = (Map<String, Object>) map.get("HEAD");
		System.out.println(map.toString());
//		System.out.println(headmap.get("tran_cd"));
		getElement(s1, "BODY");
		
		String sign = getElementChild(s1, "HEAD", "signed_str");
		System.out.println(sign);

	}

}
