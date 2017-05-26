package webapp.sockets.concentrateor.dao.vo;

/**
 * Created by Administrator on 2016/11/14.
 */
public class MeterExceptionVo {
    /**id*/
    private String id;
    /**设备编号*/
    private String meterId;
    /**异常代码*/
    private String exceptionId;
    /**异常发生时间*/
    private String dataDate;

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

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    /**创建时间*/
    private String createDate;
}
