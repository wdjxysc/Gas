package webapp.sockets.concentrateor;

import webapp.sockets.BaseIotMessageHandler;
import webapp.sockets.BaseIotServer;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Administrator on 2017/5/17.
 */
public class IotConcentratorServer extends BaseIotServer {
    static IotConcentratorServer iotConcentratorServer;

    public IotConcentratorServer(int port) throws IOException {
        super(port);
    }

    public IotConcentratorServer(int port, int timeout) throws IOException {
        super(port,timeout);
    }


    @Override
    public void messageHandler(Socket socket) {
        executorService.execute(new IotConcentratorMessageHandler(socket, connectTimeout));
    }

    @Override
    public boolean isDeviceOnline(String deviceId) {
        deviceId = "0000" + deviceId;
        return super.isDeviceOnline(deviceId);
    }
}
