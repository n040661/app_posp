package xdt.dto;
/**
 * 签到状态修改
 * @author p
 *
 */
public class PaymentSignResponseRequestDTO {
	private String status;//状态  0:成功  1：失败
    private String snno;//设备号

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    public String getSnno() {
        return snno;
    }

    public void setSnno(String snno) {
        this.snno = snno;
    }
}
