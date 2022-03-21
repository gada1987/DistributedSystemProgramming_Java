import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client{
	
	static boolean isAlive = true;
	static ObjectOutputStream oos;
	static ObjectInputStream ois;
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		Scanner clientInput = new Scanner(System.in);
		
		Socket clientSocket = new Socket("127.0.0.1", 1337);
		
		//Send message thread
		Thread sendMessage = new Thread(new Runnable(){
			
			@Override
			public void run(){
				
				while(isAlive){
					
					String msgToSend = clientInput.nextLine();

					if(msgToSend.equals("exit")) isAlive = false;
					
					try{
						
						oos = new ObjectOutputStream(clientSocket.getOutputStream());
						ois = new ObjectInputStream(clientSocket.getInputStream());
						
						MessageSender ms = new MessageSender(msgToSend);
						oos.writeObject(ms);
						//dos.writeUTF(msgToSend);
						
					}
					catch(Exception e){System.err.println(e);}
					
					if(msgToSend.equals("sync")){
						
						//serialize client folder and send it to server
						try{
							
							//ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
							FileSender fs = new FileSender();
							fs.readClientFiles();
							
							oos.writeObject(fs);
							
							//input from server
							
							MessageSender recvMsg = (MessageSender)ois.readObject();
							
							String msgToRecieve = recvMsg.recMsg();
							
							System.out.println("Message from server:\n" + msgToRecieve);
							
							//oos.reset();
							//oos.close();
							//dos = new DataOutputStream(clientSocket.getOutputStream());
							
						}
						catch(Exception e){System.err.println(e);}
						
					}
					
				}
				
				System.out.println("terminating...");
				
				try{
					
					oos.close();
					ois.close();
					//dis.close();
					//dos.close();
					clientSocket.close();
					
				}
				catch(Exception e){System.err.println(e);}
				
			}
			
		});
		
		sendMessage.start();
		//recieveMessage.start();
		
	}
	
}