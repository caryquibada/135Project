import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class ServerGUI extends JFrame {

	private JPanel contentPane;
	private JTextField commandLine;
	public JTextArea responseArea = new JTextArea();;
	private Server server;
	/**
	 * Create the frame.
	 * @throws UnknownHostException 
	 */
	
	
	
	public ServerGUI(int port) throws UnknownHostException {
		
		server = new Server(port);
		
	}
	
	public ServerGUI(){
		showWindow();
	}
	
	public void showWindow(){
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		commandLine = new JTextField();
		commandLine.setBounds(10, 305, 564, 45);
		contentPane.add(commandLine);
		commandLine.setColumns(10);
		
		
		responseArea.setWrapStyleWord(true);
		responseArea.setEditable(false);
		responseArea.setBounds(10, 11, 564, 280);
		contentPane.add(responseArea);
	}
	public void writeResponse(String message){
		responseArea.append(message);
	}
}
