import java.io.IOException;

import javax.swing.JFrame;

public class Client_Main {

	public static void main(String[] args) throws IOException {
		
		// Connecting to localhost
		Client user = new Client("127.0.0.1");
		user.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		user.startRunning();

	}

}
