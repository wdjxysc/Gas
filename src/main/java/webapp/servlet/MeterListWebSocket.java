package webapp.servlet;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by Administrator on 2017/5/15.
 */
public class MeterListWebSocket extends BaseWebSocket {

    @Override
    public void onOpen(Session session) {
        super.onOpen(session);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public void onMessage(String message, Session session) {
        super.onMessage(message, session);
    }

    @Override
    public void onError(Session session, Throwable error) {
        super.onError(session, error);
    }

    public void NotifyMeterList() throws IOException{
        super.sendMessage("");
    }
}
