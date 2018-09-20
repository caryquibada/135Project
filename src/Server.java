import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class Server implements Runnable{
	
	private List<ClientStorage> clientList = new ArrayList<ClientStorage>();
	
	private DatagramSocket socket;
	private int port;
	private Thread runServer,receive,send;
	private boolean serverRunning;
	Thread clients;
	public Server(int port){ //Constructor for opening the Datagram Socket on port given by the server creator.
		this.port=port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		runServer=new Thread(this,"Server");
		runServer.start();
		System.out.println("Server running on port: "+port);
	}
	public void run(){
		serverRunning = true;
		receiveMessage();
	}
	/*Thread that constantly tries to receive a message from any clients*/
	public void receiveMessage(){
		receive = new Thread("Receive Message"){
			public void run(){
				while(serverRunning){
					byte[] data = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(data,data.length);
					try {
						socket.receive(receivePacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
					parsePacket(receivePacket);
				}
			}
		};
		receive.start();
	}
	/*Parsing the packet being received*/
	public void parsePacket(DatagramPacket packet){
		String message = new String(packet.getData());
		if(message.startsWith("00")){ //00 For login
			int id = new SecureRandom().nextInt();
			clientList.add(new ClientStorage(message.substring(2,message.length()),packet.getAddress(),packet.getPort(),id));
			String ID = "00"+id;
			sendMessage(ID.getBytes(), packet.getAddress(), packet.getPort());
			System.out.println("Client connected with IP: "+packet.getAddress());//Printing connecting client IP
		}else if(message.startsWith("01")){//01 For message;
			sendToClients(message);
			System.out.println(message.substring(2).trim());
		}else if(message.startsWith("02")){//02 For Canvas Drawing
			sendToClients(message);
			System.out.println(message.substring(2).trim());
		}else if(message.startsWith("03")){//03 For Clearing the Board
			sendToClients(message);
			System.out.println("Board cleared!");
		}else if(message.startsWith("04")){//04 For Disconnection
			removeClient(message);
			sendToClients("01"+message.substring(2).trim()+" has disconnected!");
			System.out.println(message.substring(2).trim()+" has disconnected!");
		}
	}
	/*Iterating through client list to pass message to all clients*/
	public void sendToClients(String message){
		for(int i=0;i<clientList.size();i++){
			ClientStorage client = clientList.get(i);
			message=message.trim();
			sendMessage(message.getBytes(),client.address,client.port);
		}
	}
	/*Removing client from array list when client is disconnecting*/
	public void removeClient(String name){
		name=name.substring(2).trim();
		for(int i=0;i<clientList.size();i++){
			if(clientList.get(i).getName().trim().equals(name))
				clientList.remove(i);
		}
		for(int i=0;i<clientList.size();i++){
			System.out.println(clientList.get(i).getName().trim());
		}
	}
	/*Sending data to client IP address*/
	public void sendMessage(byte[] data, InetAddress IPAddress,int port){

		send = new Thread("Send"){
			public void run(){
				DatagramPacket sendPacket = new DatagramPacket(data,data.length,IPAddress,port);
				try{
					socket.send(sendPacket);
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
}
