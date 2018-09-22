import java.awt.EventQueue;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JOptionPane;
public class RunServer {
	
	private Server server;
	private int port;
	public RunServer(int port) throws UnknownHostException{
		this.port=port;
		new Server(port);
	}
	public static void main(String[] args) throws UnknownHostException{
		int port=Integer.parseInt(JOptionPane.showInputDialog("Enter Server Port Number: "));
		new RunServer(port);
		
	}
	
	
}
