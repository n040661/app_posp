package xdt.dto.yf;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * xml转换工具
 *
 * @author guoyanjiang
 * @creation 20152015-4-13下午03:47:32
 */
public class JaxbUtils {

//	/**
//	 * parse xml String to specific Object.
//	 * 
//	 * @param xmlString
//	 * @param c
//	 * @return
//	 * @throws UnmarshalException
//	 * @throws Exception
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> T parseXML(String xmlString, Class<T> c) throws Exception {
//		JAXBContext jaxbContext = JAXBContext.newInstance(c);
//		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//		// Ignore the namespace.
//		SAXParserFactory sax = SAXParserFactory.newInstance();
//		sax.setNamespaceAware(false);
//		XMLReader xmlReader = sax.newSAXParser().getXMLReader();
//		Source source = new SAXSource(xmlReader, new InputSource(new StringReader(xmlString)));
//		return (T) jaxbUnmarshaller.unmarshal(source);
//	}

    @SuppressWarnings("unchecked")
    public static <T> T parseXML(String xmlString, Class<T> c) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(c);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        StringReader sr = null;
        T t = null;
        try {
            sr = new StringReader(xmlString);
            t = (T) jaxbUnmarshaller.unmarshal(sr);
        } finally {
            sr.close();
        }

        return t;
    }

    /**
     * deparser Object to xml String
     *
     * @param object 解析对象
     * @throws Exception 解析过程中可能会有异常
     * @author guoyanjiang
     * @creation 20152015-4-14下午03:59:01
     */
    public static <T> String deParseXML(Object object) throws Exception {
        String result = null;
        StringWriter writer = new StringWriter();
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(object, writer);
            result = writer.getBuffer().toString();
        } finally {
            writer.close();
        }
        return result;
    }
}
