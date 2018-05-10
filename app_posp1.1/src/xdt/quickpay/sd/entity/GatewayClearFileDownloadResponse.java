/**
 * Copyright : http://www.sandpay.com.cn , 2011-2014
 * Project : sandpay-cashier-webgateway
 * $Id$
 * $Revision$
 * Last Changed by pxl at 2016-12-27 下午3:55:43
 * $URL$
 * 
 * Change Log
 * Author      Change Date    Comments
 *-------------------------------------------------------------
 * pxl         2016-12-27        Initailized
 */
package xdt.quickpay.sd.entity;

import cn.com.sandpay.cashier.sdk.SandpayResponse;

/**
 * @author pan.xl
 *
 */
public class GatewayClearFileDownloadResponse extends SandpayResponse {

	private GatewayClearFileDownloadResponseBody body;
	
	public GatewayClearFileDownloadResponseBody getBody() {
		return body;
	}
	public void setBody(GatewayClearFileDownloadResponseBody body) {
		this.body = body;
	}
	
	public class GatewayClearFileDownloadResponseBody {
		private String clearDate;  // 清算日期
		private String content;  // 内容
		private String extend;  // 扩展域
		public String getClearDate() {
			return clearDate;
		}
		public void setClearDate(String clearDate) {
			this.clearDate = clearDate;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getExtend() {
			return extend;
		}
		public void setExtend(String extend) {
			this.extend = extend;
		}
	}
}
