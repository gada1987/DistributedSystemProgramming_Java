import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/** Server thread class to and thread the messages and different commands
	 * Received by User */

public class ServerThread implements Runnable {
		private Socket socket;
		private PrintWriter pwriter;
		private Scanner scan;
		int userid;
		int namesnumber;
		boolean delete;
		String name;
		String allNames;

/*=============== This is a class structure =================== */		
public ServerThread(Socket socket) {
			this.socket = socket;
			userid = 0;
			namesnumber = 0;
			delete = false;
			this.name = "";
			allNames = "";
		}

/*===============This method will set a new name for User=================== */
		public void setName(String a) {
			this.name = a;
		}
/*=============This method will be for the client to be "Superuser"========== */
		
		public void setID() {
			if (userid == 0)
				userid = 1;
			else
				userid = 0;
		}
/*=============This method will send the messages from client to all users========== */
		public void sendAllMsg(String msg) {
			if (this.userid == 1 && !delete) 
				msg = "SuperUser " + msg;
			for (ServerThread user : Server.userslist) { 
				PrintWriter userWrite = user.getWriter();
				if (userWrite != null) {
					userWrite.write(msg);
					userWrite.flush();
				}

			}
		}
/*====================== This method will run Server thread ======================== */
		@Override
		public void run() {

			try {
				pwriter = new PrintWriter(socket.getOutputStream());
				scan = new Scanner(socket.getInputStream());

				while (!socket.isClosed()) {

					if (scan.hasNextLine()) {
						String str = scan.nextLine();
						if (!str.equals("> ")) { 
							String arr[] = str.split(" "); 

							if (arr[1].equals("@private")) { 
								for (ServerThread user : Server.userslist) {
									if (user.name.equalsIgnoreCase(arr[2])) {
										PrintWriter userWrite = user.getWriter();
										if (userWrite != null) {
											userWrite.write(this.name + str + "\n");
											userWrite.flush();
										}
									}

								}
							}
//===================For the server to receive the name===================
							else if (arr[0].equals("*set")) {
							    String result = arr[1];
								for (ServerThread user : Server.userslist) {
									if (user.name.equalsIgnoreCase(arr[1])) {
										result = arr[1] + Server.userslist.size(); 
										delete = true;
									}

								}
								if (delete) {
									setName(result);
								} else
									setName(arr[1]);
								delete = false;
							}
//===================== get online users ==========================
							else if (arr[1].equals("online")) {
								String allNames = "";
								for (int i = 0; i < Server.userslist.size(); i++) {
									allNames = allNames + ", " + Server.userslist.get(i).name;
								}
								pwriter.write("Connected user: [" + allNames + "]" + "\n");
								pwriter.flush();
							}
//===================== block the user ==========================
							else if (arr[1].equals("&block")) {
		// ban the user from server, SuperUser move only.
								if (this.userid == 1) {
									ServerThread ban = null;
									for (ServerThread user : Server.userslist) {
										if (user.name.equalsIgnoreCase(arr[2])) {
											PrintWriter userWrite = user.getWriter();
											if (userWrite != null) {
												userWrite.write("logoutfromserver" + "\n");
												userWrite.flush();
												this.delete = true;
												sendAllMsg(user.name + " has been blocked." + "\n");
												this.delete = false;
												ban = user;
											}
										}

									}
									Server.userslist.remove(ban);
								} else {
									pwriter.write("Access Refused." + "\n");
									pwriter.flush();
								}
//===================== Allow the user to be superuser====================								
							} else if (arr[1].equals("#super")) {
								setID();
							}

							else {
								sendAllMsg(name + str + "\n");
								
							}
						}

					}
				}
			} catch (IOException e) {
			
				e.printStackTrace();
			}

		}

/*=======================Method to get user writer==================*/		
		public PrintWriter getWriter() {
			return pwriter;
		}

	}
	