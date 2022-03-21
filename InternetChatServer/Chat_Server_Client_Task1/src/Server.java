
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	static ArrayList<ServerThread> userslist = new ArrayList<ServerThread>();
	static ArrayList<String> names = new ArrayList<String>();

	public static void main(String[] args) {
		try {
			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(9876);
			System.out.println("The server has been started at port 9876 & localhost");
             while (true) {
				Socket socket = server.accept();
				ServerThread handler = new ServerThread(socket);
				Thread thread = new Thread(handler);
				userslist.add(handler);
				thread.start();

			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	}
	
	
