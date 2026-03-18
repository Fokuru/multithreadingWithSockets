package com.example;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class SocketClientExample {
	
	
	/*
	 * Modify this example so that it opens a dialogue window using java swing, 
	 * takes in a user message and sends it
	 * to the server. The server should output the message back to all connected clients
	 * (you should see your own message pop up in your client as well when you send it!).
	 *  We will build on this project in the future to make a full fledged server based game,
	 *  so make sure you can read your code later! Use good programming practices.
	 *  ****HINT**** you may wish to have a thread be in charge of sending information 
	 *  and another thread in charge of receiving information.
	*/
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException{
        //get the localhost IP address, if server is running on some other IP, you need to use that
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        socket = new Socket("localhost", 9876);
        oos = new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Sending request to Socket Server");
        boolean running = true;
        Scanner sc = new Scanner(System.in);
        while (running) {
            if (ois != null) {
                ois = new ObjectInputStream(socket.getInputStream());
                String message = (String) ois.readObject();
                System.out.println("Message: " + message);
            }
            try{
                System.out.println("Enter a message to send to the server (type 'exit' to quit):");
                
                    String message = sc.nextLine();
                    if (message.equalsIgnoreCase("exit")) {
                        oos.writeObject("exit");
                        if (ois != null) ois.close();
                        if (oos != null) oos.close();
                        if (socket != null) socket.close();
                        sc.close();
                        running = false;
                        System.out.println("Exiting client...");
                    } else {
                        oos.writeObject(message);
                        oos.flush();
                    }
                
            } catch (IOException e) {
                System.out.println("Error sending message to server: " + e);
            }
        
            //read the server response message
            //if (running){
            
            Thread.sleep(100);
                
        //}
    }
    
        
    }
    // Use flush to send the message immediately without waiting for the buffer to fill up
    // oos.flush();
}
