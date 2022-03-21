import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
	
	static Vector<ClientHandler> clientArray = new Vector<>();
	
	public static void main(String[] args) throws IOException{
		
		int numberOfClients = 0;
		
		ServerSocket theServerSocket = new ServerSocket(1337);
		
		Socket clientListener;
		
		//polling for new clients
		while(true){
			
			clientListener = theServerSocket.accept();
			
			System.out.println("New client:" + clientListener);
			
			//DataInputStream dis = new DataInputStream(clientListener.getInputStream());
			//DataOutputStream dos = new DataOutputStream(clientListener.getOutputStream());
			
			//ObjectOutputStream oos = new ObjectOutputStream(clientListener.getOutputStream());
			//ObjectInputStream ois = new ObjectInputStream(clientListener.getInputStream());
			
			ClientHandler newClientHandler = new ClientHandler(clientListener, numberOfClients);
			
			//creating a new thread for this client
			Thread clientThread = new Thread(newClientHandler);
			
			System.out.println("Adding new client to thread");
			
			//add client thread to array of clients
			clientArray.add(newClientHandler);
			
			//start the client thread
			clientThread.start();
			
			numberOfClients++;
			
		}
		
	}
	
}

class ClientHandler implements Runnable{
	
	private Scanner clientInputKeyboard = new Scanner(System.in);
	private Socket client;
	private int clientId;
	private boolean isAlive;
	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public ClientHandler(Socket c, int id){
		
		client = c;
		this.oos = oos;
		this.ois = ois;
		clientId = id;
		isAlive = true;
		
	}
	
	@Override
	public void run(){
		
		String recvMsg;
		String msgToSend;
		
		while(isAlive){
			
			try{
				
				ois = new ObjectInputStream(client.getInputStream());
				oos = new ObjectOutputStream(client.getOutputStream());
				
				MessageSender ms = (MessageSender)ois.readObject();
				
				recvMsg = ms.recMsg();
				//recieve message
				//recvMsg = dis.readUTF();
				
				System.out.println(recvMsg);
				
				if(recvMsg.equals("exit")){
					
					System.out.println("terminating client...");
					
					try{
						
						oos.close();
						ois.close();
						
					}
					catch(Exception e){System.err.println(e);}
					
					isAlive = false;
					
				}
				
				else if(recvMsg.equals("sync")){
					
					System.out.println("recieving serialized list of files from client");
					FileSender fs = (FileSender)ois.readObject();
					
					File[] listOfClientFiles = fs.getClientFiles();
					/*
					for(File local : listOfClientFiles){
						
						System.out.println(local.getName());
						
					}
					*/
					
					//Serialize server client's folder and send it back to client
					fs.readServerFiles(clientId);
					File[] listOfServerFiles = fs.getServerFiles();
					
					for(File local : listOfServerFiles){
						
						//System.out.println(local.getName());
						
					}
					
					//Send list of files, or send just an acknowledgement
					String messageToSend = "No new files to sync";
					boolean found = false;
					
					for(File server : listOfServerFiles){
						
						found = false;
						
						for(File client : listOfClientFiles){
							
							if(server.isDirectory()){
								
								found = true;
								
							}
							
							if(server.getName().equals(client.getName())){
								
								//File was detected
								found = true;
								
							}
							
						}
						
						if(!found){
							
							if(messageToSend.equals("No new files to sync")) messageToSend = "Files that exist on server drive and not on local drive:\n";
							
							messageToSend = messageToSend + server.getName() + "\n";
							
						}
						
					}
					
					MessageSender sendToClient = new MessageSender(messageToSend);
					oos.writeObject(sendToClient);
					//ms = new MessageSender(messageToSend);
					
				}
				
			}
			catch(Exception e){System.err.println(e);}
			
		}
		
		try{
			
			client.close();
			
			//dis.close();
			//dos.close();
			
		}
		catch(Exception e){System.err.println(e);}
		
	}
	
}