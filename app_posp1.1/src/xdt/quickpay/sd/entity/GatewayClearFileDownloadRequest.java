/**
 * Copyright : http://www.sandpay.com.cn , 2011-2014
 * Project : sandpay-cashier-webgateway
 * $Id$
 * $Revision$
 * Last Changed by pxl at 2016-12-27 下午3:55:20
 * $URL$
 * 
 * Change Log
 * Author      Change Date    Comments
 *-------------------------------------------------------------
 * pxl         2016-12-27        Initailized
 */
package xdt.quickpay.sd.entity;

import com.alibaba.fastjson.annotation.JSONField;

import cn.com.sandpay.cashier.sdk.SandpayRequest;

/**
 * @author pan.xl
 *
 */
public class GatewayClearFileDownloadRequest extends SandpayRequest<GatewayClearFileDownloadResponse>{

	private GatewayClearFileDownloadRequestBody body;
	
	public GatewayClearFileDownloadRequestBody getBody() {
		return body;
	}
	public void setBody(GatewayClearFileDownloadRequestBody body) {
		this.body = body;
	}

	public static class GatewayClearFileDownloadRequestBody {
		private String clearDate;  // 清算日期
		private String fileType;  // 文件返回类型
		private String extend;  // 扩展域
		public String getClearDate() {
			return clearDate;
		}
		public void setClearDate(String clearDate) {
			this.clearDate = clearDate;
		}
		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public String getExtend() {
			return extend;
		}
		public void setExtend(String extend) {
			this.extend = extend;
		}

	}
	
	/* (non-Javadoc)
	 * @see cn.com.sandpay.cashier.SandpayRequest#getResponseClass()
	 */
	@Override
	@JSONField(serialize=false) 
	public Class<GatewayClearFileDownloadResponse> getResponseClass() {
		return GatewayClearFileDownloadResponse.class;
	}

	/* (non-Javadoc)
	 * @see cn.com.sandpay.cashier.SandpayRequest#getTxnDesc()
	 */
	@Override
	@JSONField(serialize=false) 
	public String getTxnDesc() {
		return "gwClearFileDownload";
	}

}
