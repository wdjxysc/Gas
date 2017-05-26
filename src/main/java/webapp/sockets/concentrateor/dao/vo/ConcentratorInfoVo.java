package webapp.sockets.concentrateor.dao.vo;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ConcentratorInfoVo {
    /** 数据id */
    private String id;



    /** 集中器id */
    private String concentratorId;

    /** 表socket ip */
    private String clientIp;

    /** 表socket端口号 */
    private int clientPort;

    /** 最后登录日期 */
    private String lastOnlineDate;

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

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public String getLastOnlineDate() {
        return lastOnlineDate;
    }

    public void setLastOnlineDate(String lastOnlineDate) {
        this.lastOnlineDate = lastOnlineDate;
    }
}
