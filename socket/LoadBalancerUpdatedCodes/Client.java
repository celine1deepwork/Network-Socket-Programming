package LoadBalancerUpdatedCodes;

import java.io.*;
import java.net.*;

public class Client {
    private static final String LOAD_BALANCER_HOST = "127.0.0.1";
    private static final int LOAD_BALANCER_PORT = 9090;

    public static void main(String[] args) {
        try (Socket loadBalancerSocket = new Socket(LOAD_BALANCER_HOST, LOAD_BALANCER_PORT);
             PrintWriter lbOut = new PrintWriter(loadBalancerSocket.getOutputStream(), true);
             BufferedReader lbIn = new BufferedReader(new InputStreamReader(loadBalancerSocket.getInputStream()))) {

            lbOut.println("request");
            String serverInfo = lbIn.readLine();  // Burada sunucu bilgilerini okuyoruz
            if (serverInfo == null || serverInfo.equals("no available server")) {
                System.out.println("No available server to handle the request.");
                return;
            }

            String[] parts = serverInfo.split(":");
            if (parts.length != 2) {
                System.out.println("Invalid server information received: " + serverInfo);
                return;
            }

            String serverHost = parts[0].replace("/", "");
            int serverPort;
            try {
                serverPort = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number: " + parts[1]);
                return;
            }

            try (Socket serverSocket = new Socket(serverHost, serverPort);
                 PrintWriter serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
                 BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {

                String request = "Hello from client";
                serverOut.println(request);
                String response = serverIn.readLine();
                System.out.println("Response from server: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
