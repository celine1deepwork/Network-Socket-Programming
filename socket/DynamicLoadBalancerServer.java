import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DynamicLoadBalancerServer {
    private static final int PORT = 9999;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is listening on port " + PORT);

            Queue<Process> processes = new LinkedList<>();
            Lock processLock = new ReentrantLock();
            DynamicLoadBalancer scheduler = new DynamicLoadBalancer(processes, processLock);

            Thread schedulerThread = new Thread(scheduler);
            schedulerThread.setDaemon(true);
            schedulerThread.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket, processes, processLock)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Queue<Process> processes;
    private final Lock processLock;

    public ClientHandler(Socket clientSocket, Queue<Process> processes, Lock processLock) {
        this.clientSocket = clientSocket;
        this.processes = processes;
        this.processLock = processLock;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String data = reader.readLine();
            String[] parts = data.split(" ");
            int pid = Integer.parseInt(parts[0]);
            int burstTime = Integer.parseInt(parts[1]);
            int weight = Integer.parseInt(parts[2]);

            Process process = new Process(pid, burstTime, weight);

            processLock.lock();
            try {
                processes.add(process);
                System.out
                        .println("Received process " + pid + " with burst time " + burstTime + " and weight " + weight);
            } finally {
                processLock.unlock();
            }

            writer.println("Process " + pid + " with burst time " + burstTime + " and weight " + weight + " received");
        } catch (IOException e) {
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

class Process {
    private final int pid;
    private final int burstTime;
    private int remainingTime;
    private final int weight;
    private int currentWeight;

    public Process(int pid, int burstTime, int weight) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.weight = weight;
        this.currentWeight = weight;
    }

    public int getPid() {
        return pid;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getWeight() {
        return weight;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(int currentWeight) {
        this.currentWeight = currentWeight;
    }
}

class DynamicLoadBalancer implements Runnable {
    private final Queue<Process> processes;
    private final Lock processLock;
    private volatile boolean running = true;

    public DynamicLoadBalancer(Queue<Process> processes, Lock processLock) {
        this.processes = processes;
        this.processLock = processLock;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            processLock.lock();
            try {
                if (!processes.isEmpty()) {
                    weightedRoundRobin(processes);
                }
            } finally {
                processLock.unlock();
            }

            try {
                Thread.sleep(5000); // Check every 5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    private void weightedRoundRobin(Queue<Process> processes) {
        int time = 0;

        while (!processes.isEmpty()) {
            Process currentProcess = processes.poll();

            if (currentProcess.getCurrentWeight() > 0) {
                int executeTime = Math.min(currentProcess.getRemainingTime(), currentProcess.getCurrentWeight());
                time += executeTime;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executeTime);
                currentProcess.setCurrentWeight(currentProcess.getCurrentWeight() - executeTime);

                if (currentProcess.getRemainingTime() > 0) {
                    processes.add(currentProcess);
                    System.out.println("Process " + currentProcess.getPid() + " executed for " + executeTime +
                            " units; remaining time: " + currentProcess.getRemainingTime());
                } else {
                    System.out.println("Process " + currentProcess.getPid() + " executed for " + executeTime +
                            " units; completed at time " + time);
                }
            }

            if (currentProcess.getCurrentWeight() == 0) {
                currentProcess.setCurrentWeight(currentProcess.getWeight());
            }
        }
    }
}
