package xdt.dto;

/**
 * 通过sn号进行查询pos
 * 
 */
public class SnPmsPosInfoRequestDTO {

	private String mobilePhone;// 手机号

	private String deviceNo;// sn号

	private String deviceType;// 设备类型

	private String deviceName;// 设备名称

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

}
