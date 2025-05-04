import java.io.*;
import java.net.*;

public class server {
    private ServerSocket serverSocket;

    public server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected.");
                ClientHandler ch = new ClientHandler(socket);
                Thread th = new Thread(ch);
                th.start();
            }
        } catch (IOException e) {
            
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            server s = new server(serverSocket);  //object
            s.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
