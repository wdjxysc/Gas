package webapp.sockets.iotmeter.db.vo;

import java.util.Date;

/**
 * 设备异常
 * @author Administrator
 *
 */
public class DeviceAnomalyVo {
	private String id;
	/**设备id*/
	private String devId="";
	/**发生日期*/
	private String happenDate="";
	/**发生时间*/
	private String happenTime="";
	/**异常类型*/
	private String nomalyType="";
	/**创建人名称*/
	private String createName="";
	/**创建日期*/
	private Date createDate;
	/**更新人名称*/
	private String updateName="";
	/**更新日期*/
	private Date updateDate;
	/**流程状态*/
	private String deleteStatus="";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDevId() {
		return devId;
	}
	public void setDevId(String devId) {
		this.devId = devId;
	}
	public String getHappenDate() {
		return happenDate;
	}
	public void setHappenDate(String happenDate) {
		this.happenDate = happenDate;
	}
	public String getHappenTime() {
		return happenTime;
	}
	public void setHappenTime(String happenTime) {
		this.happenTime = happenTime;
	}
	public String getNomalyType() {
		return nomalyType;
	}
	public void setNomalyType(String nomalyType) {
		this.nomalyType = nomalyType;
	}
	public String getCreateName() {
		return createName;
	}
	public void setCreateName(String createName) {
		this.createName = createName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getUpdateName() {
		return updateName;
	}
	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getDeleteStatus() {
		return deleteStatus;
	}
	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
	
}
