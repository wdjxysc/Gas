package webapp.sockets.iotmeter.db.vo;

import java.util.Date;

/**
 * 空气指数
 * @author Administrator
 *
 */
public class AirIndexVo {
	/***/
	private String id;
	/**设备编号*/
	private String devNo="";
	/**空气质量指数*/
	private String aqi="";
	/**pm2.5含量*/
	private String pm25="";
	/**二氧化硫含量*/
	private String so2="";
	/**二氧化氮含量*/
	private String no2="";
	/**臭氧含量*/
	private String o3="";
	/**一氧化碳含量*/
	private String co="";
	/**pm10含量*/
	private String pm10="";
	/**tvoc*/
	private String tvoc="";
	/**甲醛*/
	private String hcho="";
	/**温度*/
	private String temperature="";
	/**湿度*/
	private String humidity="";
	/**无线类型*/
	private String type="";
	/**信号强度*/
	private String rssi="";
	/**创建人名称*/
	private String createName="";
	/**创建日期*/
	private Date createDate;
	/**更新人名称*/
	private String updateName="";
	/**更新日期*/
	private Date updateDate;
	/**是否有效*/
	private String deleteStatus="";
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDevNo() {
		return devNo;
	}
	public void setDevNo(String devNo) {
		this.devNo = devNo;
	}
	public String getAqi() {
		return aqi;
	}
	public void setAqi(String aqi) {
		this.aqi = aqi;
	}
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}
	public String getSo2() {
		return so2;
	}
	public void setSo2(String so2) {
		this.so2 = so2;
	}
	public String getNo2() {
		return no2;
	}
	public void setNo2(String no2) {
		this.no2 = no2;
	}
	public String getO3() {
		return o3;
	}
	public void setO3(String o3) {
		this.o3 = o3;
	}
	public String getCo() {
		return co;
	}
	public void setCo(String co) {
		this.co = co;
	}
	public String getPm10() {
		return pm10;
	}
	public void setPm10(String pm10) {
		this.pm10 = pm10;
	}
	public String getTvoc() {
		return tvoc;
	}
	public void setTvoc(String tvoc) {
		this.tvoc = tvoc;
	}
	public String getHcho() {
		return hcho;
	}
	public void setHcho(String hcho) {
		this.hcho = hcho;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRssi() {
		return rssi;
	}
	public void setRssi(String rssi) {
		this.rssi = rssi;
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
