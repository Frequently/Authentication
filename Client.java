import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Client extends JFrame {

	private JTextField edittext;
	private JTextArea pane;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private Socket connection;
	String serverip="";
	
	public Client(String ip)
	{
		super("Instant Messenger");
		serverip = ip;
			
		//Creating a UI for the Client
		pane = new JTextArea();
		pane.setEditable(false);
		add(new JScrollPane(pane),BorderLayout.CENTER);
		setSize(300,400);
		setVisible(true);
	}
	
	public void startRunning() throws IOException
	{
		try{
			connecttoServer();
			setupStreams();
			sendMessage(authenticate(), true);
			closeStreams();
		}
		catch(Exception e)
		{
			showMessage("\n Connection Ended \n");
		}
		finally{
			// Good practice to close all the connection when some interruption happens 
			closeStreams();
		}
	}
	
	// Closing all the I/O streams
	private void closeStreams() throws IOException{
		output.close();
		input.close();
		connection.close();
	}
	
	// Attempt to connect to the server
	private void connecttoServer() throws UnknownHostException, IOException{
		showMessage(" \n Attempting to Connect \n");
		connection = new Socket(InetAddress.getByName(serverip),6789);
		showMessage("\n Connected to " + connection.getInetAddress().getHostName()+"\n");
		showMessage(" \n Sharing the authenticationt token \n");
		
	}
	
	// Setting up the I/O Streams
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage(" \n Streams are now connected \n");

	}
	
	// Methods running in the background to display the text on the UI screen
	private void showMessage(String message){
		SwingUtilities.invokeLater( 
				new Runnable()
				{
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pane.append(message);
					}
				});
	}
	
    
	private void sendMessage(String message, boolean ishidden){
		
		try {
			output.writeObject(message);
			output.flush();
			
			if(!ishidden)
				showMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			showMessage(" \n This message was not sent");
		}
	}
	
	// Method to generate Authentication Token
	private String authenticate()
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");  
	   	LocalDateTime now = LocalDateTime.now();  
		
		String macaddress = "5c:26:0a:02:a8:e4";
		String finalString = macaddress + " " + dtf.format(now);
		// We can generate a unique token from Mac address,location, CPU configuration, time etc depending upon our use case
		// In this case I have just used Mac address and time to calculate the unique token
		
		//System.out.println(finalString); 
		String hashValue="";
		try {
			hashValue = generateSHA1(finalString);
			System.out.println("Authenticate token is "+hashValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hashValue;
		
	}
	
	private String generateSHA1(String message) throws Exception {
        return hashString(message, "SHA-1");
    }
	 private String hashString(String message, String algorithm) throws Exception {

	        try {
	            MessageDigest digest = MessageDigest.getInstance(algorithm);
	            byte[] hashedBytes = digest.digest(message.getBytes("UTF-8"));
	 
	            return convertByteArrayToHexString(hashedBytes);
	        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
	            throw new Exception(
	                    "Could not generate hash from String", ex);
	        }
	    }
		
	    private String convertByteArrayToHexString(byte[] arrayBytes) {
	        StringBuffer stringBuffer = new StringBuffer();
	        for (int i = 0; i < arrayBytes.length; i++) {
	            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
	                    .substring(1));
	        }
	        return stringBuffer.toString();
	    }
	
}
