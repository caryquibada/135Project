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
import java.awt.event.MouseMotionAdapter;
import java.util.LinkedList;
import java.awt.event.MouseEvent;

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
    private LinkedList<Integer> x_s = new LinkedList<Integer>();
    private LinkedList<Integer> y_s = new LinkedList<Integer>();
    private int index=0;
	public ClientGUI(String name,String IPAddress, int port,String word){
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
	public void printToChat(String message){
		ChatHistory.append(message.substring(2)+"\n");
	}

	public void sendPacket(String chatMessage){
		chatMessage = "01"+chatMessage;
		client.sendMessage(chatMessage.getBytes());
		ChatBox.setText("");
	}
	public void sendXY(String XY){
		client.sendMessage(XY.getBytes());
	}
	public void run() {
		constantReceive();
	}
	public void constantReceive(){
		System.out.println(clientRunning);
		constantReceive = new Thread("constantReceive"){
			public void run(){
				while(clientRunning){
					String message=client.receive();
					if(message.startsWith("00")){ //Login
						client.setID(Integer.parseInt(message.substring(3,message.length()).trim()));
						printToChat(message);
					}else if(message.startsWith("01")){ //Chat
						printToChat(message);
					}else if(message.startsWith("02")){ //Drawing
						String[] xy=message.substring(2).split(",");
						int x = Integer.parseInt(xy[0]);
						int y = Integer.parseInt(xy[1].trim());

						x_s.add(x);
						y_s.add(y);
						index++;
						Graphics g = mainCanvas.getGraphics();
						g.fillOval(x+1, y-1, 2, 2);
						g.fillOval(x-1, y-1, 2, 2);
						g.fillOval(x, y, 4, 4);
					}
				}
			}
		};
		constantReceive.start();
	}
	

	//GUI stuff(ignore)
	public void showWindow() {
		setVisible(true);
		setTitle("Sketch Window");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//ChatHistory Text Area
		ChatHistory = new JTextArea();
		ChatHistory.setEditable(false);
		
		JScrollPane ChatScroll= new JScrollPane(ChatHistory);
		ChatScroll.setBounds(720, 11, 164, 491);
		contentPane.add(ChatScroll);
		
		//ChatBox Text Field
		ChatBox = new JTextField();
		ChatBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent KE) {
				if(KE.getKeyCode()==KeyEvent.VK_ENTER){
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
		mainCanvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Graphics g = mainCanvas.getGraphics();
				g.fillOval(e.getX(), e.getY(), 3, 3);
				String xAndy = "02"+e.getX()+","+e.getY();
				sendXY(xAndy);
			}
		});
		
	}
}
