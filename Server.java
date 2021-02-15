import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame{
	

	private ServerSocket Server;
	private JTextField edittext;
	private JTextArea pane;
	private Socket connection;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	public Server(){
		super("Server");
		
		//Creating a UI for the Server
		pane = new JTextArea();
		pane.setEditable(false);
		add(new JScrollPane(pane));
		setSize(300,400);
		setVisible(true);
	}
	
	public void startRunning() throws IOException
	{
		Server = new ServerSocket(6789,100);
		while(true){
		try{
			WaitingforConnection();
			setupSteam();
			authenticate();
		}
		catch(Exception e)
		{
			showMessage("\n Connection Ended \n");
		}
		finally{
			// Good practice to close all the connection when some interruption happens 
			closeall();
		}
		}
	}
	
	
	private void WaitingforConnection(){
		try {
			showMessage("\n Waiting for someone to connect \n");
			connection = Server.accept();
			showMessage("\n Connected to " + connection.getInetAddress().getHostName() +" \n");
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
  
	// Setting up the I/O Streams
	private void setupSteam() throws IOException{
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
			showMessage(" Streams are now connected \n");
	}
	
	// Closing all the I/O streams
	private void closeall()
	{
		try {
			input.close();
			output.close();
			connection.close();
		}
	 catch (IOException e) {
		e.printStackTrace();
	 	}
	}
	
	// Methods running in the background to display the text on the UI screens
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
	
	private void authenticate()
	{
		String message = "";
		try {
			while(true){
				message = (String) input.readObject();
				//showMessage(message+"\n");
				
				if(message.length() > 0)
					break;
			}
			
			
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
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
			//System.out.println(hashValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (message.equals(hashValue))
		{
			showMessage("\n Authentication Complete \n");
		}
		else
		{
			showMessage("\n Not a valid user \n");
		}
				
	}
	
	// Method to generate Authentication Token
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
