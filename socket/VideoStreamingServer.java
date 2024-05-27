import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class VideoStreamingServer {
    private static final int PORT = 9090;
    private static final String VIDEO_FILE_PATH = "abc.mp4";
    private static final int BIT_RATE = 1000 * 1024; // 1000 Kbps

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8282)) {
            System.out.println("Video Streaming Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedInputStream videoIn = new BufferedInputStream(new FileInputStream(VIDEO_FILE_PATH));
                 OutputStream clientOut = clientSocket.getOutputStream()) {
                byte[] buffer = new byte[BIT_RATE / 8]; // Convert bit rate to bytes per second
                int bytesRead;

                while ((bytesRead = videoIn.read(buffer)) != -1) {
                    long startTime = System.currentTimeMillis();

                    clientOut.write(buffer, 0, bytesRead);
                    clientOut.flush();

                    long elapsedTime = System.currentTimeMillis() - startTime;
                    long sleepTime = 1000 - elapsedTime;

                    if (sleepTime > 0) {
                        TimeUnit.MILLISECONDS.sleep(sleepTime);
                    }
                }
            
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
