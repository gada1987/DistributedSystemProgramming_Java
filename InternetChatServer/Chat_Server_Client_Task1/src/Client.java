
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter your username to start chat with other clients please! ");
		String name = scan.nextLine();
		// blank or null name not allowed
		while (name == null || name.trim().equals("")) {
			System.out.println("Invalid username. Re-try other username.");
			name = scan.nextLine();
		}
		// if started with spaces, it is not detected.
		name = name.trim(); 
		String arr[] = name.split(" ");
		// condition to allow only one word as a user name.
		if (arr.length > 1) {
			name = arr[0];
		}
		System.out.println(name);

		
		try {
			Socket socket = new Socket("localhost", 9876);
			ClientThread user = new ClientThread(socket, name);
			Thread thread = new Thread(user);
			thread.start();

			while (thread.isAlive()) {
	// condition to check input from user, when found send it to msgChange.
				if (scan.hasNextLine()) {
					ClientThread.msgChange(scan.nextLine());
				}
			}
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
