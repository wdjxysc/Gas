package webapp.sockets.iotmeter.cmd.bean;

import webapp.sockets.iotmeter.cmd.MeterStateConst;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/21.
 */
public class MeterInfo implements Serializable{
    /**
     * 表id
     */
    public String meterId;

    public MeterStateConst.STATE_VALVE stateValve;

    public int rssi;

    public enum PowerType
    {
        MainBattery, //1
        ViceBattery, //2
        MainPower,   //3
        VicePower    //4
    }

    public PowerType powerType;

    public float batteryVol;

    public Date detailStartDate;

    public static class DataDetail{
        public float MeterValue;

        public float getMeterValue() {
            return MeterValue;
        }

        public void setMeterValue(float meterValue) {
            MeterValue = meterValue;
        }

        public float getWorkConditions() {
            return WorkConditions;
        }

        public void setWorkConditions(float workConditions) {
            WorkConditions = workConditions;
        }

        public float WorkConditions;
    }

    public ArrayList<DataDetail> dataDetails;

    public static class ExceptionDetail
    {
        public Date OccurrenceDateTime;

        public Date getOccurrenceDateTime() {
            return OccurrenceDateTime;
        }

        public void setOccurrenceDateTime(Date occurrenceDateTime) {
            OccurrenceDateTime = occurrenceDateTime;
        }

        public int getExceptionType() {
            return ExceptionType;
        }

        public void setExceptionType(int exceptionType) {
            ExceptionType = exceptionType;
        }

        public int ExceptionType;
    }

    public ArrayList<ExceptionDetail> ExceptionDetails;

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public MeterStateConst.STATE_VALVE getStateValve() {
        return stateValve;
    }

    public void setStateValve(MeterStateConst.STATE_VALVE stateValve) {
        this.stateValve = stateValve;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public PowerType getPowerType() {
        return powerType;
    }

    public void setPowerType(PowerType powerType) {
        this.powerType = powerType;
    }

    public float getBatteryVol() {
        return batteryVol;
    }

    public void setBatteryVol(float batteryVol) {
        this.batteryVol = batteryVol;
    }

    public Date getDetailStartDate() {
        return detailStartDate;
    }

    public void setDetailStartDate(Date detailStartDate) {
        this.detailStartDate = detailStartDate;
    }

    public ArrayList<DataDetail> getDataDetails() {
        return dataDetails;
    }

    public void setDataDetails(ArrayList<DataDetail> dataDetails) {
        this.dataDetails = dataDetails;
    }

    public ArrayList<ExceptionDetail> getExceptionDetails() {
        return ExceptionDetails;
    }

    public void setExceptionDetails(ArrayList<ExceptionDetail> exceptionDetails) {
        ExceptionDetails = exceptionDetails;
    }


}
