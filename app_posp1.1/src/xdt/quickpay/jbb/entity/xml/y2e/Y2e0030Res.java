package xdt.quickpay.jbb.entity.xml.y2e;

import xdt.quickpay.jbb.util.Y2eField;

public class Y2e0030Res extends Y2eXmlEntity{
	@Y2eField(path = "yt2e.trans.trn-y2e0030-res.status.rspcod")
	private String rspcod;//报文处理状�?
	
	@Y2eField(path = "yt2e.trans.trn-y2e0030-res.status.rspmsg")
	private String rspmsg;
	
	@Y2eField(path = "yt2e.trans.trn-y2e0030-res.recon-file.reconfile")
	private String  reconfile;
	
	public String getRspcod() {
		return rspcod;
	}

	public void setRspcod(String rspcod) {
		this.rspcod = rspcod;
	}

	public String getRspmsg() {
		return rspmsg;
	}

	public void setRspmsg(String rspmsg) {
		this.rspmsg = rspmsg;
	}

	public String getReconfile() {
		return reconfile;
	}

	public void setReconfile(String reconfile) {
		this.reconfile = reconfile;
	}

}
