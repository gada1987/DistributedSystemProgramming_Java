import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
	
	static String[] board = {"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes",
		"Sum", "Bonus", "Three of a kind", "Four of a kind",
		"Full house", "Small straight", "Large straight",
		"Chance\t", "YAHTZEE\t"};
	
	static final int NR_OF_GAMES = 5;
	static boolean[] gameRoomIsActive = new boolean[NR_OF_GAMES];
	static int[] numberOfPlayersInRoom = new int[NR_OF_GAMES];
	static int[] numberOfPlayersInRoomReady = new int[NR_OF_GAMES];
	static int numberOfClients;
	
	static int[] nrOfYes = new int[NR_OF_GAMES];
	static int[] nrOfAnswers = new int[NR_OF_GAMES];
	
	static ArrayList<ClientHandler> clientArray = new ArrayList<>();
	
	public static void main(String[] args) throws IOException{
		
		numberOfClients = 0;
		
		ServerSocket theServerSocket = new ServerSocket(1337);
		
		Socket clientListener;
		
		//create a number of game rooms with independent id's
		for(int i = 0; i < NR_OF_GAMES; i++){
			
			gameRoomIsActive[i] = false;
			numberOfPlayersInRoom[i] = 0;
			nrOfYes[i] = 0;
			nrOfAnswers[i] = 0;
			
		}
		
		//polling for new clients
		while(true){
			
			clientListener = theServerSocket.accept();
			
			System.out.println("New client:" + clientListener);
			
			DataInputStream dis = new DataInputStream(clientListener.getInputStream());
			DataOutputStream dos = new DataOutputStream(clientListener.getOutputStream());
			
			//read username from client
			String username = "";
			try{
				
				username = dis.readUTF();
				
			}
			catch(Exception e){System.err.println(e);}
			
			ClientHandler newClientHandler = new ClientHandler(clientListener, username, dis, dos);
			
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
	private String username;
	private int gameRoomId;
	private int points;
	private int sum;
	private boolean isAlive;
	private boolean playerIsInARoom;
	private boolean spectatorMode;
	private boolean highscore;
	
	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	public ClientHandler(Socket c, String username, DataInputStream dis, DataOutputStream dos){
		
		sum = 0;
		points = 0;
		gameRoomId = -1;
		client = c;
		this.username = username;
		this.dis = dis;
		this.dos = dos;
		isAlive = true;
		playerIsInARoom = false;
		spectatorMode = false;
		highscore = false;
		
	}
	
	//The players turn
	public synchronized void playerTurn(){
		
		Random rand = new Random();
		
		int nrOfDices = 5;
		int[] dices = new int[nrOfDices];
		boolean[] diceLock = new boolean[nrOfDices];
		
		try{
			
			dos.writeUTF("\nRolling the dices\n");
			
			for(int i = 0; i < nrOfDices; i++){
				
				diceLock[i] = false;
				
				rand = new Random();
				
				dices[i] = rand.nextInt((6 - 1) + 1) + 1;
				
				dos.writeUTF("Dice " + (i + 1) + ":" + dices[i]);
				
			}
			
			dos.writeUTF("Lock dices by writing eg: 1,2,3\n");
			dos.writeUTF("If you don't want to lock any dices, write: 0\n");
			
			String lockDice = dis.readUTF();
			
			if(lockDice.equals("0")){
				
				
				
			}
			else{
				
				//read through string and lock dices that are marked
				for(int i = 0; i < lockDice.length(); i++){
					
					for(int j = 0; j < nrOfDices; j++){
						
						char theDiceToLock = (char)(j + '1');
						
						if(lockDice.charAt(i) == theDiceToLock){
							
							diceLock[j] = true;
							
						}
						
					}
					
				}
				
			}
			
			for(int j = 0; j < 2; j++){
				
				for(int i = 0; i < nrOfDices; i++){
					
					if(diceLock[i]){
						
						dos.writeUTF("Dice " + (i + 1) + ":" + dices[i]);
						
					}
					else{
						
						rand = new Random();
						
						dices[i] = rand.nextInt((6 - 1) + 1) + 1;
						
						dos.writeUTF("Dice " + (i + 1) + ":" + dices[i]);
						
					}
					
				}
				
				if(j >= 1) continue;
				
				dos.writeUTF("Lock dices by writing eg: 1,2,3\n");
				dos.writeUTF("If you don't want to lock any dices, write: 0\n");
				
				lockDice = dis.readUTF();
				
				if(lockDice.equals("0")){
					
					//don't lock any dices
					
				}
				else{
					
					//read through string and lock dices that are marked
					for(int i = 0; i < lockDice.length(); i++){
						
						for(int l = 0; l < nrOfDices; l++){
							
							char theDiceToLock = (char)(l + '1');
							
							if(lockDice.charAt(i) == theDiceToLock){
								
								dos.writeUTF("locking dice:" + l);
								diceLock[l] = true;
								
							}
							
						}
						
					}
					
				}
				
			}
			
			//dice rolling is now done, print out score board to choose from
			//go through all the dices and check what kind of points the player can collect
			
			dos.writeUTF("\nPress key to select option:\n");
			
			for(int i = 0; i < 15; i++){
				
				if(i == 6){
					
					dos.writeUTF(Server.board[i] + "\t\t\t" + sum);
					continue;
					
				}
				if(i < 8) dos.writeUTF(Server.board[i] + "\t\t\t" + (i + 1));
				else      dos.writeUTF(Server.board[i] + "\t\t"   + (i + 1));
				
			}
			
			String selection = dis.readUTF();
			
			int numbers = 0;
			int[] nrOfDifferentDices = new int[5];
			int max;
			int index;
			
			switch(selection){
			case "1":
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 1) numbers++;
					
				}
				sum = sum + numbers;
				break;
				
			case "2":
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 2) numbers = numbers + 2;
					
				}
				sum = sum + numbers;
				break;
				
			case "3":
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 3) numbers = numbers + 3;
					
				}
				sum = sum + numbers;
				break;
				
			case "4":
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 4) numbers = numbers + 4;
					
				}
				sum = sum + numbers;
				break;
				
			case "5":
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 5) numbers = numbers + 5;
					
				}
				sum = sum + numbers;
				break;
				
			case "6":
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 6) numbers = numbers + 6;
					
				}
				sum = sum + numbers;
				break;
				
				case "8":
				//bonus
				if(sum >= 63) numbers = numbers + 35;
				
				break;
				
				case "9":
				//three of a kind
				//exempel tre 2or och en 5a och en 1a ger 12 points
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 1) nrOfDifferentDices[0]++;
					if(dices[i] == 2) nrOfDifferentDices[1]++;
					if(dices[i] == 3) nrOfDifferentDices[2]++;
					if(dices[i] == 4) nrOfDifferentDices[3]++;
					if(dices[i] == 5) nrOfDifferentDices[4]++;
					if(dices[i] == 6) nrOfDifferentDices[5]++;
					
				}
				
				max = nrOfDifferentDices[0];
				index = 0;
				
				for(int i = 1; i < nrOfDices; i++){
					
					if(max < nrOfDifferentDices[i]){
						
						index = i;
						max = nrOfDifferentDices[i];
						
					}
					
				}
				
				index = index + 1;
				
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] != index) numbers = numbers + dices[i];
					
				}
				
				numbers = numbers + (3 * index);
				
				break;
				
				case "10":
				//four of a kind
				
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] == 1) nrOfDifferentDices[0]++;
					if(dices[i] == 2) nrOfDifferentDices[1]++;
					if(dices[i] == 3) nrOfDifferentDices[2]++;
					if(dices[i] == 4) nrOfDifferentDices[3]++;
					if(dices[i] == 5) nrOfDifferentDices[4]++;
					if(dices[i] == 6) nrOfDifferentDices[5]++;
					
				}
				
				max = nrOfDifferentDices[0];
				index = 0;
				
				for(int i = 1; i < nrOfDices; i++){
					
					if(max < nrOfDifferentDices[i]){
						
						index = i;
						max = nrOfDifferentDices[i];
						
					}
					
				}
				
				index = index + 1;
				
				for(int i = 0; i < nrOfDices; i++){
					
					if(dices[i] != index) numbers = numbers + dices[i];
					
				}
				
				numbers = numbers + (4 * index);
				
				break;
				
				case "11":
				//full house
				//three of the same and two of the same
				numbers = numbers + 25;
				
				break;
				
				case "12":
				//small straight
				//sequence of four
				numbers = numbers + 30;
				
				break;
				
				case "13":
				//large straight
				//sequence of five
				numbers = numbers + 40;
				
				break;
				
				case "14":
				//chance
				for(int i = 0; i < nrOfDices; i++){
					
					numbers = numbers + dices[i];
					
				}
				break;
				
				case "15":
				//yahtzee
				int numberOfSame = 1;
				int theNumber = dices[0];
				
				for(int i = 1; i < nrOfDices; i++){
					
					if(dices[i] == theNumber) numberOfSame++;
					
				}
				
				if(numberOfSame == 5) numbers = 50;
				
				break;
				
			}
			
			points = points + numbers;
			
		}
		
		catch(Exception e){System.err.println(e);}
		
	}
	
	
	
	
	@Override
	public void run(){
		
		String recvMsg;
		String msgToSend;
		
		while(isAlive){
			
			try{
				
				while(!playerIsInARoom && !spectatorMode){
					
					dos.writeUTF("List of game rooms");
					
					for(int i = 0; i < Server.NR_OF_GAMES; i++){
						
						if(Server.gameRoomIsActive[i]){
							
							dos.writeUTF("Game:" + (i + 1) + " join as spectator");
							
						}
						else{
							
							dos.writeUTF("Game:" + (i + 1));
							
						}
						
					}
					
					if(highscore) dos.writeUTF(username + " has the highest score with:" + points + " points");
					
					recvMsg = dis.readUTF();
					
					if(recvMsg.equals("exit")){
						
						isAlive = false;
						
					}
					else{
						
						switch(recvMsg){
							
						case "1":
							gameRoomId = 1;
							playerIsInARoom = true;
							Server.numberOfPlayersInRoom[gameRoomId-1]++;
							break;
							
						case "2":
							gameRoomId = 2;
							playerIsInARoom = true;
							Server.numberOfPlayersInRoom[gameRoomId-1]++;
							break;
							
						case "3":
							gameRoomId = 3;
							playerIsInARoom = true;
							Server.numberOfPlayersInRoom[gameRoomId-1]++;
							break;
							
						case "4":
							gameRoomId = 4;
							playerIsInARoom = true;
							Server.numberOfPlayersInRoom[gameRoomId-1]++;
							break;
							
						case "5":
							gameRoomId = 5;
							playerIsInARoom = true;
							Server.numberOfPlayersInRoom[gameRoomId-1]++;
							break;
							
						default:
							
							break;
							
						}
						
						if(Server.gameRoomIsActive[gameRoomId-1]){
							
							playerIsInARoom = true;
							spectatorMode = true;
							Server.numberOfPlayersInRoom[gameRoomId-1]--;
							
						}
						
					}
					
				}
				
				//Player has entered a room
				while(playerIsInARoom && !Server.gameRoomIsActive[gameRoomId-1]){
					
					if(!spectatorMode){
						
						Server.nrOfYes[gameRoomId-1] = 0;
						
						dos.writeUTF("Type 1 to start, 0 to wait, 2 to exit to lobby " + (gameRoomId-1));
						
						String clientMessage;
						
						clientMessage = dis.readUTF();
						
						if(clientMessage.equals("1")){
							
							Server.nrOfYes[gameRoomId-1]++;
							
						}
						else if(clientMessage.equals("0")){
							
							Server.nrOfYes[gameRoomId-1]--;
							
						}
						else if(clientMessage.equals("2")){
							
							//System.out.println(gameRoomId-1);
							//exit back to lobby
							Server.numberOfPlayersInRoom[gameRoomId-1]--;
							gameRoomId = -1;
							playerIsInARoom = false;
							break;
							
						}
						
						//Synchronize waiting for answers for all clients
						Thread.sleep(5000);
						
						if(Server.nrOfYes[gameRoomId-1] == Server.numberOfPlayersInRoom[gameRoomId-1]){
							
							Server.gameRoomIsActive[gameRoomId-1] = true;
							
						}
						
					}
					else{
						
						gameRoomId = -1;
						playerIsInARoom = false;
						spectatorMode = false;
						break;
						
					}
					
				}
				
				int nrOfPlayers = Server.numberOfPlayersInRoom[gameRoomId-1];
				Server.numberOfPlayersInRoomReady[gameRoomId-1] = nrOfPlayers;
				
				dos.writeUTF("Game has started with:" + nrOfPlayers + " players");
				
				Random rand = new Random();
				
				int nrOfDices = 6;
				int[] dices = new int[nrOfDices];
				
				//13 rounds maximum
				
				int gameRounds = 0;
				int waitForAllPlayers = Server.numberOfPlayersInRoom[gameRoomId-1];
				
				while(Server.gameRoomIsActive[gameRoomId-1] && gameRounds < 13){
					
					playerTurn();
					
					Server.numberOfPlayersInRoomReady[gameRoomId-1]--;
					
					//if number of players have passed => continue
					while(Server.numberOfPlayersInRoomReady[gameRoomId-1] > 0){
						
						dos.writeUTF("Waiting for " + Server.numberOfPlayersInRoomReady[gameRoomId-1] + " player/s");
						Thread.sleep(2000);
						
					}
					
					Thread.sleep(10000);
					
					gameRounds++;
					dos.writeUTF("Gameround:" + gameRounds);
					
					Server.numberOfPlayersInRoomReady[gameRoomId-1] = nrOfPlayers;
					
				}
				
				points = points + sum;
				
				dos.writeUTF("Game is finished!\n You got: " + points + " points");
				
				
				
				Server.gameRoomIsActive[gameRoomId-1] = false;
				
			}
			catch(Exception e){System.err.println(e);}
			
		}
		
		try{
			
			client.close();
			dis.close();
			dos.close();
			
		}
		catch(Exception e){System.err.println(e);}
		
	}
	
}