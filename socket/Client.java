import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12346; // Port numarasını değiştirdik

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Sunucunun durumunu kontrol et
            out.println("CHECK");
            String response = in.readLine();
            System.out.println("Sunucudan gelen yanıt: " + response);

            if ("NOT_BUSY".equalsIgnoreCase(response)) {
                // Sunucu meşgul değilse, ağır yük işlemini başlat
                out.println("BUSY");
                response = in.readLine();
                System.out.println("Ağır yük işlemi başlatıldı: " + response);
            } else {
                System.out.println("Sunucu şu anda meşgul.");
            }
        } catch (SocketException e) {
            System.err.println("Bağlantı kesildi: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
