import java.io.*;
import java.net.*;
import java.util.*;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private String clientName;

    public Client(Socket socket, String clientName) {
        this.clientName = clientName;
        this.socket = socket;
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            // Removed duplicate clientName assignment from stream
            printWriter.println(clientName); // Send client name immediately
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, printWriter);
        }
    }

    public void sendMessage() throws IOException {
        // CHANGED: Added Scanner as try-with-resources
        try (Scanner sc = new Scanner(System.in)) {
            while (socket.isConnected()) {
                String messageToSend = sc.nextLine();
                // CHANGED: Added quit command
                if (messageToSend.equalsIgnoreCase("/quit")) {
                    closeEverything(socket, bufferedReader, printWriter);
                    return;
                }
                printWriter.println(clientName + ": " + messageToSend);
                // CHANGED: Removed redundant flush (auto-flush is true)
            }
        }
    }

    public void listenForMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        messageFromGroupChat = bufferedReader.readLine();
                        // CHANGED: Added null check for disconnection
                        if (messageFromGroupChat == null) {
                            System.out.println("Disconnected from server");
                            closeEverything(socket, bufferedReader, printWriter);
                            break;
                        }
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, printWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, PrintWriter printWriter) {
        try {
            // CHANGED: Added null checks and proper ordering
            if (printWriter != null) printWriter.close();
            if (bufferedReader != null) bufferedReader.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Added exit on close
        System.exit(0);
    }

    public static void main(String[] args) {
        //  Added try-with-resources for Scanner
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter your name: ");
            String clientName = scanner.nextLine();
            Socket socket = new Socket("localhost", 1234);
            Client client = new Client(socket, clientName);
            client.listenForMessages();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}