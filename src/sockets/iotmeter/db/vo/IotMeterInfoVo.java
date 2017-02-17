package sockets.iotmeter.db.vo;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/14.
 */
public class IotMeterInfoVo {
    /** 数据id */
    private String id;

    /** 表id */
    private String meterId;

    /** 表socket ip */
    private String clientIp;

    /** 表socket端口号 */
    private String clientPort;

    /** 最后在线日期 */
    private Date lastOnlineDate;

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

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getClientPort() {
        return clientPort;
    }

    public void setClientPort(String clientPort) {
        this.clientPort = clientPort;
    }

    public Date getLastOnlineDate() {
        return lastOnlineDate;
    }

    public void setLastOnlineDate(Date lastOnlineDate) {
        this.lastOnlineDate = lastOnlineDate;
    }
}
