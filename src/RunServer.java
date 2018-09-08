import java.util.Scanner;
public class RunServer {
	
	private Server server;
	private int port;
	public RunServer(int port){
		this.port=port;
		new Server(port);
	}
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter port number: ");
		int port = sc.nextInt();
		sc.close();
		new RunServer(port);
		
	}
}
