package org.project;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jojo on 6/18/17.
 */
public class OrderGatewayFix implements Runnable {
    final private int port;
    final private String ipAddress;
    private int connectionCounter = 0;

    public OrderGatewayFix(int myPort, String ipAddress)  {
        this.port = myPort;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
        System.out.println("Starting orderGateway..");
        try {
            final InetAddress gatewayAddress = InetAddress.getByName(ipAddress);
            final ServerSocket serverSocket = new ServerSocket(port, 0, gatewayAddress);
            byte[] buffer = new byte[1024];
            ExecutorService threadPool = Executors.newFixedThreadPool(2);

            while (true) {
                // wait for connection
                Socket socket = serverSocket.accept();
                // if connection accpeted, create client thread
                Future<String> f = threadPool.submit(new clientConnection(socket));
            }
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    private class clientConnection implements Callable<String> {
        private final Socket clientSocket;
        private final byte[] buffer = new byte[1024];
        private String name;

        public clientConnection(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public String call() throws Exception {
            this.name = Thread.currentThread().getName();
            System.out.println("Starting: " + this.name);
            try (InputStream inStream = this.clientSocket.getInputStream()) {
                int response = 0;
                while(response >= 0) {
                    response = inStream.read(buffer);
                    return (new String(buffer) + " from: " + this.name);
//                    System.out.println(new String(buffer));
                }
                System.out.println("Connection lost: " + this.name);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
