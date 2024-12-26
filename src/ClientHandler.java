import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // List to keep track of all connected clients
    private Socket socket; // Socket for the client connection
    private BufferedReader bufferedReader; // Reader to receive messages from the client
    private BufferedWriter bufferedWriter; // Writer to send messages to the client
    private String clientUsername; // Username of the connected client

    // Constructor to initialize the client handler
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine(); // Read the username sent by the client
            clientHandlers.add(this); // Add the new client to the list of handlers
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!"); // Notify other clients about the new client
        } catch (IOException e) {
            // Close resources if an error occurs during initialization
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        // Continuously listen for messages from the client while the socket is connected
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine(); // Read the message from the client
                broadcastMessage(messageFromClient); // Broadcast the message to other clients
            } catch (IOException e) {
                // Close resources if an error occurs while reading messages
                closeEverything(socket, bufferedReader, bufferedWriter);
                break; // Exit the loop if an error occurs
            }
        }
    }

    // Method to broadcast a message to all connected clients except the sender
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Send the message to all clients except the sender
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                // Close resources if an error occurs while sending messages
                closeEverything(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
            }
        }
    }

    // Method to remove the client from the list and notify others
    public void removeClientHandler() {
        clientHandlers.remove(this); // Remove the client from the list
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!"); // Notify other clients about the disconnection
    }

    // Method to close all resources (socket, reader, writer) and remove the client
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler(); // Remove the client from the list
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
}
