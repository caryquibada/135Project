import java.net.InetAddress;

public class ClientStorage {
	
	public String name;
	public InetAddress address;
	public int port;
	public final int ID;
	public int attempt=0;
	
	public ClientStorage(String name, InetAddress address, int port, final int ID){
		this.name=name;
		this.address=address;
		this.port=port;
		this.ID=ID;
	}
	public int getID(){
		return this.ID;
	}
	public int getPort(){
		return this.port;
	}
	public InetAddress getAddress(){
		return this.address;
	}
	public String getName(){
		return this.name;
	}
}
