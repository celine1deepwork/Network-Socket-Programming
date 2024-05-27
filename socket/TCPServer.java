import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class TCPServer {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8383;
        int totalClients;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter number of clients: ");
            totalClients = Integer.parseInt(reader.readLine());
            
            ServerSocket serverSocket = new ServerSocket(port, totalClients, InetAddress.getByName(host));
            List<Socket> connections = new ArrayList<>();
            
            System.out.println("Initiating clients");
            for (int i = 0; i < totalClients; i++) {
                Socket conn = serverSocket.accept();
                connections.add(conn);
                System.out.println("Connected with client " + (i + 1));
            }

            int fileno = 0;
            int idx = 0;
            for (Socket conn : connections) {
                idx++;
                InputStream in = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                StringBuilder data = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    data.append(line).append("\n");
                }

                if (data.length() > 0) {
                    String filename = "output" + fileno + ".txt";
                    fileno++;
                    try (FileWriter fileWriter = new FileWriter(filename)) {
                        fileWriter.write(data.toString());
                    }

                    System.out.println();
                    System.out.println("Receiving file from client " + idx);
                    System.out.println();
                    System.out.println("Received successfully! New filename is: " + filename);
                }
                conn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
