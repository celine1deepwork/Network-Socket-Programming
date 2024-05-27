import java.io.*;
import java.net.*;

public class VideoStreamingClient {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8282;
    private static final String OUTPUT_FILE_PATH = "path_to_save_video_file.mp4";

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             InputStream serverIn = socket.getInputStream();
             BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(OUTPUT_FILE_PATH))) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = serverIn.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
            }

            System.out.println("Video streaming complete. File saved to " + OUTPUT_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
