/*Code and structure for chat, client storage and handling login from user "TheChernoProject" on Youtube
 * Youtube Channel: https://www.youtube.com/user/TheChernoProject
 * Github Repo: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat 
 * Github,Youtube. (2014). Cherno Chat. [online] Available at: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat, https://www.youtube.com/user/TheChernoProject [Accessed 24 Sep. 2018].*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	private String name;
	private String IPAddress;
	private int port;
	private int ID;
	private DatagramSocket socket;
	private InetAddress IP;
	private String message;
	private ClientGUI clientgui;
	private Thread sendMessage,receive;
	private String word;
	private boolean myTurn=false; //If turn to guess
	public String ready="false";
	private int guessTurn=0; //Guess turn number
	
	public Client(String name, String IPAddress, int port,String word){
		this.name=name;
		this.IPAddress=IPAddress;
		this.port=port;
		this.word=word;
	}
	public String getName(){
		return name;
	}
	
	public String getWord(){
		return word;
	}
	
	public void setID(int ID){
		this.ID=ID;
	}
	public int getID(){
		return this.ID;
	}
	public void setMyTurn(){
		if(myTurn){
			this.myTurn=false;
		}else{
			this.myTurn=true;
		}
	}
	public boolean getMyTurn(){
		return this.myTurn;
	}
	public void setGuessTurn(int turnNumber){
		this.guessTurn=turnNumber;
	}
	public int getGuessTurn(){
		return this.guessTurn;
	}
	
	/*Connecting to the server by opening a socket*/
	public boolean connect(String IPAddress, int port){
		try {
			socket = new DatagramSocket();
			IP=InetAddress.getByName(IPAddress);
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*Receiving a message from the server*/
	public String receive(){
		byte[] data = new byte[128];
		DatagramPacket receivePacket = new DatagramPacket(data,data.length);
		try {
			socket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(receivePacket.getData());
		return message;
	}
	
	/*Sending a message to the server*/
	public void sendMessage(final byte[] data){
		DatagramPacket sendPacket = new DatagramPacket(data,data.length,IP,port);
		try{
			socket.send(sendPacket);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
