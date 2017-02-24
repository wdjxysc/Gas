package webapp.sockets.iotmeter.constant;

import java.io.Serializable;

/**
 * 命令样本
 *
 * @author Administrator
 */
public class IotMeterCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String cmd_logic = "开始符|报文长度|功能码|传送方向|请求响应标识|从站编号|报文ID|数据域长度|数据域|CRC16校验码|结束符";

    /**
     * 3003
     */
    public static final String cmd3003 = "3003";
    public static final String cmd3003_name = "(从站向主站)发送心跳包，主站返回报文目的是方便从站确定通讯故障及时切换通讯通道";
    /**
     * 从站上行发送
     */
    public static final String cmd3003_up_ask = "";
    /**
     * 主站下行回复
     */
    public static final String cmd3003_down_reply = "";


    /**
     * 3004
     */
    //68 21 00 30 04 00 01 23 05 17 09 00 00 01 23 05 17 09 00 00 01 07 00 23 05 17 09 00 00 01 36 48 16
    public static final String cmd3004 = "3004";
    public static final String cmd3004_name = "(从站向主站)建立连接后，发送注册包，注册包每次登录时发送";
    public static final String cmd3004_up_ask = "";
    public static final String cmd3004_down_reply = "";

    /**
     * 3021
     */
    public static final String cmd3021 = "3021";
    public static final String cmd3021_name = "(主站向从站)发送心跳周期设置指令";
    /**
     * 主站下行发送
     */
    public static final String cmd3021_down_ask = "68|0021|3021|00|00|20011607000001|16071700000001|0002|003C|0000|16";
    /**
     * 从站上行回复
     */
    public static final String cmd3021_up_reply = "68|001c|3021|00|01|20011607000001|16071700000001|0002|003c|2cba|16";

    /**
     * 3022
     */
    public static final String cmd3022 = "3022";
    public static final String cmd3022_name = "(主站向从站)发送心跳周期查询指令";
    public static final String cmd3022_down_ask = "68|0021|3022|00|00|20011607000001|16071700000001|0000|0000|16";
    public static final String cmd3022_up_reply = "68|001a|3022|00|01|20011607000001|16071700000001|0000|8e4a|16";


    /**
     * 3023
     */
    public static final String cmd3023 = "3023";
    public static final String cmd3023_name = "(主站向从站)发送上报周期设置指令";
    public static final String cmd3023_down_ask = "";
    public static final String cmd3023_up_reply = "";

    /**
     * 3024
     */
    public static final String cmd3024 = "3024";
    public static final String cmd3024_name = "(主站向从站)发送上报周期查询指令";
    public static final String cmd3024_down_ask = "";
    public static final String cmd3024_up_reply = "";

    /**
     * 3025
     */
    public static final String cmd3025 = "3025";
    public static final String cmd3025_name = "(从站向主站)从站自醒时，从站向主站发送表具对时指令";
    public static final String cmd3025_up_ask = "";
    public static final String cmd3025_down_reply = "";

    /**
     * 3026
     */
    public static final String cmd3026 = "3026";
    public static final String cmd3026_name = "(主站向从站)主站向从站发送时钟查询指令";
    public static final String cmd3026_down_ask = "";
    public static final String cmd3026_up_reply = "";

    /**
     * 3027
     */
    public static final String cmd3027 = "3027";
    public static final String cmd3027_name = "(主站向从站)发送主站IP地址设置指令";
    public static final String cmd3027_down_ask = "68|0021|3027|00|00|20011607000001|16071700000001|0009|192168001005|008080|0000|16";
    public static final String cmd3027_up_reply = "68|0023|3027|00|01|20011607000001|16071700000001|0009|192168001005|008080|56e2|16";

    /**
     * 3028
     */
    public static final String cmd3028 = "3028";
    public static final String cmd3028_name = "(主站向从站)发送主站IP地址查询指令";
    public static final String cmd3028_down_ask = "";
    public static final String cmd3028_up_reply = "";

    /**
     * 3029
     */
    public static final String cmd3029 = "3029";
    public static final String cmd3029_name = "(主站向从站)主站向从站发送自醒上报时间设置指令";
    public static final String cmd3029_down_ask = "";
    public static final String cmd3029_up_reply = "";

    /**
     * 3020
     */
    public static final String cmd3020 = "3020";
    public static final String cmd3020_name = "(主站向从站)主站向从站发送自醒上报时间查询指令";
    public static final String cmd3020_down_ask = "";
    public static final String cmd3020_up_reply = "";

    /**
     * 3042
     */
    public static final String cmd3042 = "3042";
    public static final String cmd3042_name = "(主站向从站)主站向从站发送单表抄表指令";
    public static final String cmd3042_down_ask = "";
    public static final String cmd3042_up_reply = "";

    /**
     * 3043
     */
    public static final String cmd3043 = "3043";
    public static final String cmd3043_name = "(从站向主站)从站自醒后，从站向主站发送抄表数据，从站将当天0时0分的抄表信息上传主站";
    public static final String cmd3043_up_ask = "";
    public static final String cmd3043_down_reply = "";

    /**
     * 3044
     */
    public static final String cmd3044 = "3044";
    public static final String cmd3044_name = "(主站向从站)主站向从站发送单表历史数据查询指令，从站将该表最近60天抄表信息上传主站，响应报文返回时采用分拆成多个报文帧返回";
    public static final String cmd3044_down_ask = "";
    public static final String cmd3044_up_reply = "";

    /**
     * 3045
     */
    public static final String cmd3045 = "3045";
    public static final String cmd3045_name = "(主站向从站)主站向从站发送单表历史数据查询指令，获取表具月用量历史数据，按保存10年计算120条记录，整个包为2K，响应报文采用分拆多个报文帧返回";
    public static final String cmd3045_down_ask = "";
    public static final String cmd3045_up_reply = "";

    /**
     * 3046
     */
    public static final String cmd3046 = "3046";
    public static final String cmd3046_name = "(从站向主站)非实时在线物理表，从站自醒后，从站向主站发送抄表数据，从站将当天0时0分的抄表信息上传主站。";
    public static final String cmd3046_up_ask = "";
    public static final String cmd3046_down_reply = "";

    /**
     * 3051
     */
    public static final String cmd3051 = "3051";
    public static final String cmd3051_name = "(主站向从站)主站向从站发送单表开阀指令";
    public static final String cmd3051_down_ask = "";
    public static final String cmd3051_up_reply = "";

    /**
     * 3052
     */
    public static final String cmd3052 = "3052";
    public static final String cmd3052_name = "(主站向从站)主站向从站发送单表关阀指令";
    public static final String cmd3052_down_ask = "";
    public static final String cmd3052_up_reply = "";

    /**
     * 3062
     */
    public static final String cmd3062 = "3062";
    public static final String cmd3062_name = "(从站向主站)当设备出现异常时，主动上报当前异常信息";
    public static final String cmd3062_up_ask = "";
    public static final String cmd3062_down_reply = "";

    /**
     * 3065
     */
    public static final String cmd3065 = "3065";
    public static final String cmd3065_name = "(主站向从站)主站向从站发出故障信息清除指令，从站收到清除指令后，更新从站的工作状态";
    public static final String cmd3065_down_ask = "";
    public static final String cmd3065_up_reply = "";

	/*2015 2016 2018 2019 为出厂设置*/
    /**
     * 2015
     */
    public static final String cmd2015 = "2015";
    public static final String cmd2015_name = "(主站向从站)物联网表具时钟设置";
    public static final String cmd2015_down_ask = "";
    public static final String cmd2015_up_reply = "";

    /**
     * 2016
     */
    public static final String cmd2016 = "2016";
    public static final String cmd2016_name = "(主站向从站)物联网表具底度设置";
    public static final String cmd2016_down_ask = "";
    public static final String cmd2016_up_reply = "";

    /**
     * 2018
     */
    public static final String cmd2018 = "2018";
    public static final String cmd2018_name = "(主站向从站)物联网表具地址(表号)设置";
    public static final String cmd2018_down_ask = "";
    public static final String cmd2018_up_reply = "";

    /**
     * 2019
     */
    public static final String cmd2019 = "2019";
    public static final String cmd2019_name = "(主站向从站)物联网表具出厂启用";
    public static final String cmd2019_down_ask = "";
    public static final String cmd2019_up_reply = "";


    //----------------------------命令格式------------------------------------

    public static final byte[] CommunicateSendStoC = {(byte) 0x68, (byte) 0xff, (byte) 0x00, (byte) 0x20, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x05, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x00, (byte) 0x16};
    public static final byte[] CommunicateBackCtoS = {(byte) 0x68, (byte) 0xff, (byte) 0x00, (byte) 0x20, (byte) 0x01,
            (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x23, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x16};

    //------------------------------- 命令部分----------------------------------

    /***起始符*/
    public static final byte beginCode = 0x68;

    /***传送方向 1：从站发出*/
    public static final byte conToMs = 0x01;

    /***传送方向 0：主站发出*/
    public static final byte msToCon = 0x00;

    /*** 请求响应标识 0：请求*/
    public static final byte requiry = 0x00;

    /*** 请求响应标识 1：响应*/
    public static final byte response = 0x01;
}
