package LoadBalancerUpdatedCodes;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class LoadBalancer {
    private static final int LOAD_BALANCER_PORT = 9090;
    private static List<ServerInfo> serverList = new ArrayList<>();
    private static int currentIndex = -1;
    private static int currentWeight = 0;
    private static int gcdWeight = 0;
    private static int maxWeight = 0;
    private static int serverCount = 0;

    static class ServerInfo {
        String host;
        int port;
        int weight;

        ServerInfo(String host, int port, int weight) {
            this.host = host;
            this.port = port;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return host + ":" + port + " (weight: " + weight + ")";
        }
    }

    public static void main(String[] args) {
        // Add server information and weights
        serverList.add(new ServerInfo("127.0.0.1", 8081, 5));
        serverList.add(new ServerInfo("127.0.0.1", 8082, 1));
        serverList.add(new ServerInfo("127.0.0.1", 8083, 1));

        // Calculate GCD and max weight
        serverCount = serverList.size();
        for (ServerInfo server : serverList) {
            gcdWeight = gcd(gcdWeight, server.weight);
            if (server.weight > maxWeight) {
                maxWeight = server.weight;
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(LOAD_BALANCER_PORT)) {
            System.out.println("Load Balancer is running on port " + LOAD_BALANCER_PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String request = in.readLine();
                    if ("request".equals(request)) {
                        String algorithm = in.readLine(); // Read algorithm from client (e.g., "rr" or "wrr")
                        ServerInfo server = null;
                        if ("wrr".equalsIgnoreCase(algorithm)) {
                            server = getNextServerWeighted();
                        } else {
                            server = getNextServerRoundRobin();
                        }
                        if (server != null) {
                            out.println(server.host + ":" + server.port);
                            System.out.println("Assigned server: " + server);
                        } else {
                            out.println("no available server");
                            System.out.println("No available server to handle the request.");
                        }
                    } else {
                        out.println("invalid request");
                        System.out.println("Received invalid request: " + request);
                    }
                } catch (IOException e) {
                    System.err.println("IO error while handling client request: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("IO error while starting load balancer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static synchronized ServerInfo getNextServerRoundRobin() {
        currentIndex = (currentIndex + 1) % serverCount;
        return serverList.get(currentIndex);
    }

    private static synchronized ServerInfo getNextServerWeighted() {
        while (true) {
            currentIndex = (currentIndex + 1) % serverCount;
            if (currentIndex == 0) {
                currentWeight = currentWeight - gcdWeight;
                if (currentWeight <= 0) {
                    currentWeight = maxWeight;
                    if (currentWeight == 0) {
                        return null;
                    }
                }
            }
            if (serverList.get(currentIndex).weight >= currentWeight) {
                return serverList.get(currentIndex);
            }
        }
    }

    private static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }
}
