import java.io.*;
import java.util.*;

public class FileSender implements Serializable{
	
	private static final long serialVersionUID = 7829136421241571165L;
	
	File[] clientFiles;
	File[] serverFiles;
	
	public FileSender(){
		
		//clientFiles = new ArrayList<String>();
		//serverFiles = new ArrayList<String>();
		
	}
	
	public void readClientFiles(){
		
		File folder = new File("F:/Distributed_Systems_Programming/Task_3_ny_11_30/clientFiles/");
		File[] listOfFiles = folder.listFiles();
		
		clientFiles = listOfFiles;
		
	}
	
	public void readServerFiles(int clientId){
		
		File folder = new File("F:/Distributed_Systems_Programming/Task_3_ny_11_30/serverFiles/" + clientId + "/");
		File[] listOfFiles = folder.listFiles();
		
		serverFiles = listOfFiles;
		
	}
	
	public File[] getClientFiles(){
		
		return clientFiles;
		
	}
	
	public File[] getServerFiles(){
		
		return serverFiles;
		
	}
	
}