import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import uk.ac.imperial.lsds.seep.api.API;
import uk.ac.imperial.lsds.seep.api.SeepTask;
import uk.ac.imperial.lsds.seep.api.data.ITuple;


public class Sink implements SeepTask {

	private int PERIOD = 1000;
	private int count = 0;
	private long time;
	
	@Override
	public void setUp() {
		// TODO Auto-generated method stub
	}

	@Override
	public void processData(ITuple data, API api) {
		count++;
		int userId = data.getInt("userId");
		long ts = data.getLong("ts");
		String text = data.getString("text");
		System.out.println("[Sink] UID: "+userId+" ts: "+ts+" text: "+text);
		
		//Sent a responce every 100 requests! (each play request maps to 100 from Seep-Source code)
		if((count-1)%100 == 0)
			new PlayHTTPResponceSender().run();
		
		System.out.println("[Sink] Sent responce to Play");
		
		if(System.currentTimeMillis() - time > PERIOD){
			System.out.println("[Sink] e/s: "+count);
			count = 0;
			time = System.currentTimeMillis();
		}
	}

	@Override
	public void processDataGroup(ITuple dataBatch, API api) {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	
	public static class PlayHTTPResponceSender implements Runnable{
		
		private BufferedReader in;
		private PrintWriter out;

		
		public PlayHTTPResponceSender(){
			
		}
		
		public void run(){
			sendToServer();
		}
		
		public void sendToServer() {
			try{
			// Get the server address from a dialog box.
			String serverAddress = "127.0.0.1";

			// Make connection and initialize streams
			Socket socket = new Socket(serverAddress, 9100);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
			
			out.println("Sample Responce");
			socket.close();
			} catch (IOException e){
				System.out.println("IOException when sending Play HTTP responce "+ e);
			}

		}
	}

}
