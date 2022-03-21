
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/** Client Thread Class for client,
 *  a thread to see incoming messages from
 * server and to send users messages to server*/

public class ClientThread implements Runnable {
	private Socket socket;
	private PrintWriter write;
	private Scanner scan;
	private InputStream in;
	private static String str;
	private static boolean state;
	private String name;
/*============================Class Structure =========================*/
public ClientThread(Socket socket, String name) {
		this.socket = socket;
        str = "";
		state = false;
		this.name = name;

	}
/*==================Send method to send msg=======================*/
	private void send() {
		state = false;
		write.println("> " + str);
		write.flush();
		str = "";
	}
/*==================Send name method to print a name=======================*/
	private void sendName(String a) {
		state = false;
		write.println(a);
		write.flush();
	}
/*=========================================================*/
	public static void msgChange(String msg) {
		str = msg;
		state = true;
	}
/*========================================================*/
	@Override
	public void run() {
		try {
			write = new PrintWriter(socket.getOutputStream());
			in = socket.getInputStream();
			scan = new Scanner(in);
            System.out.println("-----------------------------------------");
			System.out.println("Welcome to the Chat Server! \n" + "To help, type 'ProgramInfo' to list the commands!");
			System.out.println("-----------------------------------------");
			sendName("*set " + name);
			write.println(" has joined the server!");
			write.flush();

			while (!socket.isClosed()) {

				if (in.available() > 0) {
					if (scan.hasNextLine()) {
						String s = scan.nextLine();
						if (s.equals("logoutfromserver")) {
							System.out.println("You have been blocked.");
							socket.close();
						} else
							System.out.println(s);

					}
				}
				
				if (str.equals("ProgramInfo")) {
					System.out.println("-----------------------------------------");
					System.out.println("Welcome to list of all the instructions! ");
					System.out.println("1- Type 'online' to get all connected users ");
					System.out.println("2- Type 'logout' to leave the chat server.");
					System.out.println("3- Type '*set' to change superuser ");
					System.out.println("4- Type '@private Username' to send a message to a specific user.");
					System.out.println("5- Type '#super' to become a SuperUser :)");
					System.out.println("6- Type '&block Username' to ban a specific user from the server.");
					System.out.println("-----------------------------------------");
					state = false;
					str = "";
				}
				if (str.equals("logoutfromserver")) {
					str = "";
				}

					if(str.equals("logout")) {
						//using sendName to not have same structure as normal messages.
						sendName(" has left the server :(");
						str = "";
						socket.close();
					}

				if (state) {
					send();
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

}