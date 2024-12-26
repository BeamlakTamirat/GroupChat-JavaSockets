import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket; // Socket for connecting to the server
    private BufferedReader bufferedReader; // Reader to receive messages from the server
    private BufferedWriter bufferedWriter; // Writer to send messages to the server
    private String username; // Username of the client

    // Constructor to initialize the client with a socket and username
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            // Close resources if an error occurs during initialization
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to send messages to the server
    public void sendMessage() {
        try {
            // Send the username as the first message
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);

            // Continuously read user input and send messages while the socket is connected
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            // Close resources if an error occurs while sending messages
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to listen for incoming messages from the server
    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGroupChat;

            // Continuously listen for messages while the socket is connected
            while (socket.isConnected()) {
                try {
                    msgFromGroupChat = bufferedReader.readLine();
                    System.out.println(msgFromGroupChat); // Print the received message
                } catch (IOException e) {
                    // Close resources if an error occurs while reading messages
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break; // Exit the loop if an error occurs
                }
            }
        }).start();
    }

    // Method to close all resources (socket, reader, writer)
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close(); // Close the reader
            }
            if (bufferedWriter != null) {
                bufferedWriter.close(); // Close the writer
            }
            if (socket != null) {
                socket.close(); // Close the socket
            }
        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace if an error occurs during closing
        }
    }

    // Main method to start the client application
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine(); // Get the username from the user

        try {
            // Connect to the server on localhost at port 1357
            Socket socket = new Socket("localhost", 1357);
            Client client = new Client(socket, username);
            client.listenForMessage(); // Start listening for messages
            client.sendMessage(); // Start sending messages
        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace if an error occurs during connection
        }
    }
}
