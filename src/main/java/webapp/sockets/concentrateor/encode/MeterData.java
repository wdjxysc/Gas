package webapp.sockets.concentrateor.encode;

import webapp.sockets.util.Tools;

/**
 * Created by Administrator on 2017/5/23.
 */
public class MeterData {

    /// <summary>
    /// 流量转换为需要的格式  四字节整数部分，两字节小数部分，三位小数
    /// </summary>
    /// <param name="flow"></param>
    /// <returns></returns>
    public static byte[] meterFlowToBytes(float flow) {
            /*--2byte小数--/--4byte整数--*/  /*HEX*/ /*三位小数*/

        int flowTotal = (int) (flow * 1000);
        int flowInt = flowTotal / 1000;
        int flowDec = flowTotal % 1000;

        String str = Tools.Bytes2HexString(Tools.intToByte(flowDec), Tools.intToByte(flowDec).length).substring(0, 4) +
                Tools.Bytes2HexString(Tools.intToByte(flowInt), Tools.intToByte(flowInt).length);
        return Tools.HexString2Bytes(str);
    }

    /// <summary>
    /// 流量转换为需要的格式  四字节整数部分，两字节小数部分，三位小数
    /// </summary>
    /// <param name="flowBytes"></param>
    /// <returns></returns>
    public static float meterFlowBytesToFloat(byte[] flowBytes) {
            /*--2byte小数--/--4byte整数--*/
            /*HEX*/
            /*三位小数*/
        byte[] intPartBytes = new byte[4];
        System.arraycopy(flowBytes, 2, intPartBytes, 0, intPartBytes.length);
        int intPart = (intPartBytes[0]&0xff) + ((intPartBytes[1]&0xff) << 8) + ((intPartBytes[2]&0xff) << 16) + ((intPartBytes[3]&0xff) << 24);

        byte[] decPartBytes = new byte[2];
        System.arraycopy(flowBytes, 0, decPartBytes, 0, decPartBytes.length);
        int decPart = (decPartBytes[0]&0xff) + ((decPartBytes[1]&0xff) << 8);

        return intPart + ((float) decPart) / 1000.0f;
    }
}
