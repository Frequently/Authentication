import java.io.IOException;

import javax.swing.JFrame;

public class Server_Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server jarvis = new Server();
		jarvis.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			jarvis.startRunning();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
