package com.QuickDraw;
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
	public JLabel bannerField;
	private ImageIcon banner;
	public LoginGUI() throws IOException {
		setResizable(false);
		buildWindow();
	}
	
	public void buildWindow() throws IOException {
		setBackground(new Color(255, 255, 255));
		setForeground(new Color(255, 255, 255));
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 614, 440);
		contentPane = new JPanel();
		contentPane.setVisible(true);
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		//Name text field
		NameField = new JTextField();
		NameField.setFont(new Font("Arial", Font.PLAIN, 18));
		NameField.setBounds(69, 373, 124, 27);
		contentPane.add(NameField);
		NameField.setColumns(10);
		JLabel lblName = new JLabel("Name:");
		lblName.setBackground(Color.WHITE);
		lblName.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		lblName.setBounds(10, 373, 91, 27);
		contentPane.add(lblName);
		
		
		
		//IP text field
		IPField = new JTextField();
		IPField.setFont(new Font("Arial", Font.PLAIN, 12));
		String[] IP;
		try {
			IP = InetAddress.getLocalHost().toString().split("/");
			IPField.setText(IP[1]);
		} catch (UnknownHostException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		IPField.setColumns(10);
		IPField.setBounds(228, 371, 103, 31);
		contentPane.add(IPField);
		lblIpAddress = new JLabel("IP:");
		lblIpAddress.setFont(new Font("Trebuchet MS", Font.BOLD, 16));
		lblIpAddress.setBounds(203, 372, 35, 31);
		contentPane.add(lblIpAddress);
		
		//Port text field
		PortField = new JTextField();
		PortField.setFont(new Font("Arial", Font.PLAIN, 14));
		PortField.setText("3000");
		PortField.setColumns(10);
		PortField.setBounds(389, 375, 56, 27);
		contentPane.add(PortField);
		lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Trebuchet MS", Font.BOLD, 14));
		lblPort.setBounds(345, 372, 49, 32);
		contentPane.add(lblPort);
		
		//LOGIN button
		JButton LoginBTN = new JButton("LOGIN");
		LoginBTN.setForeground(new Color(0, 51, 0));
		LoginBTN.setBackground(new Color(255, 255, 255));
		LoginBTN.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				String name=NameField.getText();
				String IPAddress=IPField.getText();
				int port=Integer.parseInt(PortField.getText());
				login(name,IPAddress,port);
			}
		});
		LoginBTN.setFont(new Font("Yu Gothic UI", Font.BOLD, 26));
		LoginBTN.setBounds(474, 363, 124, 39);
		contentPane.add(LoginBTN);
		BufferedImage img = ImageIO.read(new File("resources/Images/title.png"));
		banner = new ImageIcon(img);
		System.out.println(banner);
		bannerField = new JLabel(banner);
		bannerField.setBounds(0, 0, 610, 416);
		contentPane.add(bannerField);
	} 
	//Calling the ClientGUI class
	public void login(String name, String IPAddress, int port){
		dispose();
		new AvatarWindow(name,IPAddress,port);
	}
}
