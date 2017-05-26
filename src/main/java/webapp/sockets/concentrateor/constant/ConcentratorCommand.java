package webapp.sockets.concentrateor.constant;

import java.io.Serializable;

/**
 * 命令样本
 *
 * @author Administrator
 */
public class ConcentratorCommand implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String cmd_logic = "开始符|报文长度|功能码|传送方向|请求响应标识|从站编号|报文ID|数据域长度|数据域|CRC16校验码|结束符";

    /**
     * 2001
     */
    public static final String cmd2001 = "2001";
    public static final String cmd2001_name = "(主站向从站)主站通信测试：主站向从站发送通信测试指令";



    /**
     * 2002
     */
    public static final String cmd2002 = "2002";
    public static final String cmd2002_name = "(从站向主站)集中器测试：从站向主站发送通信测试指令";

    /**
     * 2003
     */
    public static final String cmd2003 = "2003";
    public static final String cmd2003_name = "(从站向主站)心跳包：从站向主站发送心跳包，主站返回报文目的是方便从站确定通讯故障及时切换通讯通道";

    /**
     * 2004
     */
    public static final String cmd2004 = "2004";
    public static final String cmd2004_name = "(主站向从站)注册包：从站和主站建立连接后，发送注册包";

    /**
     * 2011
     */
    public static final String cmd2011 = "2011";
    public static final String cmd2011_name = "(主站向从站)集中器心跳周期设置:主站向从站发送心跳周期设置指令";

    /**
     * 2012
     */
    public static final String cmd2012 = "2012";
    public static final String cmd2012_name = "(主站向从站)集中器心跳周期查询：主站向从站发送心跳周期查询指令";

    /**
     * 2013
     */
    public static final String cmd2013 = "2013";
    public static final String cmd2013_name = "(主站向从站)集中器集抄时间设置：主站向从站发送集抄时间设置指令";

    /**
     * 2014
     */
    public static final String cmd2014 = "2014";
    public static final String cmd2014_name = "(主站向从站)集中器集抄时间查询：主站向从站发送集抄时间查询指令";

    /**
     * 2015
     */
    public static final String cmd2015 = "2015";
    public static final String cmd2015_name = "(主站向从站)集中器时钟设置：主站向从站发送时钟设置指令";

    /**
     * 2016
     */
    public static final String cmd2016 = "2016";
    public static final String cmd2016_name = "(主站向从站)集中器时钟查询：主站向从站发送时钟查询指令";

    /**
     * 2021
     */
    public static final String cmd2021 = "2021";
    public static final String cmd2021_name = "(主站向从站)表具唤醒周期设置：主站向从站发送表具唤醒周期设置指令";

    /**
     * 2022
     */
    public static final String cmd2022 = "2022";
    public static final String cmd2022_name = "(主站向从站)表具唤醒周期查询：主站向从站发送表具唤醒周期查询指令";

    /**
     * 2023
     */
    public static final String cmd2023 = "2023";
    public static final String cmd2023_name = "(主站向从站)表具时钟设置：主站向从站发送表具时钟设置指令";
    /**
     * 2024
     */
    public static final String cmd2024 = "2024";
    public static final String cmd2024_name = "(主站向从站)表具时钟查询：主站向从站发送表具时钟查询指令";

    /**
     * 2031
     */
    public static final String cmd2031 = "2031";
    public static final String cmd2031_name = "(主站向从站)集中器增加下级采集器";

    /**
     * 2032
     */
    public static final String cmd2032 = "2032";
    public static final String cmd2032_name = "(主站向从站)集中器删除下级采集器";

    /**
     * 2033
     */
    public static final String cmd2033 = "2033";
    public static final String cmd2033_name = "(主站向从站)增加下级智能表";

    /**
     * 2034
     */
    public static final String cmd2034 = "2034";
    public static final String cmd2034_name = "(主站向从站)删除下级智能表";



    /**
     * 2035
     */
    public static final String cmd2035 = "2035";
    public static final String cmd2035_name = "(主站向从站)集中器下级采集器查询";

    /**
     * 2036
     */
    public static final String cmd2036 = "2036";
    public static final String cmd2036_name = "(主站向从站)下级表具查询";

    /**
     * 2041
     */
    public static final String cmd2041 = "2041";
    public static final String cmd2041_name = "(主站向从站)获取集抄数据";

    /**
     * 2042
     */
    public static final String cmd2042 = "2042";
    public static final String cmd2042_name = "(主站向从站)单表实时抄表";

    /**
     * 2043
     */
    public static final String cmd2043 = "2043";
    public static final String cmd2043_name = "(主站向从站)单表月用量历史查询";

    /**
     * 2044
     */
    public static final String cmd2044 = "2044";
    public static final String cmd2044_name = "(主站向从站)单表日用量历史查询";

    /**
     * 2051
     */
    public static final String cmd2051 = "2051";
    public static final String cmd2051_name = "(主站向从站)单表开阀";

    /**
     * 2052
     */
    public static final String cmd2052 = "2052";
    public static final String cmd2052_name = "(主站向从站)单表关阀";

    /**
     * 2053
     */
    public static final String cmd2053 = "2053";
    public static final String cmd2053_name = "(主站向从站)阀门状态查询";

    /**
     * 2061
     */
    public static final String cmd2061 = "2061";
    public static final String cmd2061_name = "(从站向主站)集中器异常事件上传";

    /**
     * 2062
     */
    public static final String cmd2062 = "2062";
    public static final String cmd2062_name = "(从站向主站)表异常事件上传";


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
