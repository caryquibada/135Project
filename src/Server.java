import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{
	
	private List<ClientStorage> clientList = new ArrayList<ClientStorage>();
	
	private DatagramSocket socket;
	private int port;
	private Thread runServer,receive,send;
	private boolean serverRunning;
	Thread clients;
	public Server(int port){
		this.port=port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		runServer=new Thread(this,"Server");
		runServer.start();
	}
	public void run(){
		System.out.println("running");
		serverRunning = true;
		clients();
		receiveMessage();
	}
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
	public void parsePacket(DatagramPacket packet){
		String message = new String(packet.getData());
		if(message.startsWith("00")){ //00 For login
			int id = new SecureRandom().nextInt();
			clientList.add(new ClientStorage(message.substring(2,message.length()),packet.getAddress(),packet.getPort(),id));
			String ID = "00"+id;
			sendMessage(ID.getBytes(), packet.getAddress(), packet.getPort());
		}else if(message.startsWith("01")){//01 For message;
			sendToClients(message);
		}else if(message.startsWith("02")){//02 For Canvas Drawing
			sendToClients(message);
		}else if(message.startsWith("03")){//03 For Clearing the Board
			sendToClients(message);
		}
	}
	public void sendToClients(String message){
		
		for(int i=0;i<clientList.size();i++){
			ClientStorage client = clientList.get(i);
			message=message.trim();
			sendMessage(message.getBytes(),client.address,client.port);
		}
	}
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
	public void clients(){
		clients = new Thread("Client Manager"){
			public void run(){
				while(serverRunning){
					
				}
			}
		};
		clients.start();
	}
}
