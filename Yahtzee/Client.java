import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client{
	
	private static boolean isAlive = true;
	private static String username;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		Scanner clientInput = new Scanner(System.in);
		
		Socket clientSocket = new Socket("127.0.0.1", 1337);
		
		DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
		
		//Send message thread
		Thread sendMessage = new Thread(new Runnable(){
			
			@Override
			public void run(){
				
				System.out.print("Enter username:");
				username = clientInput.nextLine();
				
				try{
					
					dos.writeUTF(username);
					
				}
				catch(Exception e){System.err.println(e);}
				
				while(isAlive){
					
					String msgToSend = clientInput.nextLine();
					
					if(msgToSend.equals("exit")) isAlive = false;
					
					try{
						
						dos.writeUTF(msgToSend);
						
					}
					catch(Exception e){System.err.println(e);}
					
				}
				
				try{
					
					clientSocket.close();
					dis.close();
					dos.close();
					
				}
				catch(Exception e){System.err.println(e);}
				
			}
			
		});
		
		//Recieve message thread
		Thread recieveMessage = new Thread(new Runnable(){
			
			@Override
			public void run(){
				
				while(isAlive){
					
					try{
						
						String msgToRecieve = dis.readUTF();
						System.out.println(msgToRecieve);
						
					}
					catch(Exception e){System.err.println(e);}
					
				}
				
				try{
					
					clientSocket.close();
					dis.close();
					dos.close();
					
				}
				catch(Exception e){System.err.println(e);}
				
			}
			
		});
		
		sendMessage.start();
		recieveMessage.start();
		
	}
	
}