package webapp.sockets.iotmeter.db.vo;

import java.util.Date;

/**
 * Created by Administrator on 2016/11/14.
 */
public class MeterExceptionVo {
    /**id*/
    private String id;
    /**设备编号*/
    private String meterId;
    /**异常代码*/
    private float exceptionId;
    /**异常发生时间*/
    private Date happenDate;
    /**创建时间*/
    private Date createDate;
}
