package sockets.iotmeter.util;



import sockets.iotmeter.protocol.Protocol;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 *  时间描述类
 * @author Administrator
 *
 */
public class TimeTag implements Cloneable,Serializable{
	private static final long serialVersionUID = 1L;

	/* 时间类型 */
	public static final byte TIMETYPE_A = 5;

	public static final byte TIMETYPE_B = 7;

	/* 时间单位 */
	public static final byte TIMEUNIT_SECOND = 0;

	public static final byte TIMEUNIT_MINUTE = 1;

	public static final byte TIMEUNIT_HOUR = 2;

	public static final byte TIMEUNIT_DAY = 3;

	public static final byte TIMEUNIT_MONTH = 4;

	public static final byte TIMEUNIT_YEAR = 5;

	public static final byte TIMEUNIT_WEEK = 6;

	public static final byte TIMEUNIT_MILLISECOND = 7;
	
	public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

	public int year = 0, month = 0, day = 0, hour = 0, minute = 0, second = 0,
			week = 0;

	public long millisecond = 0;

	private Calendar calendar = Calendar.getInstance();

	private Protocol protocol = Protocol.getInstance();

	public TimeTag() {
	}
	
	/**
	 * 构造函数
	 * @param millis
	 */
	public TimeTag(long millis) {
		setTime(millis);
	}
	
	/**
	 * 构造函数
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 */
	public TimeTag(int year, int month, int day, int hour, int minute, int second) {
		this.year = year;
		if (this.year < 2000)
			this.year = this.year + 2000;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		setCalendar();
	}

	/**
	 * 构造函数
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param ms
	 */
	public TimeTag(int year, int month, int day, int hour, int minute,
				   int second, long ms) {
		this.year = year;
		if (this.year < 2000)
			this.year = this.year + 2000;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.millisecond = ms;
		setCalendar();
	}

	/**
	 * 构造函数
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param week
	 */
	public TimeTag(int year, int month, int day, int hour, int minute,
				   int second, int week) {
		this.year = year;
		if (this.year < 2000)
			this.year = this.year + 2000;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.week = week;
		setCalendar();
	}

	/**
	 * 构造函数
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param week
	 * @param ms
	 */
	public TimeTag(int year, int month, int day, int hour, int minute,
				   int second, int week, long ms) {
		this.year = year;
		if (this.year < 2000)
			this.year = this.year + 2000;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.week = week;
		this.millisecond = ms;
		setCalendar();
	}

	/**
	 * 判断时间是否有效
	 * 
	 * @return
	 */
	public boolean isValid() {
		if (year < 2000 || month > 12 || month < 1 || day < 1 || day > 31
				|| hour < 0 || hour > 25 || minute < 0 || minute > 59 || second < 0
				|| second > 59 || millisecond > 1000) {
			return false;
		}
		return true;
	}

	/**
	 * 设置时间
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 */
	public void setTime(int year, int month, int day, int hour, int minute) {
		if (year < 2000)
			this.year = year + 2000;
		else
			this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = 0;
		setCalendar();
	}

	/**
	 * 设置时间
	 * @param timeTag
	 */
	public void setTime(TimeTag timeTag) {
		this.year = timeTag.year;
		this.month = timeTag.month;
		this.day = timeTag.day;
		this.hour = timeTag.hour;
		this.minute = timeTag.minute;
		setCalendar();
	}

	public void setTime(Calendar calendar) {
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DATE);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
		setCalendar();
	}

	public void setTime(long millis) {
		calendar.setTimeInMillis(millis);
		calendar.set(Calendar.MILLISECOND, 0);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DATE);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
	}
	
	public void setTimeWithMillSec(long millis){
		calendar.setTimeInMillis(millis);
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DATE);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
		millisecond = calendar.get(Calendar.MILLISECOND);
	}

	public void setCalendar() {
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	public void setTimeAddMin(Calendar calendar) {
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		day = calendar.get(Calendar.DATE);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE) + 1;
		second = calendar.get(Calendar.SECOND);
		setCalendar();
	}

	public long getMillis() {
		return calendar.getTimeInMillis();
	}
	
	public Calendar getCalendar(){
		return this.calendar;
	}

	/**
	 * 返回BCD格式的数组
	 */
	public byte[] toBcd() {
		byte[] bcdTime = new byte[] { protocol.hexToBcd((byte) (year>=2000?year - 2000:year), 0),
				protocol.hexToBcd((byte) month, 0), protocol.hexToBcd((byte) day, 0),
				protocol.hexToBcd((byte) hour, 0), protocol.hexToBcd((byte) minute, 0) };
		return bcdTime;
	}

	/**
	 * 转换为IEEE格式时间，从1970-1-1 0:0:0开始计算的以秒为单位的时间
	 * @return 4字节时间数组
	 */
	public byte[] toIEEETime(){
		return protocol.intToHex(getMillis()/1000);
	}
	
	/**
	 * 返回BCD格式的数组,精确至分钟
	 */
	public byte[] toMiddleBcd() {
		byte[] bcdTime = new byte[] { protocol.hexToBcd((byte) (year - 2000), 0),
				protocol.hexToBcd((byte) month, 0), protocol.hexToBcd((byte) day, 0),
				protocol.hexToBcd((byte) hour, 0), protocol.hexToBcd((byte) minute, 0),
				protocol.hexToBcd((byte) second, 0) };
		return bcdTime;
	}

	/**
	 * 返回BCD格式的数组
	 */
	public byte[] toBcdWithMillisecond() {
		byte[] bcdTime = new byte[] { protocol.hexToBcd((byte) (year - 2000), 0),
				protocol.hexToBcd((byte) month, 0), protocol.hexToBcd((byte) day, 0),
				protocol.hexToBcd((byte) hour, 0), protocol.hexToBcd((byte) minute, 0),
				protocol.hexToBcd((byte) second, 0),
				protocol.hexToBcd((byte) millisecond, 0) };
		return bcdTime;
	}

	public byte[] toBigBcd() {
		byte[] bcdTime = new byte[] { protocol.hexToBcd((byte) (year - 2000), 0),
				protocol.hexToBcd((byte) month, 0), protocol.hexToBcd((byte) day, 0),
				protocol.hexToBcd((byte) hour, 0), protocol.hexToBcd((byte) minute, 0),
				protocol.hexToBcd((byte) minute, 0), protocol.hexToBcd((byte) week, 0) };
		return bcdTime;
	}

	public byte[] toHexTime(){
		byte[] time = new byte[]{0,0,(byte)month, (byte)day, (byte)hour, (byte)minute};
		System.arraycopy(protocol.reverseByteOrder(protocol.shortToHex(year)),0,time,0,2);
		return time;
	}
	
	/**
	 * 返回102时间信息A
	 */
	public byte[] toTimeA() {
		byte[] time = new byte[TimeTag.TIMETYPE_A];
		time[0] = (byte) (this.minute & 0x3F);
		time[1] = (byte) (this.hour & 0x1F);
		time[2] = (byte) (this.day & 0x1F);
		time[3] = (byte) (this.month & 0x0F);
		time[4] = (byte) (this.year & 0x0F);
		return time;
	}
	
	/**
	 * 返回102时间信息B
	 */
	public byte[] toTimeB() {
		byte[] time = new byte[TimeTag.TIMETYPE_B];
		byte[] mills = protocol.shortToHex((int) millisecond);
		time[0] = (byte) (mills[0] & 0xFF);
		time[1] = (byte) (mills[1] & 0x03 + second & 0xFC);
		time[2] = (byte) (this.minute & 0x3F);
		time[3] = (byte) (this.hour & 0x1F);
		time[4] = (byte) (this.day & 0x1F);
		time[5] = (byte) (this.month & 0x0F);
		time[6] = (byte) (this.year & 0x0F);
		return time;
	}
	
	/**
	 * 获取数组
	 * 
	 * @param addValue
	 * @return
	 */
	public byte[] getByteTime(byte addValue) {
		byte[] byteTime = new byte[] { (byte) (year - 2000 + addValue),
				(byte) (month + addValue), (byte) (day + addValue),
				(byte) (hour + addValue), (byte) (minute + addValue) };
		return byteTime;
	}
	
	/**
	 * 返回BCD格式的数组,精确至分钟，以逆序的形式组织
	 */
	public byte[] getRervseBinTime() {
		byte[] binTime = new byte[] { (byte) minute, (byte) hour, (byte) day, (byte) month, (byte) (year - 2000) };
		return binTime;
	}
	
	/**
	 * 返回当月日期最大值
	 */
	public int getMaxDay() {
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 判断两个时间是否相等
	 * 
	 * @param otherTime
	 * @param minusValue
	 * @return
	 */
	public boolean equalTime(byte[] otherTime, byte minusValue) {
		if (otherTime == null || otherTime.length != 5)
			return false;
		if (year != otherTime[0] + 2000 - minusValue)
			return false;
		if (month != otherTime[1] - minusValue)
			return false;
		if (day != otherTime[2] - minusValue)
			return false;
		if (hour != otherTime[3] - minusValue)
			return false;
		if (minute != otherTime[4] - minusValue)
			return false;

		return true;
	}

	public int compare(TimeTag other) {
		if (year < other.year) {
			return -1;
		} else if (year > other.year) {
			return 1;
		} else if (year == other.year) {
			if (month < other.month) {
				return -1;
			} else if (month > other.month) {
				return 1;
			} else if (month == other.month) {
				if (day < other.day) {
					return -1;
				} else if (day > other.day) {
					return 1;
				} else if (day == other.day) {
					if (hour < other.hour) {
						return -1;
					} else if (hour > other.hour) {
						return 1;
					} else if (hour == other.hour) {
						if (minute < other.minute) {
							return -1;
						} else if (minute > other.minute) {
							return 1;
						} else if (minute == other.minute) {
							return 0;
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 比较两个时间前后关系
	 */
	public static int compare(TimeTag timeTag1, TimeTag timeTag2) {
		if (timeTag1.year < timeTag2.year) {
			return -1;
		} else if (timeTag1.year > timeTag2.year) {
			return 1;
		} else if (timeTag1.year == timeTag2.year) {
			if (timeTag1.month < timeTag2.month) {
				return -1;
			} else if (timeTag1.month > timeTag2.month) {
				return 1;
			} else if (timeTag1.month == timeTag2.month) {
				if (timeTag1.day < timeTag2.day) {
					return -1;
				} else if (timeTag1.day > timeTag2.day) {
					return 1;
				} else if (timeTag1.day == timeTag2.day) {
					if (timeTag1.hour < timeTag2.hour) {
						return -1;
					} else if (timeTag1.hour > timeTag2.hour) {
						return 1;
					} else if (timeTag1.hour == timeTag2.hour) {
						if (timeTag1.minute < timeTag2.minute) {
							return -1;
						} else if (timeTag1.minute > timeTag2.minute) {
							return 1;
						} else if (timeTag1.minute == timeTag2.minute) {
							return 0;
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 按位对比两个时间的差值
	 */
	public static int compareTime(TimeTag timeTag, TimeTag contrastTime, int field) {
		int ret = 0;
		switch (field) {
		case TimeTag.TIMEUNIT_YEAR: {
			ret = timeTag.year - contrastTime.year;
			break;
		}
		case TimeTag.TIMEUNIT_MONTH: {
			int invalYear = timeTag.year - contrastTime.year;
			ret = invalYear * 12;
			ret += timeTag.month - contrastTime.month;
			break;
		}
		case TimeTag.TIMEUNIT_DAY: {
			ret = (int) ((timeTag.getMillis() - contrastTime.getMillis()) / (24 * 60 * 60 * 1000));
			break;
		}
		case TimeTag.TIMEUNIT_HOUR: {
			ret = (int) ((timeTag.getMillis() - contrastTime.getMillis()) / (60 * 60 * 1000));
			break;
		}
		case TimeTag.TIMEUNIT_MINUTE: {
			ret = (int) ((timeTag.getMillis() - contrastTime.getMillis()) / (60 * 1000));
			break;
		}
		}
		return ret;
	}

	/**
	 * 增加
	 * 
	 * @param field
	 * @param amount
	 */
	public void add(int field, int amount) {
		switch (field) {
		case TIMEUNIT_YEAR: {
			calendar.add(Calendar.YEAR, amount);
			break;
		}
		case TIMEUNIT_MONTH: {
			calendar.add(Calendar.MONTH, amount);
			break;
		}
		case TIMEUNIT_DAY: {
			calendar.add(Calendar.DATE, amount);
			break;
		}
		case TIMEUNIT_HOUR: {
			calendar.add(Calendar.HOUR_OF_DAY, amount);
			break;
		}
		case TIMEUNIT_MINUTE: {
			calendar.add(Calendar.MINUTE, amount);
			break;
		}
		}
		setTime(calendar.getTimeInMillis());
	}

	/**
	 * 返回时间域描述
	 * 
	 * @param field
	 * @return
	 */
	public static String getFieldDesc(int field) {
		switch (field) {
		case TIMEUNIT_MILLISECOND: {
			return "millisecond";
		}
		case TIMEUNIT_SECOND: {
			return "second";
		}
		case TIMEUNIT_MINUTE: {
			return "";
		}
		case TIMEUNIT_HOUR: {
			return "minute";
		}
		case TIMEUNIT_DAY: {
			return "day";
		}
		case TIMEUNIT_MONTH: {
			return "month";
		}
		case TIMEUNIT_YEAR: {
			return "year";
		}
		}
		return "";
	}

	/**
	 * 返回周几
	 * 
	 * @return
	 */
	public int getWeek() {
		//return calendar.get(Calendar.WEEK_OF_MONTH);
		int week = calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(week<=0)
			week = week+7;
		return week;
	}
	
	public long getMillisTime() {
		return calendar.getTimeInMillis();
	}

	public Calendar getTime() {
		return calendar;
	}

	/**
	 * 将时标根据采集的周期单位格式化
	 */
	public void formatByGatherUnit(int gatherUnit) {
		switch (gatherUnit) {
		case TIMEUNIT_SECOND: {
			break;
		}
		case TIMEUNIT_MINUTE: {
			second = 0;
			break;
		}
		case TIMEUNIT_HOUR: {
			minute = 0;
			second = 0;
			break;
		}
		case TIMEUNIT_DAY: {
			hour = 0;
			minute = 0;
			second = 0;
			break;
		}
		case TIMEUNIT_MONTH: {
			day = 1;
			hour = 0;
			minute = 0;
			second = 0;
			break;
		}
		case TIMEUNIT_YEAR: {
			month = 1;
			day = 0;
			hour = 0;
			minute = 0;
			second = 0;
			break;
		}
		}

		this.setCalendar();
	}

	/**
	 * 返回时间域数值
	 */
	public int get(int field) {
		switch (field) {
		case TIMEUNIT_SECOND: {
			return second;
		}
		case TIMEUNIT_MINUTE: {
			return minute;
		}
		case TIMEUNIT_HOUR: {
			return hour;
		}
		case TIMEUNIT_DAY: {
			return day;
		}
		case TIMEUNIT_MONTH: {
			return month;
		}
		case TIMEUNIT_YEAR: {
			return year;
		}
		}
		return -1;
	}

	/**
	 * 调整时间A
	 * 
	 * @param timeA
	 * @return
	 */
	private byte[] adjustTimeA(byte[] timeA) {
		if ((timeA == null) || timeA.length != TimeTag.TIMETYPE_A)
			return null;
		byte[] time = new byte[TimeTag.TIMETYPE_A];
		time[0] = (byte) (timeA[0] & 0x3F);
		time[1] = (byte) (timeA[1] & 0x1F);
		time[2] = (byte) (timeA[2] & 0x1F);
		time[3] = (byte) (timeA[3] & 0x0F);
		time[4] = (byte) (timeA[4] & 0x0F);
		return time;
	}

	/**
	 * 调整时间B
	 * 
	 * @param timeB
	 * @return
	 */
	private byte[] adjustTimeB(byte[] timeB) {
		if ((timeB == null) || timeB.length != TimeTag.TIMETYPE_B)
			return null;
		byte[] time = new byte[TimeTag.TIMETYPE_B];
		time[0] = timeB[0];
		time[1] = timeB[1];
		time[2] = (byte) (timeB[2] & 0x3F);
		time[3] = (byte) (timeB[3] & 0x1F);
		time[4] = (byte) (timeB[4] & 0x1F);
		time[5] = (byte) (timeB[5] & 0x0F);
		time[6] = (byte) (timeB[6] & 0x0F);
		return time;
	}
	
	/**
	 * 
	 * @param time
	 *          A类或B类时间
	 * @param timeType
	 *          ＝5表示A类时间，＝7表示B类时间
	 * @return
	 */
	public int compare(byte[] time, int timeType) {
		switch (timeType) {
		case TimeTag.TIMETYPE_A: {
			byte[] timeA = toTimeA();
			byte[] adjtimeA = adjustTimeA(time);
			for (int i = timeType - 1; i >= 0; i--) {
				if (timeA[i] != adjtimeA[i]) {
					return timeA[i] - adjtimeA[i];
				}
			}
			break;
		}
		case TimeTag.TIMETYPE_B: {
			byte[] timeB = this.toTimeB();
			byte[] adjtimeB = adjustTimeB(time);
			for (int i = timeType - 1; i >= 0; i--) {
				if (timeB[i] != adjtimeB[i]) {
					return timeB[i] - adjtimeB[i];
				}
			}
			break;
		}
		}
		return 0;
	}

	/**
	 * 按位对比两个时间的差值---把数据类型的因素考虑在内 有些日数据、月数据的小时、分钟、秒都设置为0，影响了计算结果
	 * 本函数把数据类型的因素考虑进来，可以避免上面的问题
	 */
	public static int compareTime(TimeTag timeTag1, TimeTag timeTag2, int field, int frzType) {
		TimeTag newTimeTag2 = new TimeTag(timeTag2.getMillisTime());
		if (frzType == 2) { // 日冻结数据,不写常量是为了兼容广西负控和国电负控2004的Dti
			newTimeTag2.hour = 0;
			newTimeTag2.minute = 0;
			newTimeTag2.second = 0;
			newTimeTag2.setCalendar();
			//if (newTimeTag2.compare(timeTag2)<0)
				newTimeTag2.add(TimeTag.TIMEUNIT_DAY, 1);
		} else if (frzType == 4) { // 月冻结数据
			newTimeTag2.day = 1;
			newTimeTag2.hour = 0;
			newTimeTag2.minute = 0;
			newTimeTag2.second = 0;
			newTimeTag2.setCalendar();
			//if (newTimeTag2.compare(timeTag2)<0)
				newTimeTag2.add(TimeTag.TIMEUNIT_MONTH, 1);
		}

		int ret = 0;
		switch (field) {
		case TimeTag.TIMEUNIT_YEAR: {
			ret = timeTag1.year - newTimeTag2.year;
			break;
		}
		case TimeTag.TIMEUNIT_MONTH: {
			while (timeTag1.year > newTimeTag2.year) {
				newTimeTag2.add(TimeTag.TIMEUNIT_YEAR, 1);
				ret += 12;
			}
			ret -= newTimeTag2.month;
			ret += timeTag1.month;
			break;
		}
		case TimeTag.TIMEUNIT_DAY: {
			ret = (int) ((timeTag1.getMillisTime() - newTimeTag2.getMillisTime()) / (24 * 60 * 60 * 1000));
			break;
		}
		case TimeTag.TIMEUNIT_HOUR: {
			ret = (int) ((timeTag1.getMillisTime() - newTimeTag2.getMillisTime()) / (60 * 60 * 1000));
			break;
		}
		case TimeTag.TIMEUNIT_MINUTE: {
			ret = (int) ((timeTag1.getMillisTime() - newTimeTag2.getMillisTime()) / (60 * 1000));
			break;
		}
		}
		return ret;
	}

	public String toDbString() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		String time = dbYear + "-" + f.format(month) + "-" + f.format(day) + " "
				+ f.format(hour) + ":" + f.format(minute) + ":" + f.format(second);
		return time;
	}
	
	public String toTimeString() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		String time = dbYear + "" + f.format(month) + "" + f.format(day) + ""
				+ f.format(hour) + "" + f.format(minute);
		return time;
	}

	/**
	 * 生成日期字符串，小时分钟秒用0
	 * @return
	 */
	public String toYYYYMMDDHMS0String() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		String time = dbYear + "-" + f.format(month) + "-" + f.format(day) + " 0:0:0";
		return time;
	}
	
	public String toDateString() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		String time = dbYear + "-" + f.format(month) + "-" + f.format(day);
		return time;
	}

	public String toDbStringWithMillisecond() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		NumberFormat fm = NumberFormat.getInstance();
		fm.setMinimumIntegerDigits(3);
		String time = dbYear + "-" + f.format(month) + "-" + f.format(day) + " "
				+ f.format(hour) + ":" + f.format(minute) + ":" + f.format(second)
				+ ":" + fm.format(millisecond);
		return time;
	}

	public String toStringWithMinute() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		String time = dbYear + "-" + f.format(month) + "-" + f.format(day) + " "
				+ f.format(hour) + ":" + f.format(minute);
		return time;
	}
	
	public String toDbStringWithWeek() {
		int dbYear = year;
		if (year < 1000) {
			dbYear += 2000;
		}
		NumberFormat fw = NumberFormat.getInstance();
		fw.setMinimumIntegerDigits(1);
		NumberFormat f = NumberFormat.getInstance();
		f.setMinimumIntegerDigits(2);
		String time = dbYear + "-" + f.format(month) + "-" + f.format(day) + " "
				+ f.format(hour) + ":" + f.format(minute) + ":" + f.format(second)
				+ " " + fw.format(week);
		return time;
	}

	public String toString() {
		return toDbString();
	}

	public Object clone() {
		TimeTag object = null;
		try {
			object = (TimeTag) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		((TimeTag) object).calendar = (Calendar) calendar.clone();
		return object;
	}

	/**
	 * 检查字符串是否为日期格式的字符串
	 * @param dateString 日期格式的字符串
	 * @return
	 */
	public static boolean isDateFormat(String dateString) {
		dateString = dateString.trim();
		String[] dates = dateString.split("-| |:");
		return (dates != null) && ((dates.length == 6) || (dates.length == 7) || (dates.length == 3));
	}

	/**
	 * 将日期格式字符串转换为TimeTag对象
	 * @param timeStr
	 * @return
	 */
	public static TimeTag valueOf(String timeStr) {
		timeStr = timeStr.trim();
		if (isDateFormat(timeStr)) {
			String[] dates = timeStr.split("-| |:");
			int year = Integer.parseInt(dates[0]);
			int month = Integer.parseInt(dates[1]);
			int day = Integer.parseInt(dates[2]);
			int hour = 0;
			int minute = 0;
			int second = 0;
			if(dates.length>3){
				hour = Integer.parseInt(dates[3]);
				minute = Integer.parseInt(dates[4]);
				second = Integer.parseInt(dates[5]);
			}
			
			return new TimeTag(year>=2000?year:year+2000, month, day, hour, minute, second);
		}
		return null;
	}

	/**
	 * 贵州华立厂站102协议时间格式A
	 * @param data
	 * @return
	 */
	public static TimeTag parseTimeA(byte[] data){
		if (data==null||data.length!=5)
			return null;
		TimeTag tt=new TimeTag();
		tt.minute=data[0]&0x3F;
		tt.hour=data[1]&0x1F;
		tt.day=data[2]&0x1F;
		tt.month=data[3]&0x0F;
		tt.year=data[4]&0x7F;
		if (tt.year < 1000) {
			tt.year += 2000;
		}
		tt.setCalendar();
		return tt;
	}

	/**
	 * 贵州华立厂站102协议时间格式B
	 * @param data
	 * @return
	 */
	public static TimeTag parseTimeB(byte[] data){
		if (data==null||data.length!=7)
			return null;
		TimeTag tt=new TimeTag();
		tt.millisecond=(data[1]&0x03)*16+data[0]&0xFF;
		tt.second=(data[1]>>2)&0x3F;
		tt.minute=data[2]&0x3F;
		tt.hour=data[3]&0x1F;
		tt.day=data[4]&0x1F;
		tt.month=data[5]&0x0F;
		tt.year=data[6]&0x7F; 
		if (tt.year < 1000) {
			tt.year += 2000;
		}
		tt.setCalendar();
		return tt;
	}

	/**
	 * 解析IEEE格式时间
	 * @param time 四字节时间
	 * @return
	 */
	public static TimeTag parseIEEETime(byte[] time){
		if (time==null||time.length!=4)
			return null;
		byte[] newtime=new byte[time.length];
		System.arraycopy(time, 0, newtime, 0, time.length);
		Protocol.getInstance().reverseByteOrder(newtime);
		long seconds=Protocol.getInstance().hexToInt(newtime);
		return new TimeTag(seconds*1000);
	}

	/**
	 * 根据给定月和日，确定比系统时间小的对应年份
	 * @param iMon 指定月份
	 * @param iDay 指定日
	 * @return
	 */
	public static int getYearByMD(int iMon,int iDay){
		TimeTag current=new TimeTag(System.currentTimeMillis());
		TimeTag temp=new TimeTag(current.year,iMon,iDay,0,0,0);
		return current.getMillis()>temp.getMillis()?current.year:current.year-1;
	}
	
	/**
	 * 获取现在时间
	 * 
	 * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取时间字符串
	 *
	 * @return 返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		return dateString;
	}
}
