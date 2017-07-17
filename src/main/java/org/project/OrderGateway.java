package org.project;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Created by jojo on 6/13/17.
 */
public class OrderGateway implements Runnable {

    final private int port;
    final private String ipAddress;
    private int connectionCounter = 0;
    private ExecutorService service;

    public OrderGateway(int myPort, String ipAddress)  {
        this.port = myPort;
        this.ipAddress = ipAddress;
        this.service = Executors.newFixedThreadPool(3);
    }

    @Override
    public void run() {
        System.out.println("Starting orderGateway..");
        try {
            final InetAddress gatewayAddress = InetAddress.getByName(ipAddress);
            final ServerSocket serverSocket = new ServerSocket(port, 0, gatewayAddress);

            while (true) {
                // wait for connection
                Socket socket = serverSocket.accept();
                // if connection accpeted, create client thread
//                Future<Long> orderTs = this.service.submit(new clientConnectionCallback(socket));
//                System.out.println("Received future callback");
//                System.out.println(orderTs.get());

                Thread t = new Thread(new clientConnection(socket));
                String treadName = "OrderThread-"+this.connectionCounter;
                t.setName(treadName);
                service.submit(t);
//                t.start();

                this.connectionCounter ++;
            }
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    private class clientConnection implements Runnable {
        Socket clientSocket;
        byte[] buffer = new byte[10000];
        String name;

        public clientConnection(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {

            this.name = Thread.currentThread().getName();
            System.out.println("Starting: " + this.name);
//            OrderMessageFactory = new
            try (InputStream inStream = this.clientSocket.getInputStream()) {
                this.clientSocket.setReceiveBufferSize(1024);
                this.clientSocket.setTcpNoDelay(true);
                int response = 0;
                while(response >= 0) {
                    System.out.println(System.nanoTime());
                    response = inStream.read(buffer);

                    System.out.println(new String(buffer));
                }
                System.out.println("Connection lost: " + this.name);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

//    private class clientConnectionCallback implements Callable<Long> {
//        Socket clientSocket;
//        byte[] buffer = new byte[1024];
//        String name;
//
//        public clientConnectionCallback(Socket socket) {
//            this.clientSocket = socket;
//        }
//
//        public Long call() {
//            this.name = Thread.currentThread().getName();
//            System.out.println("Starting: " + this.name);
//            try (InputStream inStream = this.clientSocket.getInputStream()) {
//                int response = 0;
//                while(response >= 0) {
//                    response = inStream.read(buffer);
//                    System.out.println(new String(buffer) +" " + System.nanoTime());
////                    return(Long.valueOf(System.currentTimeMillis()));
//                }
//                System.out.println("Connection lost: " + this.name);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
////            return null;
//            return null;
//        }
//    }
}

