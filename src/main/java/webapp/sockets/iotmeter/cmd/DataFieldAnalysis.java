package webapp.sockets.iotmeter.cmd;

import webapp.sockets.iotmeter.cmd.bean.MeterInfo;
import webapp.sockets.iotmeter.util.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static webapp.sockets.iotmeter.cmd.bean.MeterInfo.PowerType.*;

/**
 * Created by Administrator on 2017/2/21.
 */
public class DataFieldAnalysis {
    private static DataFieldAnalysis dataFieldAnalysis;

    private DataFieldAnalysis() {

    }

    public static DataFieldAnalysis getInstantce() {
        if (dataFieldAnalysis == null) {
            dataFieldAnalysis = new DataFieldAnalysis();
        }
        return dataFieldAnalysis;
    }

    /**
     * 3046 命令数据域分析
     *
     * @param data
     * @return
     */
    public MeterInfo getMeterInfoByBytes(byte[] data) {
        MeterInfo meterInfo = new MeterInfo();

        System.out.println("data = [" + Tools.Bytes2HexString(data,data.length) + "]");

        int pos = 0;
        byte[] meterIdBytes = new byte[7];
        System.arraycopy(data, pos, meterIdBytes, 0, meterIdBytes.length);
        pos += meterIdBytes.length;
        meterInfo.setMeterId(Tools.Bytes2HexString(meterIdBytes, meterIdBytes.length));


        byte[] stateValvesBytes = new byte[1];
        System.arraycopy(data, pos, stateValvesBytes, 0, stateValvesBytes.length);
        pos += stateValvesBytes.length;
        if (stateValvesBytes[0] == 0) {
            meterInfo.setStateValve(MeterStateConst.STATE_VALVE.Close);
        } else if (stateValvesBytes[0] == 1) {
            meterInfo.setStateValve(MeterStateConst.STATE_VALVE.Open);
        } else {
            meterInfo.setStateValve(MeterStateConst.STATE_VALVE.Error);
        }

        byte[] rssiBytes = new byte[1];
        System.arraycopy(data, pos, rssiBytes, 0, rssiBytes.length);
        pos += rssiBytes.length;
        meterInfo.setRssi(rssiBytes[0] & 0xff);

        byte[] batteryTypeBytes = new byte[1];
        System.arraycopy(data, pos, batteryTypeBytes, 0, batteryTypeBytes.length);
        pos += batteryTypeBytes.length;
        switch (batteryTypeBytes[0]) {
            case 1:
                meterInfo.setPowerType(MainBattery);
                break;
            case 2:
                meterInfo.setPowerType(ViceBattery);
                break;
            case 3:
                meterInfo.setPowerType(MainPower);
                break;
            case 4:
                meterInfo.setPowerType(VicePower);
                break;
        }

        byte[] batteryVolBytes = new byte[2];
        System.arraycopy(data, pos, batteryVolBytes, 0, batteryVolBytes.length);
        pos += batteryVolBytes.length;
        float batteryVol = batteryVolBytes[0] / 100.0f + batteryVolBytes[1];
        meterInfo.setBatteryVol(batteryVol);

        byte[] detailStartDayBytes = new byte[4];
        System.arraycopy(data, pos, detailStartDayBytes, 0, detailStartDayBytes.length);
        pos += detailStartDayBytes.length;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(Tools.Bytes2HexString(detailStartDayBytes, detailStartDayBytes.length));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        meterInfo.setDetailStartDate(date);

        byte[] dataDetailCountBytes = new byte[1];
        System.arraycopy(data, pos, dataDetailCountBytes, 0, dataDetailCountBytes.length);
        pos += dataDetailCountBytes.length;

        int dayDataDetailItemLength = 12;
        byte[] dataDetailBytes = new byte[dayDataDetailItemLength * dataDetailCountBytes[0]];
        System.arraycopy(data, pos, dataDetailBytes, 0, dataDetailBytes.length);
        pos += dataDetailBytes.length;
        meterInfo.dataDetails = new ArrayList<>();
        for (int i = 0; i < (dataDetailCountBytes[0] & 0xff); i++) {
            byte[] item = new byte[dayDataDetailItemLength];
            System.arraycopy(dataDetailBytes, i * dayDataDetailItemLength, item, 0, item.length);

            MeterInfo.DataDetail dataDetail = new MeterInfo.DataDetail();
            byte[] flowBytes = new byte[6];
            System.arraycopy(item, 0, flowBytes, 0, flowBytes.length);
            dataDetail.setMeterValue(meterFlowBytesToDouble(flowBytes));

            byte[] flowBytes1 = new byte[6];
            System.arraycopy(item, 6, flowBytes1, 0, flowBytes1.length);
            dataDetail.setWorkConditions(meterFlowBytesToDouble(flowBytes1));

            meterInfo.dataDetails.add(dataDetail);
        }

        byte[] exceptionDetailCountBytes = new byte[1];
        System.arraycopy(data, pos, exceptionDetailCountBytes, 0, exceptionDetailCountBytes.length);
        pos += exceptionDetailCountBytes.length;

        int exceptionDetailItemLength = 11;
        byte[] exceptionDetailsBytes = new byte[exceptionDetailItemLength * exceptionDetailCountBytes[0]];
        System.arraycopy(data, pos, exceptionDetailsBytes, 0, exceptionDetailsBytes.length);
        pos += exceptionDetailsBytes.length;
        meterInfo.ExceptionDetails = new ArrayList<>();
        for (int i = 0; i < exceptionDetailCountBytes[0]; i++)
        {
            byte[] item = new byte[exceptionDetailItemLength];
            System.arraycopy(exceptionDetailsBytes, i * exceptionDetailItemLength, item, 0, item.length);

            MeterInfo.ExceptionDetail exceptionDetail = new MeterInfo.ExceptionDetail();
            byte[] dateTimeBytes = new byte[7];
            System.arraycopy(item, 0, dateTimeBytes, 0, dateTimeBytes.length);
            String dateTimeStr = Tools.Bytes2HexString(dateTimeBytes,dateTimeBytes.length);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                exceptionDetail.OccurrenceDateTime = simpleDateFormat1.parse(dateTimeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            byte[] exceptionBytes = new byte[4];
            System.arraycopy(item, 7, exceptionBytes, 0, exceptionBytes.length);
            String str = Tools.Bytes2HexString(new byte[] {exceptionBytes[2], exceptionBytes[3]},2);
            exceptionDetail.ExceptionType = Integer.parseInt(str);

            meterInfo.ExceptionDetails.add(exceptionDetail);
        }

        return meterInfo;
    }


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
    public static float meterFlowBytesToDouble(byte[] flowBytes) {
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
