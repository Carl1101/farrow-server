import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * This server listens on port 5001 for messages and broadcast them when received
 */
public class ChatServer {

	/**
	 * The port that the server listens on.
	 */
	private static final int PORT = 5001;

	/**
	 * The set of all the print writers for all the clients.  This
	 * set is kept so we can easily broadcast messages.
	 */
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	/**
	 * The application main method, which just listens on a port and
	 * spawns handler threads.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running.");
		ServerSocket listener = new ServerSocket(PORT);
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}

	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for a dealing with a single client
	 * and broadcasting its messages.
	 */
	private static class Handler extends Thread {
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		/**
		 * Constructs a handler thread, squirreling away the socket.
		 * All the interesting work is done in the run method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {

				//Creates a in & out object to listen and write through the socket
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				writers.add(out);

				//Listens for messages to broadcast and displays them on console
				while (true) {
					String message = in.readLine();
					System.out.println(message);

					for (PrintWriter writer : writers) {
						writer.println(message);
						writer.flush();
					}
				}

			} catch (IOException e) {
				System.out.println(e);
			} finally {
				// This client is going down!
				// writer from the sets, and close its socket.
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}