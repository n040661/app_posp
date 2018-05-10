/**
 * Copyright : http://www.sandpay.com.cn , 2011-2014
 * Project : sandpay-cashier-webgateway
 * $Id$
 * $Revision$
 * Last Changed by pxl at 2016-12-27 下午3:53:42
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
public class GatewayOrderRefundRequest extends SandpayRequest<GatewayOrderRefundResponse> {
	
	private GatewayOrderRefundRequestBody body;
	
	public GatewayOrderRefundRequestBody getBody() {
		return body;
	}
	public void setBody(GatewayOrderRefundRequestBody body) {
		this.body = body;
	}

	public static class GatewayOrderRefundRequestBody {
		private String orderCode;  // 商户订单号
		private String oriOrderCode;  // 原商户订单号
		private String refundAmount;  // 退货金额
		private String refundReason;  // 退货原因
		private String extend;  // 扩展域
		public String getOrderCode() {
			return orderCode;
		}
		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}
		public String getOriOrderCode() {
			return oriOrderCode;
		}
		public void setOriOrderCode(String oriOrderCode) {
			this.oriOrderCode = oriOrderCode;
		}
		public String getRefundAmount() {
			return refundAmount;
		}
		public void setRefundAmount(String refundAmount) {
			this.refundAmount = refundAmount;
		}
		public String getRefundReason() {
			return refundReason;
		}
		public void setRefundReason(String refundReason) {
			this.refundReason = refundReason;
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
	public Class<GatewayOrderRefundResponse> getResponseClass() {
		return GatewayOrderRefundResponse.class;
	}

	/* (non-Javadoc)
	 * @see cn.com.sandpay.cashier.SandpayRequest#getTxnDesc()
	 */
	@Override
	@JSONField(serialize=false) 
	public String getTxnDesc() {
		return "gwOrderRefund";
	}

}
