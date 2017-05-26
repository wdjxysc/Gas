package webapp.sockets.concentrateor.encode;


import webapp.sockets.concentrateor.field.CtrlCode;
import webapp.sockets.iotmeter.constant.PiaoaiCommand;

/**
 * 
 * @author Administrator
 *
 */
public class RTHCEncoder {
	private static final long serialVersionUID = 1L;

    private static RTHCEncoder instance = null;

    protected RTHCEncoder() {

    }

    public static RTHCEncoder getInstance() {
        if (instance == null)
            instance = new RTHCEncoder();
        return instance;
    }

    public byte[] organizeFrame(byte[] commandType, int longOfFrame, byte[] frame) {
        byte[] objectFrame = null;
        return objectFrame;
    }

    /**
     * 
     * @param receiveFrame
     * @return
     */
    public int getResponseFrameLength(byte[] receiveFrame) {

        //get the command type of the length of the received frame
        RTHCDecoder decoder = RTHCDecoder.getInstance();
        CtrlCode cc = new CtrlCode((byte[]) decoder.getLogicFieldByteArray(receiveFrame, 3));
        String ccString = cc.getControlCodeString();
        // then get the corresponding length of the sendCommand
        return PiaoaiCommand.getCommandStringSample(ccString, true).length();
    }
}
