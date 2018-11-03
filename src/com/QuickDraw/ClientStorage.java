package com.QuickDraw;
/*Code and structure for chat, client storage and handling login from user "TheChernoProject" on Youtube
 * Youtube Channel: https://www.youtube.com/user/TheChernoProject
 * Github Repo: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat 
 * Github,Youtube. (2014). Cherno Chat. [online] Available at: https://github.com/TheCherno/ChernoChat/tree/master/src/com/thecherno/chernochat, https://www.youtube.com/user/TheChernoProject [Accessed 24 Sep. 2018].*/

import java.net.InetAddress;

public class ClientStorage {
	
	public String name;
	public InetAddress address;
	public int port;
	public final int ID;
	public int attempt=0,score=0;
	public String ready="false";
	public boolean drawer=true;
	public String word;
	public ClientStorage(String name, InetAddress address, int port, final int ID,String word){
		this.name=name;
		this.address=address;
		this.port=port;
		this.ID=ID;
		this.word=word;
	}
	public String getWord(){
		return this.word;
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
