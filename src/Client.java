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
	public boolean connect(String IPAddress, int port){
		try {
			socket = new DatagramSocket();
			IP=InetAddress.getByName(IPAddress);
			System.out.println("Connected");
		} catch (UnknownHostException | SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public String receive(){
		byte[] data = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(data,data.length);
		try {
			socket.receive(receivePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(receivePacket.getData());
		return message;

	}
	public void sendMessage(final byte[] data){
		sendMessage = new Thread("Send"){
			public void run(){
				DatagramPacket sendPacket = new DatagramPacket(data,data.length,IP,port);
				try {
					socket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendMessage.start();
	}

}
