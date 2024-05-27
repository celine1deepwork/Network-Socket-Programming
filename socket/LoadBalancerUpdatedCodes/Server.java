package LoadBalancerUpdatedCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Server {
    private static final String LOAD_BALANCER_HOST = "127.0.0.1";
    private static final int LOAD_BALANCER_PORT = 9090;

    public static void main(String[] args) {
        try (Socket loadBalancerSocket = new Socket(LOAD_BALANCER_HOST, LOAD_BALANCER_PORT);
             PrintWriter lbOut = new PrintWriter(loadBalancerSocket.getOutputStream(), true);
             BufferedReader lbIn = new BufferedReader(new InputStreamReader(loadBalancerSocket.getInputStream()))) {


            lbOut.println("join static");
            String response = lbIn.readLine();
            if ("join accepted".equals(response)) {
                System.out.println("Server registered with load balancer.");
            }

            ServerSocket serverSocket = new ServerSocket(0);
            int server_local_port = serverSocket.getLocalPort();
            System.out.println("Server listening on port " + server_local_port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("This is the clien socket adress: " + clientSocket.getLocalSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String request = in.readLine();
                System.out.println("Received request: " + request);
                out.println("Response from server: " + request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
