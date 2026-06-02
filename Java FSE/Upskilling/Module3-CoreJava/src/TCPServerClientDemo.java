import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServerClientDemo {
    public static void main(String[] args) throws Exception {
        int port = 5000;

        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server is listening on port " + port);
                try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                    
                    String clientMessage = reader.readLine();
                    System.out.println("Server received: " + clientMessage);
                    writer.println("Hello Client from Server!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        serverThread.start();
        Thread.sleep(500); // Wait for server to start

        try (Socket socket = new Socket("localhost", port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            writer.println("Hello Server from Client!");
            String serverResponse = reader.readLine();
            System.out.println("Client received: " + serverResponse);
        }
    }
}
