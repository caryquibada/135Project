
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginGUI extends JFrame {

	private JPanel contentPane;
	private JTextField NameField;
	private JTextField IPField;
	private JTextField PortField;
	private JLabel lblIpAddress;
	private JLabel lblPort;
	private JTextField WordField;
	public LoginGUI() {
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 495, 227);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		//Name text field
		NameField = new JTextField();
		NameField.setFont(new Font("Arial", Font.PLAIN, 18));
		NameField.setBounds(115, 11, 130, 38);
		contentPane.add(NameField);
		NameField.setColumns(10);
		JLabel lblName = new JLabel("Name:");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblName.setBounds(10, 20, 91, 14);
		contentPane.add(lblName);
		
		//IP text field
		IPField = new JTextField();
		IPField.setFont(new Font("Arial", Font.PLAIN, 15));
		IPField.setText("localhost");
		IPField.setColumns(10);
		IPField.setBounds(115, 75, 130, 44);
		contentPane.add(IPField);
		lblIpAddress = new JLabel("IP Address:");
		lblIpAddress.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblIpAddress.setBounds(10, 88, 151, 14);
		contentPane.add(lblIpAddress);
		
		//Port text field
		PortField = new JTextField();
		PortField.setFont(new Font("Arial", Font.PLAIN, 14));
		PortField.setText("3000");
		PortField.setColumns(10);
		PortField.setBounds(348, 76, 130, 44);
		contentPane.add(PortField);
		lblPort = new JLabel("Port:");
		lblPort.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblPort.setBounds(255, 88, 130, 14);
		contentPane.add(lblPort);
		
		//Desired word text field
		WordField = new JTextField();
		WordField.setFont(new Font("Arial", Font.PLAIN, 14));
		WordField.setColumns(10);
		WordField.setBounds(348, 13, 130, 38);
		contentPane.add(WordField);
		JLabel lblWord = new JLabel("Your word:");
		lblWord.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblWord.setBounds(255, 25, 130, 14);
		contentPane.add(lblWord);
		
		//LOGIN button
		JButton LoginBTN = new JButton("LOGIN");
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
		LoginBTN.setFont(new Font("Arial Black", Font.PLAIN, 18));
		LoginBTN.setBounds(10, 130, 468, 61);
		contentPane.add(LoginBTN);
		
		
	}
	//Calling the ClientGUI class
	public void login(String name, String IPAddress, int port,String word){
		dispose();
		ClientGUI client = new ClientGUI(name,IPAddress,port,word);
	}
}
