/*Code and structure for chat, client storage and handling login from user "TheChernoProject" on Youtube
 * Youtube Channel: https://www.youtube.com/user/TheChernoProject
 * Github Repo: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat 
 * Github,Youtube. (2014). Cherno Chat. [online] Available at: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat, https://www.youtube.com/user/TheChernoProject [Accessed 24 Sep. 2018].*/

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.SwingConstants;

public class ClientGUI extends JFrame implements Runnable{

	private JPanel contentPane;
	private JTextField ChatBox;
	private JTextArea ChatHistory;
	private Client client;
	private Thread constantReceive;
	private Thread run;
	private boolean clientRunning=false;
	private int ID=0,seconds,defSeconds;
	private int currXsend,currYsend,currXreceive,currYreceive;
    private Canvas mainCanvas;
    private int turns=0,numberOfReadyPlayers;
    private JTextField timerWindow;
    private JTextField DrawerTurn;
    private JTextField wordField;
    private String guesser;
	public ClientGUI(String name,String IPAddress, int port,String word){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				String disconnectMessage="04"+name;
				client.sendMessage(disconnectMessage.getBytes());
			}
		});
		showWindow();
		client = new Client(name,IPAddress,port,word);
		client.drawTurn=true;
		boolean connected=client.connect(IPAddress,port);
		System.out.println("Name: "+name+" IP: "+IPAddress+" Port: "+port);
		String connectPacket = "00"+name+","+word;
		client.sendMessage(connectPacket.getBytes());
		clientRunning = true;
		run = new Thread(this,"Client Thread");
		run.start();
	}
	//Appending to chat text area
	public void printToChat(String message){
		ChatHistory.append(message.substring(2).trim()+"\n");
		
	}
	
	//Sending to the client class the message for it to process.
	public void sendPacket(String chatMessage){
		chatMessage = "01"+chatMessage;
		client.sendMessage(chatMessage.getBytes());
		ChatBox.setText("");
	}
	
	//Sending x and y coordinate to client class to send the message to server
	public void sendXY(String XY){
		client.sendMessage(XY.getBytes());
	}
	public void run() {
		constantReceive();
	}
	
	//Constant receiving of message from the server and parsing the message.
	
	public void constantReceive(){
		System.out.println(clientRunning);
		constantReceive = new Thread("constantReceive"){
			public void run(){
				while(clientRunning){
					String message=client.receive();
					
					parseMessage(message);
				}
			}
		};
		constantReceive.start();
	}
	//Different prefixes are assigned to different server responses
	public void parseMessage(String message){
		if(message.startsWith("00")){ //Login
			client.setID(Integer.parseInt(message.substring(3,message.length()).trim()));
			sendPacket(client.getName()+" has connected");
		}else if(message.startsWith("01")){ //Chat
			String[] chatMessage=message.split(":");
			if(chatMessage.length>1){
				if(client.getName().equals(chatMessage[0].substring(2).trim())&&chatMessage[1].trim().equals("/ready")){
					client.ready="true";
					client.sendMessage(("05"+client.getName()).getBytes());
				}else{
					printToChat(message);
				}
			}else{
				printToChat(message);
			}
		}else if(message.startsWith("02")){ //Drawing
			String[] xy=message.substring(2).split(",");
			int oldx=Integer.parseInt(xy[0]);
			int oldy=Integer.parseInt(xy[1]);
			int x = Integer.parseInt(xy[2]);
			int y = Integer.parseInt(xy[3].trim());
			Graphics g = mainCanvas.getGraphics();
			g.drawLine(oldx, oldy, x, y);
		}else if(message.startsWith("03")){ //Clear Board
			Graphics g = mainCanvas.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
		}else if(message.startsWith("06")){ //Count down for game start
			numberOfReadyPlayers=Integer.parseInt(message.substring(2).trim());
			if(numberOfReadyPlayers==4){
				seconds=5*4*4;
				defSeconds=5;
			}else if(numberOfReadyPlayers==5){
				seconds=4*5*5;
				defSeconds=4;
			}else if(numberOfReadyPlayers>5){
				defSeconds=3;
				seconds=3*numberOfReadyPlayers*numberOfReadyPlayers;
			}
			countDown();
		}else if(message.startsWith("07")){ //Guesser preparation
			guesser=message.substring(2).trim();
			if(guesser.equals(client.getName())){
				mainCanvas.setVisible(false);
				client.drawer=false;
			}else{
				mainCanvas.setVisible(true);
				client.drawer=true;
			}
		}else if(message.startsWith("08")){ //Drawer preparation
			String[] drawers= message.trim().split("\t");
			for(int i=0;i<drawers.length;i++){
				if(drawers[i].equals(client.getName())){
					client.drawer=true;
					mainCanvas.setVisible(true);
				}
			}
		}else if(message.startsWith("09")){ //Turn packet
			String player=message.substring(2).trim();
			if(player.equals(client.getName())){
				client.drawTurn=true;
				DrawerTurn.setText("Your turn");
			}else{
				client.drawTurn=false;
				DrawerTurn.setText(player+"'s turn");
			}
		}else if(message.startsWith("13")){ //What to draw packet/guess packet
			if(client.drawer){
				String currentWord=message.substring(2).trim();
				wordField.setText("\tDraw: "+currentWord);
			}else{
				String currentWord=message.substring(2).trim();
				String guesserWord="\t";
				for(int i=0;i<currentWord.length();i++){
					guesserWord=guesserWord+" _";
				}
				wordField.setText(guesserWord);
			}
		}else if(message.startsWith("14")){ //Guess turn packet
			if(message.substring(2).trim().equals(client.getName())){
				mainCanvas.setVisible(true);
			}
			DrawerTurn.setText(message.substring(2).trim()+" is guessing");
		}
	}
	
	//Countdown Timer
		public void countDown(){
			Timer timer = new Timer();
			timer.schedule(new TimerTask(){
				int turns=numberOfReadyPlayers-1;
				int drawTurns=1;
				public void run(){
					int drawTime=(seconds-(numberOfReadyPlayers*defSeconds*turns))%defSeconds;
					timerWindow.setText(drawTime+"");
					if(drawTime==0 && drawTurns<numberOfReadyPlayers && drawTurns!=1){
						client.sendMessage("10Draw".getBytes()); //Draw Turn
						System.out.println("between");
						drawTurns++;
					}else if(drawTime==0 && drawTurns==1){
						client.sendMessage("10Draw".getBytes());
						client.sendMessage("12ChangeGuessingTurn".getBytes());
						System.out.println("1");
						drawTurns++;
					}else if(drawTime==0&& drawTurns==numberOfReadyPlayers){
						client.sendMessage("11ChangeGuessTurn".getBytes());
						System.out.println("end");
						drawTurns=1;
						turns--;
					}
					seconds--;
					if(seconds==0){
						timer.cancel();
					}
				}
			},0,1000);
		}
	
	public void isCurrentDrawer(String nameMessage){
		if(client.getName().equals(nameMessage)){
			client.myTurn=true;
		}else{
			client.myTurn=false;
		}
	}
	
	//GUI stuff(ignore)
	public void showWindow() {
		setVisible(true);
		setTitle("Sketch Window");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(245, 255, 250));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//ChatHistory Text Area
		ChatHistory = new JTextArea();
		ChatHistory.setLineWrap(true);
		ChatHistory.setFont(new Font("Dialog", Font.PLAIN, 11));
		ChatHistory.setEditable(false);
		DefaultCaret caret = (DefaultCaret)ChatHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane ChatScroll= new JScrollPane(ChatHistory);
		ChatScroll.setBounds(720, 11, 164, 491);
		contentPane.add(ChatScroll);
		
		//ChatBox Text Field
		ChatBox = new JTextField();
		ChatBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent KE) {
				if(KE.getKeyCode()==KeyEvent.VK_ENTER&&!ChatBox.getText().isEmpty()){
					sendPacket(client.getName()+": "+ChatBox.getText());
				}
			}
		});
		ChatBox.setBounds(720, 513, 164, 47);
		contentPane.add(ChatBox);
		ChatBox.setColumns(10);
		
		contentPane.add(ChatBox);
		ChatBox.setColumns(10);
		
		mainCanvas = new Canvas();
		mainCanvas.setBackground(Color.WHITE);
		mainCanvas.setBounds(10, 69, 693, 491);
		contentPane.add(mainCanvas);
		
		JButton ClearButton = new JButton("Clear Board");
		ClearButton.setBackground(new Color(255, 255, 255));
		ClearButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				String clear="03Clear";
				client.sendMessage(clear.getBytes());
			}
		});
		
		ClearButton.setBounds(10, 11, 89, 23);
		contentPane.add(ClearButton);
		
		timerWindow = new JTextField();
		timerWindow.setHorizontalAlignment(SwingConstants.CENTER);
		timerWindow.setBackground(new Color(245, 255, 250));
		timerWindow.setEditable(false);
		timerWindow.setFont(new Font("Oxygen", Font.BOLD, 16));
		timerWindow.setBounds(676, 11, 34, 47);
		contentPane.add(timerWindow);
		timerWindow.setColumns(10);
		
		JButton changeTurn = new JButton("Turn");
		changeTurn.setBackground(new Color(255, 255, 255));
		changeTurn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				client.changeTurn();
			}
		});
		changeTurn.setBounds(10, 40, 89, 23);
		contentPane.add(changeTurn);
		
		DrawerTurn = new JTextField();
		DrawerTurn.setHorizontalAlignment(SwingConstants.LEFT);
		DrawerTurn.setBackground(new Color(255, 255, 255));
		DrawerTurn.setEditable(false);
		DrawerTurn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
		DrawerTurn.setBounds(502, 12, 164, 46);
		contentPane.add(DrawerTurn);
		DrawerTurn.setColumns(10);
		
		wordField = new JTextField();
		wordField.setEditable(false);
		wordField.setBackground(new Color(255, 255, 255));
		wordField.setBounds(125, 12, 362, 46);
		contentPane.add(wordField);
		wordField.setColumns(10);
		mainCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setCurrXY(e.getX(),e.getY());
			}
		});
		mainCanvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if(client.drawTurn){
					draw(e.getX(),e.getY());
				}
			}
		});
		
	}
	
	//Setting initial x and y
	public void setCurrXY(int x, int y){
		currXsend=x;
		currYsend=y;
	}
	
	//Canvas drawing and sending draw packet
	public void draw(int x, int y){
		Graphics g = mainCanvas.getGraphics();
		int oldXsend=currXsend;
		int oldYsend=currYsend;
		currXsend=x;
		currYsend=y;
		g.drawLine(oldXsend, oldYsend, currXsend, currYsend);
		String xAndy = "02"+oldXsend+","+oldYsend+","+x+","+y+","+client.getID();
		sendXY(xAndy);
	}
	
	
	//Timer seconds
	public void setSeconds(int seconds){
		this.seconds=seconds;
	}
}
