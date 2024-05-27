import java.io.*;
import java.net.*;

public class Server {
    private static boolean busy = false;

    public static void main(String[] args) {
        int port = 12346; // Port numarasını değiştirdik

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Sunucu başlatıldı ve port " + port + " dinleniyor...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String request = in.readLine();
                    if ("CHECK".equalsIgnoreCase(request)) {
                        if (busy) {
                            out.println("BUSY");
                        } else {
                            out.println("NOT_BUSY");
                        }
                    } else if ("BUSY".equalsIgnoreCase(request)) {
                        if (!busy) {
                            busy = true;
                            out.println("STARTED_LOAD");
                            new Thread(Server::simulateHeavyLoad).start();
                        } else {
                            out.println("ALREADY_BUSY");
                        }
                    } else {
                        out.println("UNKNOWN_COMMAND");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void simulateHeavyLoad() {
        long duration = 60000; // 60 saniye
        long endTime = System.currentTimeMillis() + duration;
        int count = 0;

        System.out.println("Ağır yük işlemi başladı...");
        for (long i = 2; System.currentTimeMillis() < endTime; i++) {
            if (isPrime(i)) {
                count++;
            }
        }
        System.out.println("Bulunan asal sayı adedi: " + count);
        System.out.println("Ağır yük işlemi bitti.");
        busy = false;
    }

    private static boolean isPrime(long n) {
        if (n <= 1) return false;
        for (long i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
