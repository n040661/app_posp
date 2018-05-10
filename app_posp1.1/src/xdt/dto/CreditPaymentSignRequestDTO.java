package xdt.dto;
/**
 * 通过sn号进行pos签到
 * @author p
 *
 */
public class CreditPaymentSignRequestDTO {
	private String sn;//sn号

	public String getSn(){
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
}
