package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;

public class PlayRequestTransceiver {

	private static long start;
	private static long end;
	
	private BufferedReader in;
	private PrintWriter out;

	private static class SinkReponseReceiver implements Runnable {
		private Socket socket;
		ServerSocket SeepSinklistener;

		public SinkReponseReceiver() {
			try {
				SeepSinklistener = new ServerSocket();
				SeepSinklistener.bind(new InetSocketAddress("127.0.0.1", 9100));
				System.out
						.println("[PlayRequestTransceiver] The SeepSink listener server is running.");
			} catch (IOException e) {
				System.out
						.println("[PlayRequestTransceiver] Could not start SeepSink Listener! "
								+ e);
			}
		}

		public void run() {

			while (true) {
				/*
				 * Wait for the client to connect
				 */
				try {
					this.socket = SeepSinklistener.accept();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				try {
					this.socket.setKeepAlive(true);
				} catch (SocketException e) {
					e.printStackTrace();
				}
				System.out.println("[SinkReponseReceiver] New connection at "
						+ socket);

				try {

					// Decorate the streams so we can send characters
					// and not just bytes.
					BufferedReader in = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));

					// Get messages from the client, line by line
					while (true) {
						System.out.println("INSIDE");
						String input = in.readLine();
						if (input == null || input.equals("exit")) {
							break;
						}
						end = System.currentTimeMillis();
						System.out.println("[SinkReponseReceiver] READ: "
								+ input.toUpperCase());
						System.out.println("[SinkReponseReceiver] Total Time: "
								+ (end - start) + " ms");
					}
				} catch (IOException e) {
					System.out.println("Error handling client# " + e);
				} finally {
					try {
						socket.close();
					} catch (IOException e) {
						System.out
								.println("Couldn't close a socket, what's going on?");
					}
					System.out.println("Connection with client# closed");
				}
			}
		}

	}

	/**
	 * Implements the connection logic by prompting the end user for the
	 * server's IP address, connecting, setting up streams, and consuming the
	 * welcome messages from the server. The Capitalizer protocol says that the
	 * server sends three lines of text to the client immediately after
	 * establishing a connection.
	 */
	public void sendToServer() throws IOException {

		// Get the server address from a dialog box.
		String serverAddress = "127.0.0.1";

		// Make connection and initialize streams
		Socket socket = new Socket(serverAddress, 9000);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);


		while (socket.isConnected()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String line = br.readLine();
			start  = System.currentTimeMillis();
			out.println(line);
			if (line.compareTo("exit") == 0)
				break;
		}
		// socket.close();
	}

	/**
	 * Runs the client application.
	 */
	public static void main(String[] args) throws Exception {
		PlayRequestTransceiver client = new PlayRequestTransceiver();
		System.out.println("Test");
		Thread t = new Thread(new SinkReponseReceiver());
		t.start();
		System.out.println("Now connecting to server");
		client.sendToServer();
	}
}