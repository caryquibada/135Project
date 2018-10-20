/*Code and structure for chat, client storage and handling login from user "TheChernoProject" on Youtube
 * Youtube Channel: https://www.youtube.com/user/TheChernoProject
 * Github Repo: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat 
 * Github,Youtube. (2014). Cherno Chat. [online] Available at: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat, https://www.youtube.com/user/TheChernoProject [Accessed 24 Sep. 2018].*/

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
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

import javax.imageio.ImageIO;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.UIManager;

public class ClientGUI extends JFrame implements Runnable{

	private JPanel contentPane;
	private JTextField ChatBox;
	private JTextArea ChatHistory;
	private Client client;
	private Thread constantReceive;
	private Thread run;
	private boolean clientRunning=false,correct=false;
	private int ID=0,seconds=0,defSeconds,drawTime=100,allSeconds;
	private int currXsend,currYsend,currXreceive,currYreceive;
    private JPanel mainCanvas,coverPanel;
    private int turns=0,numberOfReadyPlayers;
    private JTextField timerWindow;
    private JTextField DrawerTurn;
    private JTextField wordField;
    private String guesser,currentWord="";
    private JLayeredPane panel;
    private Image img;
    private List<Point> points = new ArrayList<Point>();
    private List<Point> oldPoints = new ArrayList<Point>();
	public ClientGUI(String name,String IPAddress, int port,String word){
		setBackground(new Color(255, 255, 255));
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
		
		String condition=message.substring(0, 2);
		System.out.println(condition);
		switch(condition) {
			case	"00":
				client.setID(Integer.parseInt(message.substring(3,message.length()).trim()));
				sendPacket(client.getName()+" has connected");
				break;
			case	"01":
				String[] chatMessage=message.split(":");
				if(chatMessage.length>1){ 
					System.out.println(client.drawer);
					if(drawTime<=defSeconds&&client.myTurn&&seconds!=0){//When it's time to guess
						String[] splitGuess=message.split(":");
						if(splitGuess[1].trim().equals(currentWord)) {
							printToChat("01Correct!");
							String win="15Win";
							client.sendMessage(win.getBytes());
							seconds=1;
						}else {
							printToChat("01Incorrect! You only have seconds "+drawTime+" left!");
						}
					}else {
						printToChat(message);
					}
				}else {
					printToChat(message);
				}
				break;
			case	"02":
				String[] xy=message.substring(2).split(",");
				
				int oldx=Integer.parseInt(xy[0]);
				int oldy=Integer.parseInt(xy[1]);
				int x = Integer.parseInt(xy[2]);
				int y = Integer.parseInt(xy[3].trim());
				Point oldP = new Point(oldx+20,oldy+100);
				Point p=new Point(x+20,y+100);
				oldPoints.add(oldP);
				points.add(p);
				repaint();
				break;
			case	"03":
				clearCanvas();
				break;
			case	"06":
				numberOfReadyPlayers=Integer.parseInt(message.substring(2).trim());
				if(numberOfReadyPlayers==4){
					seconds=5*4;
					allSeconds=5*4;
					defSeconds=5;
				}else if(numberOfReadyPlayers==5){
					seconds=4*5;
					allSeconds=4*5;
					defSeconds=4;
				}else if(numberOfReadyPlayers>5){
					seconds=3*numberOfReadyPlayers;
					allSeconds=3*numberOfReadyPlayers;
					defSeconds=3;
				}
				countDown();
				break;
			case	"07":
				guesser=message.substring(2).trim();
				System.out.println(guesser);
				if(guesser.equals(client.getName())){
					System.out.println("Guesser");
					client.drawer=false;
					client.myTurn=true;
				}else{
					client.myTurn=false;
					client.drawer=true;
				}
				break;
			case	"08":
				String[] drawers= message.trim().split("\t");
				for(int i=0;i<drawers.length;i++){
					if(drawers[i].equals(client.getName())){
						client.drawer=true;
						client.myTurn=false;
					}
				}
				break;
			case	"09":
				String player=message.substring(2).trim();
				if(player.equals(client.getName())){
					client.drawTurn=true;
					DrawerTurn.setText("Your turn");
				}else{
					client.drawTurn=false;
					DrawerTurn.setText(player+"'s turn");
				}
				break;
			case	"13":
				if(client.drawer){
					setCurrentWord(message.substring(2).trim());
					wordField.setText("\tDraw: "+currentWord);
				}else{
					setCurrentWord(message.substring(2).trim());
					String guesserWord="\t";
					for(int i=0;i<currentWord.length();i++){
						guesserWord=guesserWord+" _";
					}
					wordField.setText(guesserWord);
				}
				break;
			case	"14":
				repaint();
				client.drawer=true;
				DrawerTurn.setText(message.substring(2).trim()+" is guessing");
				break;
			case	"15":
				correct=true;
				break;
			default:
				break;
		}
	}
	
	//Countdown Timer
	public void countDown(){
		client.sendMessage("10Draw".getBytes()); //Draw Turn
		Timer timer = new Timer();
		setSeconds(allSeconds);
		timer.schedule(new TimerTask(){
			int turns=numberOfReadyPlayers-1;
			int drawTurns=1;
			
			public void run(){
				drawTime=seconds-(defSeconds*turns);
				timerWindow.setText(drawTime+"");
				if(drawTime==0 && drawTurns<numberOfReadyPlayers-1){
					client.sendMessage("10Draw".getBytes()); //Draw Turn
					drawTurns++;
					turns--;
				}else if(drawTime==0&& drawTurns==numberOfReadyPlayers-1){
					client.sendMessage("11ChangeGuessTurn".getBytes());
					drawTurns=1;
					turns--;
				}
				seconds--;
				if(seconds==0){
					if(correct) {
						printToChat("02Nice");
					}else {
						printToChat("02Time's up! The word was "+currentWord);
					}
					client.sendMessage("16Unready".getBytes());
					clearCanvas();
					timerWindow.setText(allSeconds+"");
					setSeconds(allSeconds);
					drawTime=allSeconds;
					timer.cancel();
				}
			}
		},3000,1000);
	}
	
	public void isCurrentDrawer(String nameMessage){
		if(client.getName().equals(nameMessage)){
			client.myTurn=true;
		}else{
			client.myTurn=false;
		}
	}
	
	public void clearCanvas() {
		Graphics g = mainCanvas.getGraphics();
		oldPoints.clear();
		points.clear();
		g.setColor(new Color(247,247,242));
		g.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
	}
	
	//GUI stuff(ignore)
	public void showWindow() {
		setVisible(true);
		setTitle("Sketch Window");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(238, 245, 219));
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
		

		mainCanvas = new JPanel();
		mainCanvas.setBackground(new Color(247,247,242));
		mainCanvas.setBounds(20, 70, 690, 490);
		contentPane.add(mainCanvas);
		mainCanvas.setLayout(null);
		
		System.out.println(System.getProperty("user.dir"));
		mainCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setCurrXY(e.getX(),e.getY());
			}
		});
		try {
			img = ImageIO.read(new File("src/Images/pencil.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("src/Images/pencil.png");
		Cursor c = toolkit.createCustomCursor(image , new Point(contentPane.getX(), 
		           contentPane.getY()), "img");
		contentPane.setCursor (c);
		mainCanvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if(client.drawTurn&&e.getX()>0&&e.getY()>0&&e.getX()<mainCanvas.getWidth()&&e.getY()<mainCanvas.getHeight()){
					draw(e.getX(),e.getY());
				}
			}
		});
		
		timerWindow = new JTextField();
		timerWindow.setHorizontalAlignment(SwingConstants.CENTER);
		timerWindow.setBackground(new Color(245, 255, 250));
		timerWindow.setEditable(false);
		timerWindow.setFont(new Font("Oxygen", Font.BOLD, 18));
		timerWindow.setBounds(109, 12, 55, 47);
		contentPane.add(timerWindow);
		timerWindow.setColumns(10);
		
		JButton readyBtn = new JButton("Ready");
		readyBtn.setFont(new Font("Verdana", Font.PLAIN, 16));
		readyBtn.setForeground(UIManager.getColor("textText"));
		readyBtn.setBackground(new Color(204, 255, 255));
		readyBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				client.ready="true";
				client.sendMessage(("05"+client.getName()).getBytes());
			}
		});
		readyBtn.setBounds(10, 11, 89, 47);
		contentPane.add(readyBtn);
		
		DrawerTurn = new JTextField();
		DrawerTurn.setHorizontalAlignment(SwingConstants.LEFT);
		DrawerTurn.setBackground(new Color(255, 255, 255));
		DrawerTurn.setEditable(false);
		DrawerTurn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
		DrawerTurn.setBounds(546, 13, 164, 46);
		contentPane.add(DrawerTurn);
		DrawerTurn.setColumns(10);
		
		wordField = new JTextField();
		wordField.setFont(new Font("Verdana", Font.PLAIN, 26));
		wordField.setEditable(false);
		wordField.setBackground(new Color(255, 255, 255));
		wordField.setBounds(174, 13, 362, 46);
		contentPane.add(wordField);
		wordField.setColumns(10);
		
	}
	
	//Setting initial x and y
	public void setCurrXY(int x, int y){
		currXsend=x;
		currYsend=y;
	}
	
	//Canvas drawing and sending draw packet
	public void draw(int x, int y){
		int oldXsend=currXsend;
		int oldYsend=currYsend;
		currXsend=x;
		currYsend=y;
		Point oldP = new Point(oldXsend+20,oldYsend+100);
		Point p=new Point(x+20,y+100);
		oldPoints.add(oldP);
		points.add(p);
		repaint();
		String xAndy = "02"+oldXsend+","+oldYsend+","+x+","+y+","+client.getID();
		sendXY(xAndy);
	}
	
	public void paint(Graphics g){
		if(client.drawer){
			g.setColor(Color.BLACK);
			int i = 0;
			System.out.println(points.size());
		    while (i < points.size()-1) {
		      Point p0 = (Point) (oldPoints.get(i++));
		      Point p1 = (Point) (points.get(i++));
		      int oldx = p0.x;
		      int oldy = p0.y;
		      int x = p1.x;
		      int y = p1.y;
		      g.drawLine(oldx, oldy, x, y);
		    }
		}else{
			g.setColor(new Color(247,247,242));
			g.fillRect(20, 100, mainCanvas.getWidth(), mainCanvas.getHeight());
		}
	}
	
	public void setCurrentWord(String currentWord) {
		this.currentWord=currentWord;
	}
	
	
	//Timer seconds
	public void setSeconds(int seconds){
		this.seconds=seconds;
	}
}
