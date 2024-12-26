import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket; // ServerSocket to listen for incoming client connections

    // Constructor to initialize the server with a ServerSocket
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Method to start the server and accept client connections
    public void startServer() {
        try {
            // Continuously listen for client connections until the server socket is closed
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); // Accept a new client connection
                System.out.println("A new client has connected!"); // Notify about the new connection
                ClientHandler clientHandler = new ClientHandler(socket); // Create a handler for the client

                Thread thread = new Thread(clientHandler); // Start a new thread for the client handler
                thread.start();
            }
        } catch (IOException e) {
            // Print the stack trace if an error occurs while accepting connections
            e.printStackTrace();
        }
    }

    // Method to close the server socket
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close(); // Close the server socket
            }
        } catch (IOException e) {
            // Print the stack trace if an error occurs while closing the server socket
            e.printStackTrace();
        }
    }

    // Main method to start the server
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1357); // Create a server socket listening on port 1357
        Server server = new Server(serverSocket); // Initialize the server with the created server socket
        server.startServer(); // Start the server
    }
}
