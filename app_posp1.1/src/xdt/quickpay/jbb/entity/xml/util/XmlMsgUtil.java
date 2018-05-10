package xdt.quickpay.jbb.entity.xml.util;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import xdt.quickpay.jbb.entity.XmlEntity;
import xdt.quickpay.jbb.entity.xml.y2e.Y2eXmlEntity;
import xdt.quickpay.jbb.util.Y2eField;





/**
 * @类说�?
 * @创建�?zxb
 * @创建日期:2012-10-9
 */
public class XmlMsgUtil {

	private static final String FIELD_TYPE_LIST = "list";
	private static final String FIELD_TYPE_DEFAULT = "text";
	private static final Logger log = Logger.getLogger(XmlMsgUtil.class);
	private Map<String, Element> map = new Hashtable<String, Element>();
	private Document doc = null;

	private static String toUpperCaseFirstChar(String str) {
		
		String first = (str.substring(0, 1));
		if (first.equals("n")) {
			return str;
		} else {
			first = first.toUpperCase();

		}
		String other = str.substring(1);
		return first + other;
	}

	/**
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 * @param fieldName
	 *            get方法字段名称
	 * @return <br>
	 */
	public static String getMethodWithGet(String fieldName) {
		return "get".concat(toUpperCaseFirstChar(fieldName));
	}

	/**
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 * @param fieldName
	 *            set方法字段名称
	 * @return <br>
	 */
	public static String getMethodWithSet(String fieldName) {
		return "set".concat(toUpperCaseFirstChar(fieldName));

	}

	/**
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 * @param fieldName
	 *            add方法字段名称
	 * @return <br>
	 */
	public static String getMethodWithAdd(String fieldName) {
		return "add".concat(toUpperCaseFirstChar(fieldName));

	}

	/**
	 * 
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 * @param msg
	 *            解析xml转化的对�?
	 * @param xmlDoc
	 *            解析的xml
	 */
	public void unmarshaller(Y2eXmlEntity msg, String xmlDoc) {
		try {
			
			if(StringUtils.isNotEmpty(xmlDoc)){
				
				Document doc = DocumentHelper.parseText(xmlDoc);
				Field[] childFields = msg.getClass().getDeclaredFields();
				Field[] superFields = msg.getClass().getSuperclass().getDeclaredFields();
				Y2eField fieldAnno=null;
				log.info(" parse xml file start...");
				
				for (Field field : superFields) {
					fieldAnno = (Y2eField) field.getAnnotation(Y2eField.class);
					if (fieldAnno != null && !field.getType().isArray()) {
						processDefaultField(msg, field, doc);
					}
				}
				
				for (Field field : childFields) {
					fieldAnno = (Y2eField) field.getAnnotation(Y2eField.class);
					if (fieldAnno != null && !field.getType().isArray()) {
						String fieldType = fieldAnno.type();
						if (fieldType.equals(FIELD_TYPE_DEFAULT)) {
							processDefaultField(msg, field, doc);
						} else if (fieldType.equals(FIELD_TYPE_LIST)) {
							processListField(msg, field, doc);
						}

					}
				}
				
				log.info(" parse xml file successfully finished.");
			}
			
		} catch (Exception e) {
			
			log.info(" parse xml file unsuccessfully finished.");
		}
	}

	private static void processListField(Y2eXmlEntity msg, Field field,Document doc) {
		try {
			Y2eField fieldAnno = (Y2eField) field.getAnnotation(Y2eField.class);
			String fieldPath = "/" + fieldAnno.path().replaceAll("\\.", "/");
			List<Node> itemList = doc.selectNodes(fieldPath);
			if (itemList == null || itemList.size() == 0) {
				throw new Exception(" not match  node to appoint element,appoint path is :"+ fieldPath);
			}
		
			// 根据泛型创建参数实例
			Class itemClass = getGenericType(field);
			Constructor constructor = itemClass.getDeclaredConstructor();

			// 根据集合类型字段名获取add方法
			Method addMethod = msg.getClass().getMethod(getMethodWithAdd(field.getName()), itemClass);
			Field[] itemFields = itemClass.getDeclaredFields();
			for (Node itemNode : itemList) {
				Object itemInstance = constructor.newInstance();
				for (Field itemField : itemFields) {
					fieldAnno = (Y2eField) itemField.getAnnotation(Y2eField.class);
					fieldPath=fieldAnno.path().replaceAll("\\.", "/");
					Node fieldNode = itemNode.selectSingleNode(fieldPath);
					if (fieldNode == null) {
						log.error(" not match appoint node is :"+ itemField.getName());
					} else {
						Method setMethod = itemInstance.getClass().getMethod(getMethodWithSet(itemField.getName()),itemField.getType());
						setMethod.invoke(itemInstance, fieldNode.getStringValue());
					}
				}
				addMethod.invoke(msg, itemInstance);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("error! process list field occoured error！error reason:"+ e.getMessage());
		}
	}

	private static void processDefaultField(Y2eXmlEntity msg, Field field,
			Document doc) {
		try {
			Y2eField fieldAnno = (Y2eField) field.getAnnotation(Y2eField.class);
			String fieldPath = fieldAnno.path().replaceAll("\\.", "/");
			Node fieldNode = doc.selectSingleNode(fieldPath);
			if (fieldNode == null) {
				log.error(" not match appoint path is :" + fieldPath);
			} else {
				Method setMethod = msg.getClass().getMethod(getMethodWithSet(field.getName()), field.getType());
				setMethod.invoke(msg, fieldNode.getStringValue());
			}
		} catch (Exception e) {
			log.error("error! process default field occoured error！error reason: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 * @param msg
	 *            生成xml转化的对�?
	 * @return String
	 */
	public String marshaller(XmlEntity msg) {
		try {
			log.info(" create xml file start...");
			Field[] childFields = msg.getClass().getDeclaredFields();
			Field[] superFields = msg.getClass().getSuperclass().getDeclaredFields();
			Y2eField fieldAnno=null;
			// 父类
			for (Field field : superFields) {
				fieldAnno = (Y2eField) field.getAnnotation(Y2eField.class);
				if (fieldAnno != null && !field.getType().isArray()) {
					String fieldPath = fieldAnno.path();
					if(fieldPath.equalsIgnoreCase("yt2e.trans.trn-y2e1010-req.trn-info.notify-url")){
						log.info("ssssssssssphp");
						Object fieldVal = msg.getClass().getMethod(getMethodWithGet(field.getName()), null).invoke(msg, null);
						createElement(fieldPath, 0).setText(nullToEmpty(fieldVal));//
					}
					else {
						Object fieldVal = msg.getClass().getMethod(getMethodWithGet(field.getName()), null).invoke(msg, null);
						createElement(fieldPath, 0).setText(nullToEmpty(fieldVal));//
					}

				}
			}
			// 子类
			for (Field field : childFields) {
				fieldAnno = (Y2eField) field.getAnnotation(Y2eField.class);
				if (fieldAnno != null && !field.getType().isArray()) {
					String fieldType = fieldAnno.type();
					String fieldPath = fieldAnno.path();
					if (fieldType.equals(FIELD_TYPE_DEFAULT)) {
						Object fieldVal = msg.getClass().getMethod(getMethodWithGet(field.getName()),null).invoke(msg, null);
						createElement(fieldPath, 0).setText(nullToEmpty(fieldVal));// 叶子节点set�?
					} else if (fieldType.equals(FIELD_TYPE_LIST)) {
						createListElement(fieldPath, msg, field);// 叶子节点set�?
					}
				}
			}
			map.clear();
			log.info(" create xml str successfully finished.");
		} catch (Exception e) {
			log.error(" create xml str unsuccessfully finished,reason : "+ e.getMessage());

		}
		return createXmlByElement();
	}

	private Element createElement(String path, int offset) {
		String[] paths = path.split("\\.");
		String[] keys = new String[paths.length];
		Element parent = null;
		Element child = null;
		for (int i = 0; i < paths.length; i++) {
			if (i == 0) {
				keys[i] = paths[i];
			} else {
				keys[i] = keys[i - 1] + paths[i];
			}
			if (i == (keys.length - 1)) {
				keys[i] = keys[i] + getProxyMark(offset);
			}
		}

		for (int i = 0; i < keys.length; i++) {
			if (map.containsKey(keys[i])) {
				if (i != keys.length - 1) {
					parent = (Element) map.get(keys[i]);
				} else {
					child = (Element) map.get(keys[i]);
				}

			} else {
				if (i == 0) {// i=0,根元�?
					doc = DocumentHelper.createDocument();
					child = doc.addElement(paths[i]);
					child.addAttribute("version", "100");
					child.addAttribute("security", "true");
					child.addAttribute("lang", "chs");
				} else {
					child = parent.addElement(paths[i]);// 创建子元�?
				}
				if (i != keys.length - 1)parent = child;// 作为下个元素的父元素
				map.put(keys[i], child);// 创建的元素保存到map

			}
		}
		return child;

	}

	private Element createListElement(String complexPath, XmlEntity msg,Field complexField) throws Exception {
		try {
			List complexItems = (List) msg.getClass().getMethod(getMethodWithGet(complexField.getName()), null).invoke(msg, null);
			Class paramClass = getGenericType(complexField);
			
			Field[] itemFields = paramClass.getDeclaredFields();
			int offset = 0;
			
			for (Object complexItem:complexItems) {
				createElement(complexPath, offset);// List节点
				for (Field itemField : itemFields) {// 创建list子节�?
					Y2eField fieldAnno = (Y2eField) itemField.getAnnotation(Y2eField.class);
					String itemFieldPath=complexPath + getProxyMark(offset)+"." ;
					if(fieldAnno!=null){
						itemFieldPath+=fieldAnno.path();
					}
					Object itemFieldVal = paramClass.getMethod(getMethodWithGet(itemField.getName()), null).invoke(complexItem, null);
					createElement(itemFieldPath, 0).setText(nullToEmpty(itemFieldVal));// 叶子节点set�?
				}
				offset++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(" create list item happended error !");
		}
		return null;
	}

	/**
	 * 
	 * @throws BusnissException
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-10
	 */
	private static Class getGenericType(Field field) throws Exception {
		// 获取泛型类型
		Type actuaParamType = null;
		Type genericType = field.getGenericType();
		if (genericType instanceof ParameterizedType) {
			// 执行强制类型转换
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			// 获取泛型类型的泛型参�?
			actuaParamType = parameterizedType.getActualTypeArguments()[0];
		} else {
			throw new Exception(" can't get generic's type!");
		}
		return (Class) actuaParamType;
	}
	

	private String createXmlByElement() {
		StringWriter writer = null;
		XMLWriter xmlWriter=null;
		try {
			writer=new StringWriter();
			OutputFormat format = OutputFormat.createCompactFormat();
			format.setEncoding("utf-8");
			xmlWriter = new XMLWriter(writer, format);
			xmlWriter.write(this.doc);
			xmlWriter.close();
		} catch (Exception e) {
			log.error(" create xml by element unsuccessfully finished ! reason: "+e.getMessage());
		}finally{
			try {
				xmlWriter.close();
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String resultStr = writer.toString();
		if (doc != null)
			doc.clearContent();
		return resultStr;
	}

	/**
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-9
	 * @param obj
	 *            生成xml转化的对�?
	 * @return String
	 */
	public static String nullToEmpty(Object obj) {
		return (obj == null) ? "" : obj.toString();
	}

	private String getProxyMark(int offset) {
		return "@" + offset;
	}

	/**
	 * @param args String[]
	 * @函数说明:
	 * @创建�?zxb
	 * @创建日期:2012-10-10
	 */
	public static void main(String[] args){
		long time1=System.currentTimeMillis();
		XmlMsgUtil util = new XmlMsgUtil();
		//util.parseXmlB2eRes();
		String xmlStr=util.creatXmlToB2e0007();
		//B2e0014Req req=util.parseXmlToB2e0014();
		long time2=System.currentTimeMillis();
		System.out.println("----------"+xmlStr);
		//String resultStr=util.marshaller(req);
		//long time3=System.currentTimeMillis();
		//System.out.println("----------"+(time3-time2));
		//System.out.println("----------------"+resultStr);
	}
	
	/**
	 * @函数说明:creatXmlToB2e0007
	 * @创建�?zxb
	 * @创建日期:2012-10-23
	 * @return String
	 */
	public String creatXmlToB2e0007(){
		return null;/*
		
		B2e0007Req msg = new B2e0007Req();
		B2e0007ReqDetail detail1 = new B2e0007ReqDetail();
		detail1.setInsid("0000000");
		detail1.setObssid("11232132");
		msg.addDetail(detail1);
		B2e0007ReqDetail detail2 = new B2e0007ReqDetail();
		detail2.setInsid("1111111");
		detail2.setObssid("2222222");
		msg.addDetail(detail2);
		return this.marshaller(msg);
	*/}


	

}
