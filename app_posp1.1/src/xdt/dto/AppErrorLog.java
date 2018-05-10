package xdt.dto;
/**
 * app系统错误信息
 * @author wumeng
 */
public class AppErrorLog {
	
	 	private String equipmentmodel;//设备类型

	    private String systemversion;//系统版本

	    private String appversion;//应用版本

	    private String appidentifier;// 应用包名

	    private String logs;//错误信息
	    
	    
	    //------------------------------------------
	    private String createtime;//创建时间

	    private String mercid;//商户编号
	   //------------------------------------------
	    
	    
		public String getEquipmentmodel() {
			return equipmentmodel;
		}

		public void setEquipmentmodel(String equipmentmodel) {
			this.equipmentmodel = equipmentmodel;
		}

		public String getSystemversion() {
			return systemversion;
		}

		public void setSystemversion(String systemversion) {
			this.systemversion = systemversion;
		}

		public String getAppversion() {
			return appversion;
		}

		public void setAppversion(String appversion) {
			this.appversion = appversion;
		}

		public String getAppidentifier() {
			return appidentifier;
		}

		public void setAppidentifier(String appidentifier) {
			this.appidentifier = appidentifier;
		}

		public String getCreatetime() {
			return createtime;
		}

		public void setCreatetime(String createtime) {
			this.createtime = createtime;
		}

		public String getMercid() {
			return mercid;
		}

		public void setMercid(String mercid) {
			this.mercid = mercid;
		}

		public String getLogs() {
			return logs;
		}

		public void setLogs(String logs) {
			this.logs = logs;
		}
	    
	
}