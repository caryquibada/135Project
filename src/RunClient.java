import java.awt.EventQueue;
import java.net.UnknownHostException;

public class RunClient {
	public static void main(String[] args) throws UnknownHostException {
		LoginGUI frame = new LoginGUI();
		frame.setVisible(true);
		frame.showTitle();
	}
}
