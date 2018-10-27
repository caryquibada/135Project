/*Code and structure for chat, client storage and handling login from user "TheChernoProject" on Youtube
 * Youtube Channel: https://www.youtube.com/user/TheChernoProject
 * Github Repo: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat 
 * Github,Youtube. (2014). Cherno Chat. [online] Available at: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat, https://www.youtube.com/user/TheChernoProject [Accessed 24 Sep. 2018].*/

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.omg.CORBA.portable.InputStream;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginGUI extends JFrame {

	public JPanel contentPane;
	private JTextField NameField;
	private JTextField IPField;
	private JTextField PortField;
	private JLabel lblIpAddress;
	private JLabel lblPort;
	private JTextField WordField;
	public JPanel panel;
	public LoginGUI() throws UnknownHostException {
		buildWindow();
	}
	
	public void buildWindow() {
		setBackground(new Color(255, 255, 255));
		setForeground(new Color(255, 255, 255));
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1150, 700);
		contentPane = new JPanel();
		contentPane.setVisible(true);
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		//Name text field
		NameField = new JTextField();
		NameField.setFont(new Font("Arial", Font.PLAIN, 18));
		NameField.setBounds(567, 402, 130, 38);
		contentPane.add(NameField);
		NameField.setColumns(10);
		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblName.setBounds(462, 413, 91, 14);
		contentPane.add(lblName);
		
		
		
		//IP text field
		IPField = new JTextField();
		IPField.setFont(new Font("Arial", Font.PLAIN, 15));
		String[] IP;
		try {
			IP = InetAddress.getLocalHost().toString().split("/");
			IPField.setText(IP[1]);
		} catch (UnknownHostException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IPField.setColumns(10);
		IPField.setBounds(567, 500, 130, 44);
		contentPane.add(IPField);
		lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblIpAddress.setBounds(462, 513, 151, 14);
		contentPane.add(lblIpAddress);
		
		//Port text field
		PortField = new JTextField();
		PortField.setFont(new Font("Arial", Font.PLAIN, 14));
		PortField.setText("3000");
		PortField.setColumns(10);
		PortField.setBounds(567, 555, 130, 44);
		contentPane.add(PortField);
		lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblPort.setBounds(462, 567, 130, 14);
		contentPane.add(lblPort);
		
		//Desired word text field
		WordField = new JTextField();
		WordField.setFont(new Font("Arial", Font.PLAIN, 14));
		WordField.setColumns(10);
		WordField.setBounds(567, 451, 130, 38);
		contentPane.add(WordField);
		JLabel lblWord = new JLabel("Your word:");
		lblWord.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblWord.setBounds(462, 460, 130, 14);
		contentPane.add(lblWord);
		
		//LOGIN button
		JButton LoginBTN = new JButton("LOGIN");
		LoginBTN.setForeground(new Color(0, 51, 0));
		LoginBTN.setBackground(new Color(255, 255, 255));
		LoginBTN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				String name=NameField.getText();
				String IPAddress=IPField.getText();
				String word=WordField.getText();
				int port=Integer.parseInt(PortField.getText());
				login(name,IPAddress,port,word);
			}
		});
		LoginBTN.setFont(new Font("Yu Gothic UI", Font.BOLD, 26));
		LoginBTN.setBounds(462, 610, 235, 61);
		contentPane.add(LoginBTN);
		
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 0, 1144, 391);
		contentPane.add(panel);
	} 
	public void showTitle() {
		Graphics g=panel.getGraphics();
		
		Image img = new ImageIcon("Images/title.png").getImage();
		g.drawImage(img, 0, 0, null);
	}
	//Calling the ClientGUI class
	public void login(String name, String IPAddress, int port,String word){
		dispose();
		new AvatarWindow(name,IPAddress,port,word);
	}
}
