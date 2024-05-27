import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Select Load Balancer Type:");
        System.out.println("1. Dynamic");
        System.out.println("2. Static");
        System.out.print("Choice:");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> System.out.println("Dynamic Load Balancer selected.");
            // Add code here to handle dynamic load balancer
            case 2 -> {
                System.out.println("Static Load Balancer selected.");
                runVideoStreamingClient();
            }
            default -> System.out.println("Invalid choice. Please select 1 or 2.");
        }

        scanner.close();
    }

    public static void runVideoStreamingClient() {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + "/bin/java";
        String classpath = System.getProperty("java.class.path");

        // First, run the VideoStreamingServer
        String serverClassName = "VideoStreamingServer";
        String[] serverCmd = {
                javaBin,
                "-cp",
                classpath,
                serverClassName
        };

        // Then, run the VideoStreamingClient
        String clientClassName = "VideoStreamingClient";
        String[] clientCmd = {
                javaBin,
                "-cp",
                classpath,
                clientClassName
        };

        try {
            // Start the server process
            ProcessBuilder serverProcessBuilder = new ProcessBuilder(serverCmd);
            serverProcessBuilder.redirectErrorStream(true); // Merge stdout and stderr
            java.lang.Process serverProcess = serverProcessBuilder.start();

            // Print server output in a separate thread
            new Thread(() -> {
                try (BufferedReader serverOutput = new BufferedReader(
                        new InputStreamReader(serverProcess.getInputStream()))) {
                    String line;
                    while ((line = serverOutput.readLine()) != null) {
                        System.out.println("Server: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Wait for a short time to ensure the server starts
            Thread.sleep(2000);

            // Start the client process
            ProcessBuilder clientProcessBuilder = new ProcessBuilder(clientCmd);
            clientProcessBuilder.redirectErrorStream(true); // Merge stdout and stderr
            java.lang.Process clientProcess = clientProcessBuilder.start();

            // Capture and print client output
            BufferedReader clientOutput = new BufferedReader(new InputStreamReader(clientProcess.getInputStream()));

            String line;
            System.out.println("Client Output:");
            while ((line = clientOutput.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the client process to complete
            int exitCode = clientProcess.waitFor();
            System.out.println("Client process exited with code: " + exitCode);

            // Optionally, wait for the server to terminate if needed
            // int serverExitCode = serverProcess.waitFor();
            // System.out.println("Server process exited with code: " + serverExitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
}
}
}