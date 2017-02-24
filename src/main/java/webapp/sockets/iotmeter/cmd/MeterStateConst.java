package webapp.sockets.iotmeter.cmd;

/**
 * Created by Administrator on 2017/2/21.
 */
public class MeterStateConst {

    /**
     * 错误
     */
    public final int S_ERROR_MSK = 0xDC;

    //阀门状态
    public final int S_VALVE_MSK = 0x03;
    public final int S_VALVE_OPND = 0x00;
    public final int S_VALVE_CLSD = 0x01;
    public final int S_VALVE_ERR = 0x03;

    //6V电源状态
    public final int S_BAT_6V_MSK = 0x01 << 2;
    public final int S_BAT_6V_LO = 0x01 << 2;

    //阀门泄露
    public final int S_VALVE_LEAK_MSK = 0x01 << 3;
    public final int S_VALVE_LEAK = 0x01 << 3;

    //超过15天未用
    public final int S_UNUSD_OVER_15_DAYS_MSK = 0x01 << 4;
    public final int S_UNUSD_OVER_15_DAYS = 0x01 << 4;

    //3.6V电源状态
    public final int S_BAT_3_6V_MSK = 0x01 << 5;
    public final int S_BAT_3_6V_LO = 0x01 << 5;

    //强磁干扰
    public final int S_STRG_MAGIC_MSK = 0x01 << 6;
    public final int S_STRG_MAGIC = 0x01 << 6;

    //干簧管坏
    public final int S_RDSW_BAD_MSK = 0x01 << 7;
    public final int S_RDSW_BAD = 0x01 << 7;

    /// <summary>
    /// 阀门状态
    /// </summary>
    public enum STATE_VALVE
    {
        Open,  //1
        Close, //0
        Error  //2
    }

    /// <summary>
    /// 6V电源状态
    /// </summary>
    public enum STATE_POWER_6_V
    {
        Low,
        Ok
    }

    /// <summary>
    /// 是否超过15天未用
    /// </summary>
    public enum STATE_POWER_3_6_V
    {
        Low,
        Ok
    }

    /// <summary>
    /// 阀门泄露状态
    /// </summary>
    public enum STATE_VALVE_LEAK
    {
        LEAK,
        OK
    }

    /// <summary>
    /// 超过15天未用
    /// </summary>
    public enum STATE_UNUSD_OVER_15_DAYS
    {
        UNUSED,
        OK
    }

    /// <summary>
    /// 是否强磁干扰
    /// </summary>
    public enum STATE_STRG_MAGIC
    {
        STRG_MAGIC,
        OK
    }

    /// <summary>
    /// 是否干簧管坏
    /// </summary>
    public enum STATE_RDSW_BAD
    {
        BAD,
        OK
    }
}
