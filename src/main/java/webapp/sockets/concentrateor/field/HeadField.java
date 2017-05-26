package webapp.sockets.concentrateor.field;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/18.
 *
 * 报文头部： 报文长度（2）+ 功能码（2） + 传送方向（1） + 请求响应标识（1） + 从站编号（7） + 报文ID（7）
 */
public class HeadField implements Serializable {
}
