import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
 
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); 
    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String clientName;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream());
            this.clientName = bufferedReader.readLine(); // Read the client name from the socket input stream
            clientHandlers.add(this); 
            broadcastMessage("SERVER: " + clientName + " has joined the chat!"); // Notify all clients about the new client 
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, printWriter);
        }
    }
    @Override
    public void run() {
        String messageFromClient;
         while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine(); // Read the message from the client
                broadcastMessage(messageFromClient); // Broadcast the message to all clients
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, printWriter);
                break;
            }
         }
        }
    public void broadcastMessage(String messageToSend) {
        try{
            for (int i = 0; i < clientHandlers.size(); i++) {
                ClientHandler clientHandler = clientHandlers.get(i); // Get the client at index i
                if (!clientHandler.clientName.equals(clientName)) { // Skip the sender
                    clientHandler.printWriter.println(messageToSend); // Send the message
                    clientHandler.printWriter.flush(); // Ensure it’s pushed out immediately
                }
            }
            
    }catch (Exception e) {
        closeEverything(socket, bufferedReader, printWriter); // Close the resources if an error occurs
    }
    }
    public void removeClientHandler() {
        clientHandlers.remove(this); // Remove the client from the list of clients
        broadcastMessage("SERVER: " + clientName + " has left the chat!"); // Notify all clients about the disconnection
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter) {
        removeClientHandler(); // Remove the client handler from the list of clients
        try {
            if (bufferedReader != null) {
                bufferedReader.close(); // Close the input stream
            }
            if (printWriter != null) {
                printWriter.close(); // Close the output stream
            }
            if (socket != null) {
                socket.close(); // Close the socket
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace if an error occurs
        } 
     }   
    
}
 