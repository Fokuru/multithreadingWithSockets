package com.example;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JTextField;



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
        final Socket socket = new Socket(host, 9876);
        final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        
        
      
        System.out.println("Sending request to Socket Server");
        AtomicBoolean running = new AtomicBoolean(true);

        JFrame gui= new JFrame();
        gui.setSize(500, 500);
        //gui.setBackground(new Color(200,200,150));
        JTextField topText = new JTextField("Type your messages bellow (type 'exit' to quit):", 40);
        JTextField input = new JTextField("", 40);
        JTextField bottomText = new JTextField("Recieved message displayed bellow:", 40);
        JTextField output = new JTextField("", 40);
        topText.setPreferredSize(new Dimension(500, 50));
        input.setPreferredSize(new Dimension(500, 150));
        bottomText.setPreferredSize(new Dimension(500, 50));
        output.setPreferredSize(new Dimension(500, 150));
        topText.setBackground(new Color(215, 220, 250));
        bottomText.setBackground(new Color(215, 220, 250));
        input.setBackground(new Color(250, 235, 215));
        output.setBackground(new Color(215, 250, 220));
        gui.setLayout(new FlowLayout());	


        // pre: none
        // post: the GUI is set up and visible, and a thread is running that listens
        // for user input and sends it to the server, and another thread is running 
        // that listens for messages from the server and displays them in the GUI.
        new Thread (() -> {input.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("tried to send message "+input.getText());
                try{
                //System.out.println("Enter a message to send to the server (type 'exit' to quit):");

                    String message = input.getText();
                    if (message.equalsIgnoreCase("exit")) {
                        running.set(false);
                        oos.writeObject("exit");
                        if (ois != null) ois.close();
                        if (oos != null) oos.close();
                        if (socket != null) socket.close();
                        gui.setVisible(false);
                        gui.dispose();
                        System.out.println("Exiting client...");
                    } else {
                        oos.writeObject(message);
                        oos.flush();
                        input.setText("");
                        }
                    
                } catch (IOException k) {
                    System.out.println("Error sending message to server: " + k);
                }
            
            }});
        }).start();
        topText.setEditable(false);
        bottomText.setEditable(false);
        output.setEditable(false);
        gui.add(topText);
        gui.add(input);
        gui.add(bottomText);
        gui.add(output);
        gui.setVisible(true);



        // pre: none
        // post: a thread is running that listens for messages from the server and displays them in the GUI. 
        // If the connection is closed, the thread stops and a message is printed.
        new Thread (() -> {while (running.get()) {
            if (ois != null) {
                String message = "";
                try {
                    message = (String) ois.readObject();
                    output.setText(message);
                    System.out.println("Someone said: " + message);
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    if (e instanceof java.net.SocketException && "Socket closed".equals(e.getMessage())) {
                        running.set(false);
                        System.out.println("Connection closed.");
                    } else {
                        e.printStackTrace();
                    }
                }
            }
            
        
            //read the server response message
            //if (running){
            /*
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                 */
                
        //}
        
    }
    
        
    }).start();
}
    // Use flush to send the message immediately without waiting for the buffer to fill up
    // oos.flush();
}
