package webapp.sockets.concentrateor.dao.vo;

/**
 * Created by Administrator on 2017/5/24.
 */
public class ConcentratorCollectorMeterMapVo {
    /**id*/
    private String id;

    /**设备编号*/
    private String concentratorId;

    /**设备编号*/
    private String collectorId;

    private String meterId;

    /**创建时间*/
    private String createDate;

    /**更新时间*/
    private String updateDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConcentratorId() {
        return concentratorId;
    }

    public void setConcentratorId(String concentratorId) {
        this.concentratorId = concentratorId;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
