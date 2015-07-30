import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import uk.ac.imperial.lsds.seep.api.API;
import uk.ac.imperial.lsds.seep.api.SeepTask;
import uk.ac.imperial.lsds.seep.api.data.ITuple;
import uk.ac.imperial.lsds.seep.api.data.OTuple;
import uk.ac.imperial.lsds.seep.api.data.Schema;
import uk.ac.imperial.lsds.seep.api.data.Type;
import uk.ac.imperial.lsds.seep.api.data.Schema.SchemaBuilder;


public class Source implements SeepTask {

	private static Schema schema1 = SchemaBuilder.getInstance().newField(Type.INT, "userId").newField(Type.LONG, "ts").newField(Type.STRING, "text").build();
//	private Schema schema2 = SchemaBuilder.getInstance().newField(Type.INT, "userId").newField(Type.LONG, "ts").build();
	private static API api;
	private boolean working = true;
	private int clientNumber = 0;
	private ServerSocket listener;

	
	@Override
	public void setUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void processData(ITuple data, API api) {
		int userId = 0;
		long ts = 0;
		
		Source.api = api;
		
		try {
			listener = new ServerSocket();
			listener.bind(new InetSocketAddress("127.0.0.1", 9000));
			System.out.println("[Source] The #PlaySeepIntegration# server is running.");
		} catch (IOException e) {
			System.out.println("[Source] Could not start Play Listener! "+ e);
		}
		waitHere(2000);
		
		while(working){
//			byte[] d = OTuple.create(schema1, new String[]{"userId", "ts", "text"}, new Object[]{userId, ts, 
//					"some text"});// text to force some errors if possiblesome long text to force some errors if possible"
//					+ "some long text to force some errors if possiblesome long text to force some errors if possible"
//					+ "some long text to force some errors if possiblesome long text to force some errors if possible"});
//			byte[] d = OTuple.create(schema2, new String[]{"userId", "ts"}, new Object[]{userId, ts,});
//			System.out.println("[Source] uid: "+ userId + " ts: "+ ts + "text: "+"some_text");
			
			try {
				new PlayReceiver(listener.accept(), clientNumber++, userId++, ts++, "some_text").run();
			} catch (IOException e) {
				System.out.println("[Source] Play Server IOException " + e);
				try {
					listener.close();
					System.exit(-1);
				} catch (IOException ex) {
					System.out.println("[Source] Play Server lister.close IOException");
				}
			}

//			System.out.println("[Source Going to send!]");
//			api.send(d);
			
//			userId++;
//			ts++;
			
		}

	}
	
	private void waitHere(int time){
		try {
			Thread.sleep(time);
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void processDataGroup(ITuple dataBatch, API api) {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() {
		this.working = false;
	}
	
	/**
     * A private thread to handle Play HTTP requests on a particular
     * socket.  The client terminates the dialogue by sending a single line
     * containing only a period.
     */
    private static class PlayReceiver implements Runnable {
        private Socket socket;
        private int clientNumber;
        private long ts;
        private int userId;
        private String text;

        public PlayReceiver(Socket socket, int clientNumber, int userId, long ts, String text) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            this.ts = ts;
            this.text =text;
            this.userId = userId;
            
            try {
            	this.socket.setKeepAlive(true);
			} catch (SocketException e) {
				e.printStackTrace();
			}
            log("[Source] New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the
         * client a welcome message then repeatedly reading strings
         */
        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                // Get messages from the client, line by line; return them
                // capitalized
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.equals("exit")) {
                    	 break;
                    }
                    else{
            			byte[] data = OTuple.create(schema1, new String[]{"userId", "ts", "text"}, new Object[]{userId++, ts++, 
    					"some text"});
            			System.out.println("[Source Play listener] uid: "+ userId + " ts: "+ ts + "text: "+"some_text");
            			for(int i =0 ; i < 100; i++)
            				Source.api.send(data);
                    }
                    out.println(input.toUpperCase());
                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        /**
         * Logs a simple message.  In this case we just write the
         * message to the server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
    }
}
