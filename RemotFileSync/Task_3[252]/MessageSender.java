import java.io.*;

public class MessageSender implements Serializable{
	
	//F:\Distributed_Systems_Programming\Task_3_ny_11_30\clientFiles
	
	private static final long serialVersionUID = 7829136421241571165L;
	
	String msg = "";
	
	public MessageSender(String msg){
		
		this.msg = msg;
		
	}
	
	public void sendMsg(String msg){
		
		this.msg = msg;
		
	}
	
	public String recMsg(){
		
		return msg;
		
	}
	
}