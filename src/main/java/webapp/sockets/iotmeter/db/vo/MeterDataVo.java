package webapp.sockets.iotmeter.db.vo;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/12.
 * 抄表单表数据
 */
public class MeterDataVo {
    /**id*/
    private String id;
    /**设备编号*/
    private String meterId="";
    /**流量读数*/
    private float flow;
    /**阀门状态*/
    private int valveState;

    /**数据时间*/
    private String dataTime;
    /**创建时间*/
    private String createDate;


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

    public float getFlow() {
        return flow;
    }

    public void setFlow(float flow) {
        this.flow = flow;
    }

    public int getValveState() {
        return valveState;
    }

    public void setValveState(int valveState) {
        this.valveState = valveState;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public final int ValveStateClose = 0;
    public final int ValveStateOpen = 1;
    public final int ValveStateError = 2;
}
