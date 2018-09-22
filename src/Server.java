import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Server extends JFrame implements Runnable{
	
	private List<ClientStorage> clientList = new ArrayList<ClientStorage>();
	
	private DatagramSocket socket;
	private int port;
	private Thread runServer,receive,send;
	private boolean serverRunning;
	private JTextArea responseLog;
	private JTextField commandLine;
	Thread clients;
	public Server(int port) throws UnknownHostException{ //Constructor for opening the Datagram Socket on port given by the server creator.
		this.port=port;
		showWindow();
		try {
			socket = new DatagramSocket(port,InetAddress.getLocalHost());
			logToServer(InetAddress.getLocalHost().toString());
		} catch (SocketException e) {
			e.printStackTrace();
		}
		runServer=new Thread(this,"Server");
		runServer.start();
		logToServer("Server running on port: "+port);
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
					byte[] data = new byte[128];
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
			logToServer("Client connected with IP: "+packet.getAddress());//Printing connecting client IP
		}else if(message.startsWith("01")){//01 For message;
			sendToClients(message);
			logToServer(message.substring(2).trim());
		}else if(message.startsWith("02")){//02 For Canvas Drawing
			sendToClients(message);
			//logToServer(message.substring(2).trim());
		}else if(message.startsWith("03")){//03 For Clearing the Board
			sendToClients(message);
			logToServer("Board cleared!");
		}else if(message.startsWith("04")){//04 For Disconnection
			disconnectClient(message);
			sendToClients("01"+message.substring(2).trim()+" has disconnected!");
			logToServer(message.substring(2).trim()+" has disconnected!");
		}
	}
	/*Iterating through client list to pass message to all clients*/
	public void sendToClients(String message){
		//If user is painting, he/she will not receive the coordinates for his/her painting
		if(message.startsWith("02")){ 
			String[] canvasInfo=message.split(",");
			int userID=Integer.parseInt(canvasInfo[2].trim());
			for(int i=0;i<clientList.size();i++){
				ClientStorage client = clientList.get(i);
				if(userID!=client.getID()){
					message=message.trim();
					sendMessage(message.getBytes(),client.address,client.port);
				}
			}
		}else{
			for(int i=0;i<clientList.size();i++){
				ClientStorage client = clientList.get(i);
				message=message.trim();
				sendMessage(message.getBytes(),client.address,client.port);
			}
		}
	}
	/*Removing client from array list when client is disconnecting*/
	public void disconnectClient(String name){
		name=name.substring(2).trim();
		for(int i=0;i<clientList.size();i++){
			if(clientList.get(i).getName().trim().equals(name))
				clientList.remove(i);
		}
	}
	/*Sending data to client IP address*/
	public void sendMessage(byte[] data, InetAddress IPAddress,int port){
		DatagramPacket sendPacket = new DatagramPacket(data,data.length,IPAddress,port);
		try{
			socket.send(sendPacket);
		}catch(IOException e){
			e.printStackTrace();
		}	
	}
	
	public void showWindow(){
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		commandLine = new JTextField();
		commandLine.setBounds(10, 305, 564, 45);
		contentPane.add(commandLine);
		commandLine.setColumns(10);
		commandLine.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent KE) {
				if(KE.getKeyCode()==KeyEvent.VK_ENTER){
					serverCommands(commandLine.getText());
				}
			}
		});
		
		
		responseLog = new JTextArea();
		responseLog.setEditable(false);
		responseLog.setBounds(10, 11, 564, 283);
		contentPane.add(responseLog);
	}
	
	
	public void serverCommands(String command){
		if(command.startsWith("/all")){
			sendToClients("01"+"Server says:"+command.substring(4));
			logToServer("Server says:"+command.substring(4));
			commandLine.setText("");
		}else if(command.equals("/listplayers")){
			logToServer("Players Online"+listPlayers());
			commandLine.setText("");
		}else{
			logToServer("Command Unknown");
			commandLine.setText("");
		}
	}
	
	public String listPlayers(){
		String players = "("+clientList.size()+"): ";
		for(int i=0;i<clientList.size();i++){
			if(i==clientList.size()-1){
				players=players+clientList.get(i).getName();
			}else{
				players=players+clientList.get(i).getName()+",";
			}
		}
		return players;
	}
	
	public void logToServer(String message){
		responseLog.setText(responseLog.getText()+"\n"+message);
	}
}
