package com.example;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program should allow the client to send it messages. The messages should then 
 * become visible to all other clients.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example). 
 * 
 * This version of the program creates a new thread for
 * every connection request.
 */
public class ChatServerWithThreads {

    public static final int LISTENING_PORT = 9876;
    private List<ConnectionHandler> connections = Collections.synchronizedList(new ArrayList<>());

    public ChatServerWithThreads() {
        ServerSocket listener;  // Listens for incoming connections.
        Socket connection;      // For communication with the connecting program.
        
        /* Accept and process connections forever, or until some error occurs. */

        // pre: none
        // post: the server is running, and is accepting and processing connection requests until some error occurs.  
        // If an error occurs, a message is printed and the server is shut down.
        try {
            listener = new ServerSocket(LISTENING_PORT);
            System.out.println("Listening on port " + LISTENING_PORT);
            while (true) {
                  // Accept next connection request and handle it.
                connection = listener.accept();
                System.out.println("Connection received from " + connection.getInetAddress());
                ConnectionHandler handler = new ConnectionHandler(connection);
                connections.add(handler);
                handler.start();
            }
        }
        catch (Exception e) {
            System.out.println("Sorry, the server has shut down.");
            System.out.println("Error:  " + e);
            return;
        }

    }

    // pre: none
    // post: a new ChatServerWithThreads object is created and the server is running, 
    // accepting and processing connection requests until some error occurs.  
    // If an error occurs, a message is printed and the server is shut down.
    public static void main(String[] args) {
        new ChatServerWithThreads();
        

    }  // end main()


    /**
     *  Defines a thread that handles the connection with one
     *  client.
     */
    // pre: none
    // post: the thread is running, and is handling the connection with one client.
    private class ConnectionHandler extends Thread {
        Socket client;
        ObjectOutputStream oos;
        ObjectInputStream ois;

        ConnectionHandler(Socket socket) {
            client = socket;
        }
        
        public void run() {
            String clientAddress = client.getInetAddress().toString();
            try {
                oos = new ObjectOutputStream(client.getOutputStream());
                ois = new ObjectInputStream(client.getInputStream());
                
                while (true) {
                    String message = (String) ois.readObject();
                    System.out.println("Message Received from " + clientAddress + ": " + message);
                    
                    // Broadcast the message to all other clients
                    synchronized (connections) {
                        for (ConnectionHandler handler : connections) {
                            if (handler != this) {
                                try {
                                    handler.oos.writeObject(clientAddress + ": " + message);
                                    System.out.println("Hehe");
                                    handler.oos.flush();
                                    
                                } catch (IOException e) {
                                    System.out.println("Error sending to client: " + e);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println("Error on connection with: " + clientAddress + ": " + e);
            } finally {
                // Remove this handler from the list when connection closes
                synchronized (connections) {
                    connections.remove(this);
                }
                try {
                    client.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }


}
