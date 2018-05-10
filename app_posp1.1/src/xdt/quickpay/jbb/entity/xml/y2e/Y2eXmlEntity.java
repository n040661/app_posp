package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.entity.XmlEntity;
import xdt.quickpay.jbb.entity.xml.util.XmlMsgUtil;

public class Y2eXmlEntity extends XmlEntity{

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createXml() {
		return new XmlMsgUtil().marshaller(this);
		}

	@Override
	public void parseXml(String xml) {
		new XmlMsgUtil().unmarshaller(this, xml);
		}

	@Override
	public boolean getStatus() {
		// TODO Auto-generated method stub
		return false;
	}

}

