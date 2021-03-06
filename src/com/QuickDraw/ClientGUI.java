package com.QuickDraw;
/*Code and structure for chat, client storage and handling login from user "TheChernoProject" on Youtube
 * Youtube Channel: https://www.youtube.com/user/TheChernoProject
 * Github Repo: https://github.com/TheCherno/ChernoChat/tree/master/com/thecherno/chernochat 
 * Github,Youtube. (2014). Cherno Chat. [online] Available at: https://github.com/TheCherno/ChernoChat/tree/master/com/thecherno/chernochat, https://www.youtube.com/user/TheChernoProject [Accessed 24 Sep. 2018].*/

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
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.MouseEvent;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.MouseAdapter;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.DatagramPacket;

import javax.swing.SwingConstants;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import javax.swing.border.LineBorder;

public class ClientGUI extends JFrame implements Runnable{

	private JPanel contentPane;
	private JTextField ChatBox;
	private JTextArea ChatHistory;
	private Client client;
	private Thread constantReceive;
	private Thread run;
	private boolean clientRunning=false,correct=false,error=false;
	private int ID=0,seconds=0,defSeconds,drawTime=100,allSeconds;
	private int currXsend,currYsend,currXreceive,currYreceive;
    private JPanel mainCanvas,coverPanel;
    private int turns=0,numberOfReadyPlayers,voteCountdown=0, roundCount, showCount;
    private String winnerName="";
    private JTextField timerWindow;
    private JTextField DrawerTurn;
    private JTextField wordField;
    private String guesser,currentWord="",latestDrawer,filename="";
    private JLayeredPane panel;
    private Image img;
    private List<Point> points = new ArrayList<Point>();
    private List<Point> oldPoints = new ArrayList<Point>();
    private JTextField nextField;
    private JTextField scoreField;
    private String[] drawers;
    private AvatarWindow avatar;
    private JPanel AvatarPanel;
	private JProgressBar progressbar;
    private List<JLabel> panels= new ArrayList<JLabel>();
    private List<String> names = new ArrayList<String>();
    private List<JLabel> labels = new ArrayList<JLabel>();
    
	public ClientGUI(String name,String IPAddress, int port){
		setTitle("QuickDraw! \t"+name+"'s Client");
		setBackground(new Color(255, 255, 255));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				deleteImages();
				String disconnectMessage="04"+name;
				client.sendMessage(disconnectMessage.getBytes());
				error=true;
				while(error) {
					updatePanels();
				}
			}
		});
		showWindow();
		client = new Client(name,IPAddress,port);
		client.drawTurn=true;
		boolean connected=client.connect(IPAddress,port);
		String connectPacket = "00"+name;
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
		constantReceive = new Thread("constantReceive"){
			public void run(){
				while(clientRunning){
					DatagramPacket message=client.receive();
					try {
						parseMessage(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		constantReceive.start();
	}
	//Different prefixes are assigned to different server responses
	public void parseMessage(DatagramPacket msg) throws IOException{
		String message = new String(msg.getData());
		String condition=message.substring(0, 2);
		switch(condition) {
			case	"00":
				client.setID(Integer.parseInt(message.substring(3,message.length()).trim()));
				BufferedImage img1 = ImageIO.read(new File("resources/Images/"+client.getName()+".jpg"));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(img1, "jpg", baos);
				baos.flush();
				byte[] buffer  = baos.toByteArray();
				client.sendMessage(buffer);
				sendPacket(client.getName()+" has connected");
				
				break;
			case	"01":
				String[] chatMessage=message.split(":");
				if(chatMessage.length>1){ 
					if(client.myTurn&&seconds!=0){//When it's time to guess
						String[] splitGuess=message.split(":");
						if(splitGuess[1].trim().toLowerCase().equals(currentWord.toLowerCase())) {
							printToChat("01Correct!");
							String win="15Win";
							client.sendMessage(win.getBytes());
							seconds=1;
						}else if(client.guesser&&allSeconds!=seconds){
							printToChat("01Incorrect!");
						}else if(client.drawer && allSeconds!=seconds){
							printToChat(message);
						}else {
							printToChat(message);
						}
					}else {
						printToChat(message);
					}
				}else {
					if(message.endsWith("has connected")) {
						String[] temp=message.split(" ");
						latestDrawer = temp[0];
					}
						
					printToChat(message);
				}
				break;
			case	"02":
				String[] xy=message.substring(2).split(",");
				int oldx=Integer.parseInt(xy[0]);
				int oldy=Integer.parseInt(xy[1]);
				int x = Integer.parseInt(xy[2]);
				int y = Integer.parseInt(xy[3].trim());
				Graphics g = mainCanvas.getGraphics();
				g.drawLine(oldx, oldy, x, y);
				break;
			case	"03":
				clearCanvas();
				break;
			case	"06":
				gameStartCountdown();
				numberOfReadyPlayers=Integer.parseInt(message.substring(2).trim());
				if(numberOfReadyPlayers==4){
					seconds=10*4;
					allSeconds=10*4;
					defSeconds=10;
				}else if(numberOfReadyPlayers==5){
					seconds=7*5;
					allSeconds=7*5;
					defSeconds=7;
				}else if(numberOfReadyPlayers>5){
					seconds=5*numberOfReadyPlayers;
					allSeconds=5*numberOfReadyPlayers;
					defSeconds=5;
				}
				
				break;
			case	"07":
				guesser=message.substring(2).trim();
				if(guesser.equals(client.getName())){
					client.guesser=true;
					client.drawer=false;
					client.myTurn=true;
				}else{
					client.guesser=false;
					client.myTurn=false;
					client.drawer=true;
				}
				break;
			case	"08":
				drawers= message.trim().split("\t");
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
				client.drawer=true;
				DrawerTurn.setText(message.substring(2).trim()+" is guessing");
				break;
			case	"15":
				correct=true;
				seconds=1;
				break;
			case	"17":
				String[] next= message.substring(2).trim().split(":");	
				if(next[1].equals(client.getName())) {
					nextField.setText("You're next");
				}else {
					nextField.setText(message.substring(2).trim());
				}
				
				break;
			case	"20":
				printToChat("01Client closing since server is closed.");
				closeCountdown();
				break;
			case	"22":
				client.sendMessage("22".getBytes());
				break;
			case	"23":
				String[] winners=message.substring(2).trim().split("\t");
				printToChat("01Most votes-");
				for(int i=1;i<winners.length;i++) {
					printToChat("01"+winners[i]);
				}
				for(int i=1;i<winners.length;i++) {
					if(winners[i].equals(client.getName())) {
						if(correct) {
							correct=false;
							client.score = client.score+Integer.parseInt(winners[0]);
						}else {
							client.score= client.score-Integer.parseInt(winners[0]);
						}
					}
				}
				correct=false;
				scoreField.setText("Your score: "+client.score );
				client.sendMessage(("18"+client.getName()+":"+client.score).getBytes());
				client.sendMessage("19".getBytes());
				
				break;
			case	"24":
				String[] namesSplit=message.substring(2).trim().split("\t");
				names.clear();
				panels.clear();
				labels.clear();
				for(int i=0;i<namesSplit.length;i++) {
					names.add(namesSplit[i]);
					panels.add(new JLabel());
					labels.add(new JLabel(names.get(i)));
				}
				error=true;
				while(error) {
					updatePanels();
				}
				updatePanels();
				client.sendMessage(("25").getBytes());
				break;
			case	"25":
				roundCount=Integer.parseInt(message.substring(2).trim());
				printToChat("01Round "+roundCount+" of " + names.size() );
				break;
			case	"26":
				printToChat("01Game Ended");
				winnerName = message.substring(2).trim();
				int choice=endScreen(winnerName);
				if(choice==0) {
					client.sendMessage("27Y".getBytes());
				}else {
					client.sendMessage("27N".getBytes());
				}
				break;
			case	"28":
				filename=message.substring(2).trim();
				break;
			case	"29":
				
				break;
			case	"30":
				printToChat("01Restart vote wasn't unanimous. Closing client.");
				endCountdown();
				break;
			case	"31":
				client.score=0;
				names.clear();	
				scoreField.setText("Your score: "+client.score );
				client.sendMessage(("18"+client.getName()+":"+client.score).getBytes());
				client.sendMessage("19".getBytes());
				printToChat("01Game has restarted. Ready up to start round.");
				break;
			default:
				System.out.println(filename);
				byte[] input=msg.getData();
			    ByteArrayInputStream input_stream= new ByteArrayInputStream(input);
			    BufferedImage img = ImageIO.read(input_stream);
			    try {
			    	 ImageIO.write(img, "jpg", new File("resources/Images/"+filename+".jpg"));
			    }catch(Exception e) {
			    	
			    }
			    updatePanels();
			    break;
		}
	}
	
	public void deleteImages() {
		File dir = new File("resources/Images");
		String[] filenames=dir.list();
		for(int i=0;i<filenames.length;i++) {
			
			if(filenames[i].endsWith(".jpg")) {
				File deleteFile= new File("resources/Images/"+filenames[i]);
				System.out.println(deleteFile.exists());
				System.out.println(deleteFile.delete());
			}
		}
	}
	public void updatePanels() {
		AvatarPanel.removeAll();
		for(int i=0;i<names.size();i++) {
			panels.get(i).setBounds(0, 0+((AvatarPanel.getHeight()/6)*i),AvatarPanel.getWidth(), (AvatarPanel.getHeight())/6);
			AvatarPanel.add(panels.get(i));
			panels.get(i).add(labels.get(i));
			panels.get(i).setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			labels.get(i).setBounds(AvatarPanel.getHeight()/6,0,AvatarPanel.getWidth()-(AvatarPanel.getHeight()/6),AvatarPanel.getHeight()/6);
			String[] filenames=names.get(i).trim().split("-");
			Image img;
			try {
				img=ImageIO.read(new File("resources/Images/"+filenames[0]+".jpg")); 
				img=img.getScaledInstance(panels.get(i).getHeight(),panels.get(i).getHeight(),Image.SCALE_SMOOTH);
				panels.get(i).setIcon(new ImageIcon(img));
			} catch (IIOException e) {
				error=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(IndexOutOfBoundsException e) {
				error=true;
			}
			
		}
		error=false;
	}
	
	public void showVoting() {
		drawers[0]=drawers[0].substring(2);
	    int rc = JOptionPane.showOptionDialog(null, "Vote for player", "Voting",
	        JOptionPane.WARNING_MESSAGE, 0, null, drawers, null);
	    client.sendMessage(("21"+drawers[rc]).getBytes());
	}
	
	public void gameStartCountdown() {
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask(){
			int countdown=3;
			public void run() {
				printToChat("01Game starting in:"+countdown);
				countdown--;
				if(countdown==0) {
					countDown();
					timer.cancel();
				}
					
			}
		},0,1000);
	}
	
	//Countdown Timer
	public void countDown(){
		ChatHistory.setText("");
		client.sendMessage("10Draw".getBytes()); //Draw Turn
		Timer timer = new Timer();
		setSeconds(allSeconds);
		progressbar.setMaximum(defSeconds);
		timer.schedule(new TimerTask(){
			int turns=numberOfReadyPlayers-1;
			int drawTurns=1;
			int progress=0;
			public void run(){
				drawTime=seconds-(defSeconds*turns);
				progressbar.setValue(++progress);
				progressbar.setString(progress+"");
				Graphics g= timerWindow.getGraphics();
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, timerWindow.WIDTH, timerWindow.HEIGHT);
				switch (drawTime) {
					case	0:
						Image img0;
						try {
							img0 = ImageIO.read(new File("resources/Images/0.png"));
							g.drawImage(img0, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	1:
						Image img1;
						try {
							img1 = ImageIO.read(new File("resources/Images/1.png"));
							g.drawImage(img1, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	2:
						Image img2;
						try {
							img2 = ImageIO.read(new File("resources/Images/2.png"));
							g.drawImage(img2, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	3:
						Image img3;
						try {
							img3 = ImageIO.read(new File("resources/Images/3.png"));
							g.drawImage(img3, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	4:
						Image img4;
						try {
							img4 = ImageIO.read(new File("resources/Images/4.png"));
							g.drawImage(img4, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	5:
						Image img5;
						try {
							img5 = ImageIO.read(new File("resources/Images/5.png"));
							g.drawImage(img5, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	6:
						Image img6;
						try {
							img6 = ImageIO.read(new File("resources/Images/6.png"));
							g.drawImage(img6, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	7:
						Image img7;
						try {
							img7 = ImageIO.read(new File("resources/Images/7.png"));
							g.drawImage(img7, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	8:
						Image img8;
						try {
							img8 = ImageIO.read(new File("resources/Images/8.png"));
							g.drawImage(img8, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	9:
						Image img9;
						try {
							img9 = ImageIO.read(new File("resources/Images/9.png"));
							g.drawImage(img9, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case	10:
						Image img10;
						try {
							img10 = ImageIO.read(new File("resources/Images/10.png"));
							g.drawImage(img10, 0, 0, null);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						break;
				}
				if(drawTime==0 && drawTurns<numberOfReadyPlayers-1){
					progressbar.setValue(0);
					progress=0;
					client.sendMessage("10Draw".getBytes()); //Draw Turn
					drawTurns++;
					turns--;
				}else if(drawTime==0&& drawTurns==numberOfReadyPlayers-1){
					progressbar.setValue(0);
					progress=0;
					client.sendMessage("11ChangeGuessTurn".getBytes());
					drawTurns=1;
					turns--;
					nextField.setText("Guessing Turn");
				}
				seconds--;
				if(seconds==0){
					progressbar.setValue(0);
					progress=0;
					if(correct) {
						winWindow();
						if(client.guesser) {
							correct=false;
							client.score=client.score+3;
						}else {
							client.score=client.score+2;
						}
					}else {
						loseWindow(currentWord);
						client.score=client.score+1;
					}
					client.sendMessage("16Unready".getBytes());
					clearFields();
					setSeconds(allSeconds);
					drawTime=allSeconds;
					Image clear;
					try {
						clear = ImageIO.read(new File("resources/Images/clear.png"));
						g.drawImage(clear, 0, 0, null);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!client.guesser) {
						showVoting();
					}
					client.guesser=false;
					
					timer.cancel();
				}
			}
		},0,1000);
	}
	
	public void clearFields() {
		clearCanvas();
		DrawerTurn.setText("");
		timerWindow.setText("");
		nextField.setText("");
		ChatHistory.setText("");
		nextField.setText("");
		wordField.setText("Ready up!");
		scoreField.setText("Your score: "+client.score );
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
		g.setColor(new Color(247,247,242));
		g.fillRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
	}
	
	//GUI stuff(ignore)
	public void showWindow() {
		setVisible(true);
		//setTitle("Sketch Window");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1369, 700);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(238, 245, 219));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		ChatHistory = new JTextArea();
		ChatHistory.setBounds(970, 71, 164, 531);
		contentPane.add(ChatHistory);
		ChatHistory.setLineWrap(true);
		ChatHistory.setFont(new Font("Dialog", Font.PLAIN, 11));
		ChatHistory.setEditable(false);
		
		JScrollPane ChatScroll= new JScrollPane(ChatHistory);
		ChatScroll.setBounds(1189, 70, 164, 533);
		contentPane.add(ChatScroll);
		DefaultCaret caret = (DefaultCaret)ChatHistory.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		//ChatBox Text Field
		ChatBox = new JTextField();
		ChatBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent KE) {
				if(KE.getKeyCode()==KeyEvent.VK_ENTER&&!ChatBox.getText().isEmpty()){
					sendPacket(client.getName()+": "+ChatBox.getText());
				}
			}
		});
		ChatBox.setBounds(1189, 613, 164, 47);
		contentPane.add(ChatBox);
		ChatBox.setColumns(10);
		
		contentPane.add(ChatBox);
		ChatBox.setColumns(10);
		

		mainCanvas = new JPanel();
		mainCanvas.setBackground(new Color(247,247,242));
		mainCanvas.setBounds(239, 70, 940, 590);
		mainCanvas.setBorder(new BevelBorder(BevelBorder.LOWERED));
		contentPane.add(mainCanvas);
		mainCanvas.setLayout(null);
		
		mainCanvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setCurrXY(e.getX(),e.getY());
			}
			@Override
			public void mouseReleased(MouseEvent arg0) {
				setCurrXY(0,0);
			}
		});
		try {
			img = ImageIO.read(new File("resources/Images/pencil.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage("resources/Images/pencil.png");
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
		timerWindow.setBounds(405, 12, 55, 47);
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
				updatePanels();
			}
		});
		readyBtn.setBounds(1189, 12, 164, 47);
		contentPane.add(readyBtn);
		
		DrawerTurn = new JTextField();
		DrawerTurn.setHorizontalAlignment(SwingConstants.LEFT);
		DrawerTurn.setBackground(new Color(255, 255, 255));
		DrawerTurn.setEditable(false);
		DrawerTurn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
		DrawerTurn.setBounds(894, 12, 154, 47);
		contentPane.add(DrawerTurn);
		DrawerTurn.setColumns(10);
		
		wordField = new JTextField();
		wordField.setFont(new Font("Dialog", Font.PLAIN, 20));
		wordField.setEditable(false);
		wordField.setBackground(new Color(255, 255, 255));
		wordField.setBounds(470, 11, 414, 47);
		contentPane.add(wordField);
		wordField.setColumns(10);
		
		nextField = new JTextField();
		nextField.setHorizontalAlignment(SwingConstants.LEFT);
		nextField.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
		nextField.setEditable(false);
		nextField.setColumns(10);
		nextField.setBackground(Color.WHITE);
		nextField.setBounds(1058, 11, 121, 48);
		contentPane.add(nextField);
		
		scoreField = new JTextField();
		scoreField.setHorizontalAlignment(SwingConstants.LEFT);
		scoreField.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
		scoreField.setEditable(false);
		scoreField.setColumns(10);
		scoreField.setBackground(Color.WHITE);
		scoreField.setBounds(241, 12, 154, 47);
		contentPane.add(scoreField);
		
		AvatarPanel = new JPanel();
		AvatarPanel.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		AvatarPanel.setBackground(Color.WHITE);
		AvatarPanel.setBounds(10, 70, 209, 590);
		AvatarPanel.setLayout(null);
		contentPane.add(AvatarPanel);
		
		//ChatHistory Text Area
		
		progressbar = new JProgressBar();
		progressbar.setBackground(Color.WHITE);
		progressbar.setFont(new Font("Tahoma", Font.BOLD, 14));
		progressbar.setBounds(10, 11, 209, 48);
		progressbar.setMinimum(0);
		progressbar.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		contentPane.add(progressbar);
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
		Graphics g = mainCanvas.getGraphics();
		g.drawLine(oldXsend, oldYsend, x, y);
		String xAndy = "02"+oldXsend+","+oldYsend+","+x+","+y;
		sendXY(xAndy);
	}
	public void setCurrentWord(String currentWord) {
		this.currentWord=currentWord;
	}
	
	public void winWindow() {
		JOptionPane winDialog= new JOptionPane("Win!");
		winDialog.showMessageDialog(contentPane,"Correct!", "Correct!", JOptionPane.INFORMATION_MESSAGE);
	}
	//Timer seconds
	public void setSeconds(int seconds){
		this.seconds=seconds;
	}
	public void loseWindow(String word) {
		JOptionPane loseDialog= new JOptionPane("Wrong!");
		loseDialog.showMessageDialog(contentPane, "The word was "+word,"Time's up" , JOptionPane.ERROR_MESSAGE);

	}
	public void closeCountdown() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			int countdown=3;
			public void run() {
				printToChat("01"+countdown+"");
				countdown--;
				if(countdown==0) {
					System.exit(0);
					timer.cancel();
				}
			}
		},0,1000);
	}
	public void endCountdown() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			int countdown=3;
			public void run() {
				printToChat("01"+countdown+"");
				countdown--;
				if(countdown==0) {
					File delete = new File("resources/Images/"+client.getName()+".jpg");
					delete.delete();
					System.exit(0);
					timer.cancel();
				}
			}
		},0,1000);
	}
	private int endScreen(String winnerName) {
		ImageIcon iconic = new ImageIcon("resources/Images/"+filename+".jpg");
		JOptionPane.showMessageDialog(null, "Congratulations!\n"+ winnerName, "Best players today!!", JOptionPane.INFORMATION_MESSAGE, iconic );
		int choice=JOptionPane.showConfirmDialog(null, "Want to play again?", "Restart?", JOptionPane.YES_NO_OPTION);
		System.out.println(choice);
		return choice;
	}
}
