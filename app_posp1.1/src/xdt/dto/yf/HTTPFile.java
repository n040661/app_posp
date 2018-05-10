package xdt.dto.yf;

import java.io.Serializable;

public class HTTPFile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4391056643297724717L;
	
	private String filename;
	private String inputname;
	private byte[] filedata;
	
	public HTTPFile(String filename, String inputname, byte[] filedata) {
		super();
		this.filename = filename;
		this.inputname = inputname;
		this.filedata = filedata;
	}
	
	public String getFilename() {
		return filename;
	}
	public String getInputname() {
		return inputname;
	}
	public byte[] getFiledata() {
		return filedata;
	}
	
	
}
