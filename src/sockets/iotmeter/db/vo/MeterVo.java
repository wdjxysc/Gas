package sockets.iotmeter.db.vo;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/14.
 */
public class MeterVo {
    /**
     * 数据id
     */
    private String id;

    /**
     * 表ID
     */
    private String meterId;

    /**
     * 表SN号
     */
    private String meterSn;

    /**
     * 生产日期
     */
    private Date productionDate;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 类型
     */
    private char typeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public String getMeterSn() {
        return meterSn;
    }

    public void setMeterSn(String meterSn) {
        this.meterSn = meterSn;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public char getTypeId() {
        return typeId;
    }

    public void setTypeId(char typeId) {
        this.typeId = typeId;
    }
}
