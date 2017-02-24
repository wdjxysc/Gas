package webapp.sockets.iotmeter.frame;



import webapp.sockets.iotmeter.encode.RTHCDecoder;
import webapp.sockets.iotmeter.field.*;

import java.io.Serializable;

public class ReceivedFrame implements Serializable {
	private static final long serialVersionUID = 1L;
	/*** 起始符*/
	private byte[] beginByte = new byte[] { 0x68 };
	/*** 帧的总长度*/
	private LengthClass lengthOfFrame;
	/*** 控制码*/
	private CtrlCode controlCode;
	/*** 标识为： 方向标识和响应标识*/
	private DirectionResponseFlag drf;
	/*** 从站编号*/
	private ClientID ssid;
	/*** 报文编号*/
	private MessageID mid;
	/*** 数据域长度*/
	private LengthClass lengthOfDataField;
	/*** 响应标识*/
	private ResponseFlag rf;
	/*** 数据域*/
	private DataField dataField;
	/*** 校验码*/
	private CrcCode crcCode;
	/*** 结束符*/
	private byte[] endByte = new byte[] { 0x16 };

	/**
	 * 返回帧
	 */
	public byte[] receivedFrame = null;

	/**
	 * 分解一个收到的Frame
	 *
	 * @param frame
	 */
	public ReceivedFrame(byte[] frame) {
		RTHCDecoder decoder = RTHCDecoder.getInstance();
		receivedFrame = frame;
		lengthOfFrame = new LengthClass((byte[]) (decoder.getLogicFieldByteArray(frame, 2)));
		controlCode = new CtrlCode((byte[]) decoder.getLogicFieldByteArray(frame, 3));
		drf = new DirectionResponseFlag((byte[]) decoder.getLogicFieldByteArray(frame, 4));
		ssid = new ClientID((byte[]) decoder.getLogicFieldByteArray(frame, 5));
		mid = new MessageID((byte[]) decoder.getLogicFieldByteArray(frame, 6));
		lengthOfDataField = new LengthClass((byte[]) decoder.getLogicFieldByteArray(frame, 7));
		rf = new ResponseFlag((byte[]) decoder.getLogicFieldByteArray(frame, 8));
		dataField = new DataField((byte[]) decoder.getLogicFieldByteArray(frame, 9));
		crcCode = new CrcCode((byte[]) decoder.getLogicFieldByteArray(frame, 10));
	}

	public void setLengthOfFrame(LengthClass lengthOfFrame) {
		this.lengthOfFrame = lengthOfFrame;
	}

	public LengthClass getLengthOfFrame() {
		return lengthOfFrame;
	}

	public void setControlCode(CtrlCode controlCode) {
		this.controlCode = controlCode;
	}

	public CtrlCode getControlCode() {
		return controlCode;
	}

	public void setDrf(DirectionResponseFlag drf) {
		this.drf = drf;
	}

	public DirectionResponseFlag getDrf() {
		return drf;
	}

	public void setSsid(ClientID ssid) {
		this.ssid = ssid;
	}

	public ClientID getSsid() {
		return ssid;
	}

	public void setMid(MessageID mid) {
		this.mid = mid;
	}

	public MessageID getMid() {
		return mid;
	}

	public void setLengthOfDataField(LengthClass lengthOfDataField) {
		this.lengthOfDataField = lengthOfDataField;
	}

	public LengthClass getLengthOfDataField() {
		return lengthOfDataField;
	}

	public void setResponseFlag(ResponseFlag rf) {
		this.rf = rf;
	}

	public ResponseFlag getResponseFlag() {
		return rf;
	}

	public void setDataField(DataField dataField) {
		this.dataField = dataField;
	}

	public DataField getDataField() {
		return dataField;
	}

	public void setCrcCode(CrcCode crcCode) {
		this.crcCode = crcCode;
	}

	public CrcCode getCrcCode() {
		return crcCode;
	}
}
