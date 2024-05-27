import java.io.*;
import java.net.*;

public class TCPClient {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8383;

        try (Socket socket = new Socket(host, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            while (true) {
                System.out.print("Input filename you want to send: ");
                String filename = reader.readLine();

                try (BufferedReader fileReader = new BufferedReader(new FileReader(filename));
                     OutputStream out = socket.getOutputStream();
                     PrintWriter writer = new PrintWriter(out, true)) {

                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        writer.println(line);
                    }

                } catch (FileNotFoundException e) {
                    System.out.println("You entered an invalid filename! \nPlease enter a valid name");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
