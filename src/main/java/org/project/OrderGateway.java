package org.project;

import com.sun.security.ntlm.Server;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jojo on 6/13/17.
 */
public class OrderGateway implements Runnable {

    final private int port;
    final private String ipAddress;
    private int connectionCounter = 0;

    public OrderGateway(int myPort, String ipAddress)  {
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


            while (true) {
                // wait for connection
                Socket socket = serverSocket.accept();
                // if connection accpeted, create client thread
                Thread t = new Thread(new clientConnection(socket));
                String treadName = "OrderThread-"+this.connectionCounter;
                t.setName(treadName);
                t.start();
                this.connectionCounter ++;
            }
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    private class clientConnection implements Runnable {
        Socket clientSocket;
        byte[] buffer = new byte[1024];
        String name;

        public clientConnection(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            this.name = Thread.currentThread().getName();
            System.out.println("Starting: " + this.name);
            try {
                InputStream inStream = this.clientSocket.getInputStream();
                int response = 0;
                while(response >= 0) {
                    response = inStream.read(buffer);
                    System.out.println(new String(buffer));
                }
                System.out.println("Connection lost: " + this.name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

