package xdt.pufa.base;


/**
 * 浦发交易字段
 * @author ValalaVilla
 *
 */
public class FieldDefine {
	
	/**交易代码*/
	public final static String PF_HEAD_TRAN_CD="tran_cd";
	/**版本代码*/
	public final static String PF_HEAD_VERSION="version";
	/**产品代码*/
	public final static String PF_HEAD_PROD_CD="prod_cd";
	/**业务代码*/
	public final static String PF_HEAD_BIZ_CD="biz_cd";
	/**日期时间*/
	public final static String PF_HEAD_TRAN_DT_TM="tran_dt_tm";
	/**签名信息*/
	public final static String PF_HEAD_SIGNED_STR="signed_str";
	
	
	/**订单号*/
	public final static String PF_REQ_BODY_ORDER_ID="order_id";
	/**机构编号*/
	public final static String PF_REQ_BODY_INS_ID_CD="ins_id_cd";
	/**商户代码*/
	public final static String PF_REQ_BODY_MCHNT_CD="mchnt_cd";
	/**授权码*/
	public final static String PF_REQ_BODY_AUTH_CODE="auth_code";
	/**交易金额*/
	public final static String PF_REQ_BODY_TRAN_AMT="tran_amt";
	/**原交易订单号*/
	public final static String PF_REQ_BODY_ORIG_ORDER_ID="orig_order_id";
	/**退款原因*/
	public final static String PF_REQ_BODY_REFUND_REASON="refund_reason";
	/**渠道流水号*/
	public final static String PF_REQ_BODY_SYS_ORDER_ID="sys_order_id";
	/**返回码*/
	public final static String PF_REQ_BODY_RET_CD="ret_cd";
	/**返回信息*/
	public final static String PF_REQ_BODY_RET_MSG="ret_msg";
	/**用户登陆名*/
	public final static String PF_REQ_BODY_BUYER_USER="buyer_user";
	/**重试标志*/
	public final static String PF_REQ_BODY_RETRY_FLAG="retry_flag";
	/**支付时间*/
	public final static String PF_REQ_BODY_PAY_TIME="pay_time";
	/**支付url,给用户扫码用*/
	public final static String PF_REQ_BODY_PAY_ORDER_ID="pay_order_id";
	
	//------------------------------------------------------------------------------
//	public static void setField(String[] Field)
//	{
//		Field[0]=PF_HEAD_TRAN_CD;
//		Field[1]=PF_HEAD_VERSION;
//		Field[2]=PF_HEAD_PROD_CD;
//		Field[3]=PF_HEAD_BIZ_CD;
//		Field[4]=PF_HEAD_TRAN_DT_TM;
//		Field[5]=PF_HEAD_SIGNED_STR;
//		Field[6]=PF_REQ_BODY_ORDER_ID;
//		Field[7]=PF_REQ_BODY_INS_ID_CD;
//		Field[8]=PF_REQ_BODY_MCHNT_CD;
//		Field[9]=PF_REQ_BODY_AUTH_CODE;
//		Field[10]=PF_REQ_BODY_TRAN_AMT;
//		Field[11]=PF_REQ_BODY_ORIG_ORDER_ID;
//		Field[12]=PF_REQ_BODY_REFUND_REASON;
//		Field[13]=PF_REQ_BODY_SYS_ORDER_ID;
//		Field[14]=PF_REQ_BODY_RET_CD;
//		Field[15]=PF_REQ_BODY_RET_MSG;
//		Field[16]=PF_REQ_BODY_BUYER_USER;
//		Field[17]=PF_REQ_BODY_RETRY_FLAG;
//		Field[18]=PF_REQ_BODY_PAY_TIME;
//	}
	
//	public static void PrintAF(DataObjectType req)
//	{
//		String[] Field= new String[129];// array[0..127] of string
//		setField(Field);
//		
//		System.out.println("MTI: "+(String)req.get(AF000_MTI));
//		System.out.println("BITMAP: "+(String)req.get(AF001_BitMap));
//		
//		byte[] SendData=CodeConverter.hexStringToByte((String)req.get(AF001_BitMap));
//		for (int i=2;i<=SendData.length*8;i++)
//		{
//			if ((SendData[(i-1)/8]&(1<<(7-(i-1)%8)))>0)
//			{
//				System.out.println("Filed "+i+": "+req.get(Field[i]));
//			}
//		}
//		System.out.println("********print end*************");		
//	}

}


