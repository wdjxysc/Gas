package webapp.sockets.iotmeter;



import webapp.sockets.iotmeter.protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoClient {
	private String host="127.0.0.1";
	private int port = 20006;
	private Socket socket;
	
	public EchoClient() throws IOException{
		socket = new Socket(host, port);
		
	}
	
	public static void main(String[] args) throws Exception{
		//new EchoClient().talk();
		int numTasks = 2;
		ExecutorService exec = Executors.newCachedThreadPool();
		for(int i=0;i<numTasks;i++){
			exec.execute(createTask(i));
		}
	}
	
	public void talk() throws IOException{
		try{
			
			boolean flag = socket.isConnected();
            InputStream inputStream = socket.getInputStream();

            while (flag) {
                try {
                    Thread.sleep(100);
                } 
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int dataCount = inputStream.available();
                if (dataCount == 0) {
                    continue;
                }

                byte[] data = new byte[dataCount];
                if (data[0] == -1) {
                    flag = false;
                    continue;
                }
                
                int read = inputStream.read(data);
                System.out.println("get " + read + " bytes");
                
            }
			
		}
		catch(IOException e){
			e.printStackTrace();
		}
		finally{
			try{
				socket.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	private static Runnable createTask(final int taskId){
		return new Runnable() {
			private Socket socket = null;
			private String host = "127.0.0.1";
			private int port = 20006;
			@Override
			public void run() {
				System.out.println("Task:" + taskId + ":start");
				try{
					socket = new Socket(host, port);
					byte[] command = "68 00 21 30 21 00 00 20 01 16 07 00 00 01 16 07 17 00 00 00 01 00 02 00 3C 00 00 16".getBytes();
					
					
					OutputStream os = socket.getOutputStream();
			        OutputStreamWriter osw = new OutputStreamWriter(os);
			        BufferedWriter bw = new BufferedWriter(osw);
			        String writeContent = Protocol.getInstance().hexToHexString(command);
			        bw.write(writeContent);
			        System.out.println("Message sent to the client is " + writeContent);
			        bw.flush();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		};
	}
}
