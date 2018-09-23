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
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientGUI extends JFrame implements Runnable{

	private JPanel contentPane;
	private JTextField ChatBox;
	private JTextArea ChatHistory;
	private Client client;
	private Thread constantReceive;
	private Thread run;
	private boolean clientRunning=false;
	private int ID=0;
	
    private Canvas mainCanvas;
    private List<Point> xs = new ArrayList<Point>();
    private List<Point> ys = new ArrayList<Point>();
    private int index=0;
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
		boolean connected=client.connect(IPAddress,port);
		System.out.println("Name: "+name+" IP: "+IPAddress+" Port: "+port);
		String connectPacket = "00"+name;
		client.sendMessage(connectPacket.getBytes());
		clientRunning = true;
		run = new Thread(this,"Client Thread");
		run.start();
	}
	//Appending to chat text area
	public void printToChat(String message){
		ChatHistory.append(message.substring(2)+"\n");
		
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
	//Different prefixes are assigned to different server responses
	//00 for login that sets the ID of the client
	//01 for chat message and it appends to the text area
	//02 for drawings being received and adding the information to the canvas
	//03 for clearing the canvas when the button for clearing the board is cleared
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
	
	public void parseMessage(String message){
		if(message.startsWith("00")){ //Login
			client.setID(Integer.parseInt(message.substring(3,message.length()).trim()));
			sendPacket(client.getName()+" has connected");
		}else if(message.startsWith("01")){ //Chat
			printToChat(message);
		}else if(message.startsWith("02")){ //Drawing
			String[] xy=message.substring(2).split(",");
			int x = Integer.parseInt(xy[0]);
			int y = Integer.parseInt(xy[1].trim());
			//x_s.add(x);
			//y_s.add(y);
			//index++;
			Graphics g = mainCanvas.getGraphics();
			//g.fillOval(x+1, y-1, 2, 2);
			//g.fillOval(x-1, y-1, 2, 2);
			g.fillOval(x, y, 4, 4);
		}else if(message.startsWith("03")){ //Clear Board
			Graphics g = mainCanvas.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
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
		contentPane.setBackground(new Color(240, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//ChatHistory Text Area
		ChatHistory = new JTextArea();
		ChatHistory.setLineWrap(true);
		ChatHistory.setFont(new Font("Monospaced", Font.PLAIN, 13));
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
		
		ClearButton.setBounds(21, 13, 89, 23);
		contentPane.add(ClearButton);
		mainCanvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Graphics g = mainCanvas.getGraphics();
				g.fillOval(e.getX(), e.getY(), 4, 4);
				String xAndy = "02"+e.getX()+","+e.getY()+","+client.getID();
				sendXY(xAndy);
			}
		});
		
	}
}
