package com.QuickDraw;
/*Code and structure for chat, client storage and handling login from user "TheChernoProject" on Youtube
 * Youtube Channel: https://www.youtube.com/user/TheChernoProject
 * Github Repo: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat 
 * Github,Youtube. (2014). Cherno Chat. [online] Available at: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat, https://www.youtube.com/user/TheChernoProject [Accessed 24 Sep. 2018].*/


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JScrollPane;

public class Server extends JFrame implements Runnable{
	
	public List<ClientStorage> clientList = new ArrayList<ClientStorage>();
	public List<Integer> scoreWinner = new ArrayList<Integer>();
	public List<String> words= new ArrayList<String>();
	public List<String> players = new ArrayList<String>();
	private List<Integer> voteScore = new ArrayList<Integer>();
	private DatagramSocket socket;
	private int port,seconds,voted=0;
	private Thread runServer,receive,send;
	private boolean serverRunning, notOpen=true, notDone=true;
	private JTextArea responseLog;
	private JTextField commandLine;
	Thread clients;
	private JScrollPane scrollPane;
	public int drawerTurn=0,guesserCount=1,currGuess=1,drawerCount=1,guessThreshold=1,rounds=0,scoreCount=1,voteCount=0,voteThresh=1,endCount = 0,  roundCount = 0,restartCount=1,checker=0, scoreRegistered=0;
	private String[] playerwords=new String[6];
	public Server(int port) throws IOException{
		setResizable(false); //Constructor for opening the Datagram Socket on port given by the server creator.
		this.port=port;
		showWindow();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				sendToDrawers("20CloseAll");
			}
		});
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
					byte[] data = new byte[11000];
					DatagramPacket receivePacket = new DatagramPacket(data,data.length);
					try {
						socket.receive(receivePacket);
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						parsePacket(receivePacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		receive.start();
	}
	/*Parsing the packet being received*/
	public void parsePacket(DatagramPacket packet) throws IOException{
		String message = new String(packet.getData());
		String condition = message.substring(0,2);
		String names;
		switch(condition) {
			case	"00":
				int id = new SecureRandom().nextInt();
				message=message.trim();
				message=message.substring(2);
				clientList.add(new ClientStorage(message,packet.getAddress(),packet.getPort(),id));
				String ID = "00"+id;
				
				sendMessage(ID.getBytes(), packet.getAddress(), packet.getPort());
				logToServer("Client connected with IP: "+packet.getAddress());//Printing connecting client IP
				names= "24";
				for(int i=0;i<clientList.size();i++) {
					names=names+clientList.get(i).getName()+"-"+clientList.get(i).score+"\t";
				}
				sendToDrawers(names);
				for(int i=0;i<clientList.size()-1;i++) {
					String filename="28"+clientList.get(i).getName();
					sendMessage(filename.getBytes(),clientList.get(clientList.size()-1).getAddress(),clientList.get(clientList.size()-1).getPort());
					BufferedImage img = ImageIO.read(new File("resources/Images/"+clientList.get(i).getName()+".jpg"));
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					ImageIO.write(img, "jpg", output);
					output.flush();
					byte[] out = output.toByteArray();
					sendMessage(out,clientList.get(clientList.size()-1).getAddress(),clientList.get(clientList.size()-1).getPort());
				}
				for(int i=0;i<clientList.size()-1;i++) {
					String filename="28"+clientList.get(clientList.size()-1).getName();
					sendMessage(filename.getBytes(),clientList.get(i).getAddress(),clientList.get(i).getPort());
					BufferedImage img = ImageIO.read(new File("resources/Images/"+clientList.get(clientList.size()-1).getName()+".jpg"));
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					ImageIO.write(img, "jpg", output);
					output.flush();
					byte[] out = output.toByteArray();
					sendMessage(out,clientList.get(i).getAddress(),clientList.get(i).getPort());
				}
				break;
			case	"01":
				sendToDrawers(message);
				logToServer(message.substring(2).trim());
				break;
			case	"02":
				sendToDrawers(message);
				//logToServer(message.substring(2).trim());
				break;
			case	"03":
				sendToDrawers(message);
				logToServer("Board cleared!");
				break;
			case	"04":
				disconnectClient(message);
				sendToDrawers("01"+message.substring(2).trim()+" has disconnected!");
				String name=message.substring(2).trim();
				for(int i=0;i<clientList.size();i++){
					if(clientList.get(i).getName().trim().equals(name)&&clientList.get(i).ready.equals("true")){
						sendToDrawers("01"+clientList.get(i).getName().trim()+" undid their ready!");
						clientList.get(i).ready="false";
					}
				}
				logToServer(message.substring(2).trim()+" has disconnected!");
				names= "24";
				for(int i=0;i<clientList.size();i++) {
					names=names+clientList.get(i).getName()+"-"+clientList.get(i).score+"\t";
				}
				sendToDrawers(names);
				break;
			case	"05":
				String nameReady=message.substring(2).trim();
				
				for(int i=0;i<clientList.size();i++){
					if(clientList.get(i).getName().trim().equals(nameReady)){
						if(clientList.get(i).ready.equals("false")) {
							sendToDrawers("01"+nameReady+" is ready!");
							clientList.get(i).ready="true";
						}else {
							sendToDrawers("01"+nameReady+" undid his/her ready!");
							clientList.get(i).ready="false";
						}
						
					}
				}
				checkGameStart();
				break;
			case	"10":
				if(drawerCount==players.size()){
					if(drawerTurn<players.size()-1){
						getCurrentDrawer();
					}else if(drawerTurn==players.size()-1){
						getCurrentDrawer();
						drawerTurn=0;
					}
					drawerCount=0;
				}
				drawerCount++;
				break;
			case	"11":
				if(currGuess==players.size()){
					String currGuesser=players.get(players.size()-1);
					sendToDrawers("14"+currGuesser);
					currGuess=0;
				}
				currGuess++;
				break;
			case	"12":
				if(guessThreshold==players.size()){
					getGuesser();
					guessThreshold=0;
				}
				guessThreshold++;
				break;
			case	"15":
				sendToDrawers("15Win");
				break;
			case	"16":
				for(int i=0;i<clientList.size();i++) {
					clientList.get(i).ready="false";
				}
				endCount++;
				break;
			case	"18":
				String[] scores=message.substring(2).trim().split(":");
				for(int i=0;i<clientList.size();i++) {
					if(clientList.get(i).getName().equals(scores[0])) {
						clientList.get(i).score=Integer.parseInt(scores[1]);
					}
				}
				break;
			case	"19":
				logToServer("ScoreCount:"+scoreCount+" PlayerSize"+players.size());
					if(scoreCount==players.size()) {
						String scoreList="01Scores\n";
						for(int i=0;i<clientList.size();i++) {
							scoreList=scoreList+clientList.get(i).getName()+"-"+clientList.get(i).score+"\n";
							
						}
						sendToDrawers(scoreList);
						names="24";
						for(int i=0;i<clientList.size();i++) {
							names=names+clientList.get(i).getName()+"-"+clientList.get(i).score+"\t";
							logToServer("Score: "+clientList.get(i).score);
						}
						sendToDrawers(names);
						scoreCount=0;
						
					}
				scoreCount++;
				break;
			case	"21":
				String vote=message.substring(2).trim();
				for(int i=0;i<players.size();i++) {
					if(players.get(i).equals(vote)) {
						voteScore.set(i, voteScore.get(i)+1);
					}
				}
				voted++;
				if(voted==players.size()-1) {
					sendToDrawers("22");
					logToServer("Voted: "+voted+"");
					voted=0;
				}
				break;
			case	"22":
				logToServer("voteThresh: "+voteThresh);
				if(voteThresh==players.size()-1) {
					int max=Collections.max(voteScore);
					String voteWinners="23"+max+"\t";
					for(int i=0;i<voteScore.size();i++) {
						if(voteScore.get(i)==max) {
							voteWinners=voteWinners+players.get(i)+"\t";
						}
					}
					logToServer("Vote Winners:"+voteWinners);
					sendToDrawers(voteWinners);
					voteThresh=-1;
					voted=0;
				}
				voteThresh++;
				break;
			case	"25":
					for(int i=0;i<clientList.size();i++){
						scoreWinner.add(clientList.get(i).score);
					}
					//logToServer("Max Val: "+ Collections.max(scoreWinner, null));
					int max = Collections.max(scoreWinner);
					String winnerName = "";
					for(int i = 0 ; i<clientList.size(); i++){
						if(max == clientList.get(i).score ){
							winnerName = winnerName +clientList.get(i).getName()+"\n";
						}
					}
					
				  if((players.size() == 4 && endCount == 16) || (players.size() == 5 && endCount == 25) || (players.size() == 6 && endCount == 36)){
					  if(checker==0) {
						  checker++;
						  sendToDrawers("26"+winnerName+"with the score of "+max);
						  scoreWinner.clear();
					  }
				  }
				 break;
			case	"27":
				//logToServer(message);
				if(message.substring(2).trim().equals("Y")) {
					logToServer("Restart: "+restartCount);
					if(restartCount==players.size()) {
						sendToDrawers("01Restarting game!");
						sendToDrawers("31");
						shuffleWords();
						roundCount=0;
						rounds=0;
						restartCount=1;
						endCount=0;
						drawerTurn=0;
						scoreCount=1;
						checker=0;
						for(int i=0;i<clientList.size();i++) {
							clientList.get(i).score=0;
							System.out.println(clientList.get(i).score);
						}
						
					}
					restartCount++;
				}else {
					sendToDrawers("30End");
				}
				break;
				
			default: 
				byte[] input=packet.getData();
			    ByteArrayInputStream input_stream= new ByteArrayInputStream(input);
			    BufferedImage final_buffered_image = ImageIO.read(input_stream);
			    String filename=clientList.get(clientList.size()-1).getName();
			    ImageIO.write(final_buffered_image , "jpg", new File("resources/Images/"+filename+".jpg") );
			    for(int i=0;i<clientList.size()-1;i++) {
					filename="28"+clientList.get(i).getName();
					System.out.println(filename);
					sendMessage(filename.getBytes(),clientList.get(clientList.size()-1).getAddress(),clientList.get(clientList.size()-1).getPort());
					 BufferedImage img = ImageIO.read(new File("resources/Images/"+clientList.get(i).getName()+".jpg"));
					 ByteArrayOutputStream output = new ByteArrayOutputStream();
					 ImageIO.write(img, "jpg", output);
					 output.flush();
					 byte[] out = output.toByteArray();
					 sendMessage(out,clientList.get(clientList.size()-1).getAddress(),clientList.get(clientList.size()-1).getPort());
				}
			    for(int i=0;i<clientList.size()-1;i++) {
					filename="28"+clientList.get(clientList.size()-1).getName();
					System.out.println(filename);
					sendMessage(filename.getBytes(),clientList.get(i).getAddress(),clientList.get(i).getPort());
					 BufferedImage img = ImageIO.read(new File("resources/Images/"+clientList.get(clientList.size()-1).getName()+".jpg"));
					 ByteArrayOutputStream output = new ByteArrayOutputStream();
					 ImageIO.write(img, "jpg", output);
					 output.flush();
					 byte[] out = output.toByteArray();
					 sendMessage(out,clientList.get(i).getAddress(),clientList.get(i).getPort());
				}
				break;
		}
	}

	public void getCurrentDrawer(){
		String currentDrawer="09"+players.get(drawerTurn);
		sendToDrawers(currentDrawer);
		if(drawerTurn<players.size()-1) {
			String nextDrawer="17"+"Next: "+players.get(drawerTurn+1);
			sendToDrawers(nextDrawer);
		}else {
			String nextDrawer="17"+"Guessing turn";
			sendToDrawers(nextDrawer);
		}
		logToServer("drawerTurn: "+drawerTurn+" Current drawer: "+players.get(drawerTurn)+" "+players.toString());
		drawerTurn++;
	}
	public void checkGameStart() throws IOException{
		
		int readyPlayers=0;
		for(int i=0;i<clientList.size();i++){
			if(clientList.get(i).ready.equals("true"))
				readyPlayers++;
		}
		if(readyPlayers==clientList.size()){
			if(readyPlayers>3){
				roundCount++;
				sendToDrawers("25"+roundCount);
				drawerTurn=0;
				sendToDrawers("06"+clientList.size());
				sendToDrawers("03Clear");
				guesserCount=players.size();
				getGuesser();

			}else{
				sendToDrawers("01\nAll players are ready but the game needs atleast 4 players to start.");
			}
		}else{
			sendToDrawers("01\nWaiting for all players to ready up. Current: "+readyPlayers);
		}
	}

	public void getGuesser() throws IOException{
		if(rounds==0) {
			shufflePlayers();
			shuffleWords();
		}else if(rounds==players.size()) {
			sendToDrawers("No more rounds left!");
			//sendToDrawers("26END");
			return;
		}
		rounds++;
		sendToDrawers("03ClearBoard");
		sendToDrawers("07"+players.get(0));
		logToServer(players.get(0));
		String drawers="08";
		voteScore.clear();
		for(int i=1;i<players.size();i++){
			drawers=drawers+players.get(i)+"\t";
			voteScore.add(0);
		}
		sendToDrawers(drawers);
		sendToDrawers("13"+words.get(0));
		Collections.rotate(words, -1);
		Collections.rotate(players, -1);
		logToServer("getGuesser"+players.toString());
		guesserCount=0;
	}
	public void shufflePlayers(){
		players.clear();
		for(int i=0;i<clientList.size();i++){
			players.add(clientList.get(i).getName());
		}
		Collections.shuffle(players);
	}
	
	
	/*Iterating through client list to pass message to all clients*/
	public void sendToDrawers(String message){
		//If user is painting, he/she will not receive the coordinates for his/her painting
		for(int i=0;i<clientList.size();i++){
			ClientStorage client = clientList.get(i);
			if(client.drawer){
				message=message.trim();
				sendMessage(message.getBytes(),client.address,client.port);
			}
		}
	}
	
	public void sendBytesToDrawers(byte[] message){
		//If user is painting, he/she will not receive the coordinates for his/her painting
		for(int i=0;i<clientList.size();i++){
			ClientStorage client = clientList.get(i);
			sendMessage(message,client.address,client.port);
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

	
	public void shuffleWords() throws IOException{
		String[] inputwords=readFile("resources/words.txt");
		Random rand = new Random();
		words.clear();
		for(int i=0;i<6;i++) {
			int n = rand.nextInt(inputwords.length);
			playerwords[i]=inputwords[n];
			logToServer(playerwords[i]);
		}
		for(int i=0;i<clientList.size();i++){
			words.add(playerwords[i]);
		}
		Collections.shuffle(words);
		logToServer(words.toString());
	}
	
	public void showWindow(){
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		responseLog = new JTextArea();
		responseLog.setLineWrap(true);
		responseLog.setEditable(false);
		responseLog.setBounds(10, 11, 564, 283);
		contentPane.add(responseLog);
		DefaultCaret caret = (DefaultCaret)responseLog.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		commandLine = new JTextField();
		commandLine.setBounds(10, 305, 564, 45);
		contentPane.add(commandLine);
		commandLine.setColumns(10);
		
		scrollPane = new JScrollPane(responseLog);
		scrollPane.setBounds(10, 11, 564, 283);
		contentPane.add(scrollPane);
		commandLine.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent KE) {
				if(KE.getKeyCode()==KeyEvent.VK_ENTER){
					serverCommands(commandLine.getText());
				}
			}
		});
	
	}
	public void serverCommands(String command){
		if(command.startsWith("/all")){
			sendToDrawers("01"+"Server says:"+command.substring(4));
			logToServer("Server says:"+command.substring(4));
			commandLine.setText("");
		}else if(command.equals("/listplayers")){
			logToServer("Players Online"+listPlayers());
			commandLine.setText("");
		}else if(command.equals("/clear")) {
			sendToDrawers("03");
			logToServer("Board Cleared!");
			commandLine.setText("");
		}else{
			logToServer("Command Unknown");
			commandLine.setText("");
		}
	}
	String[] readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        String words=sb.toString();
	        return words.split("\n");
	    } finally {
	        br.close();
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
