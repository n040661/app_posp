package xdt.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XDTHttpServletRequestWrapper extends HttpServletRequestWrapper{

	HttpServletRequest request;
	public XDTHttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
	}

	public String getParameter(String name){
		String str  = (String)this.request.getAttribute(name);
		str = str.replace("\"{", "{");
		str = str.replace("}\"", "}");
		str = str.replace("\\", "");
		return str;
	}
	
}
