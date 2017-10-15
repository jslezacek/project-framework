package org.project.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jdk.internal.util.xml.impl.Input;
import org.project.messages.OrderDecoder;

public class OrderGateway  {

    final private int port;
    final private String ipAddress;
    private int connectionCounter = 0;
    private ExecutorService service;
    private Integer MAX_MESSAGE_LEN = 1024;
    private Integer MAX_MESSAGE_COUNT = 100;
    private KafkaPublisher kafkaBus;

    public OrderGateway (int myPort, String ipAddress)  {
        this.port = myPort;
        this.ipAddress = ipAddress;
        this.service = Executors.newFixedThreadPool(3);
        this.kafkaBus = new KafkaPublisher("framework:9092", "measurements");
    }

    public void run() {
        System.out.println("Starting orderGateway..");
        try {
            final InetAddress gatewayAddress = InetAddress.getByName(ipAddress);
            final ServerSocket serverSocket = new ServerSocket(port, 0, gatewayAddress);

            while (true) {
                // wait for connection
                Socket socket = serverSocket.accept();
                // if connection accpeted, create client thread
                Thread t = new Thread(new clientConnection(socket, this.kafkaBus));
                String treadName = "OrderThread-"+this.connectionCounter;
                t.setName(treadName);
                service.submit(t);
                this.connectionCounter ++;
            }
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    private class clientConnection implements Runnable {
        Socket clientSocket;
        KafkaPublisher kafkaBus;
        String name;
        List<String> parsedMessages;
        InputStream inStream;
        OrderDecoder decoder;

        public clientConnection(Socket socket, KafkaPublisher kafkaBus) throws IOException {
            this.inStream = socket.getInputStream();
            this.kafkaBus = kafkaBus;
            this.decoder = new OrderDecoder(this.kafkaBus);
        }

        @Override
        public void run() {
            this.name = Thread.currentThread().getName();
            int receivedBytes = 0;
            byte[] buffer = new byte[MAX_MESSAGE_COUNT * MAX_MESSAGE_LEN]; //buffer to hold messages

            while(isConnectAlive(receivedBytes)) { // permanently listen on TCP data stream until connection broken.
                try {
                    receivedBytes = this.inStream.read(buffer, 0, buffer.length); // read some bytes, we don't know if full message or multple messages.
                } catch (IOException e) {
                    e.printStackTrace();
                }
                parsedMessages = this.decoder.parse(receivedBytes, buffer);
            }
            System.out.println("Connection lost: " + this.name);
        }

        public boolean isConnectAlive(Integer receivedBytes) {
            // -1: indicates closed connection
            //  0: nothing received but keep listening for new packets arriving
            return receivedBytes >= 0;
        }

        public int findByteArray(byte[] haystack, byte[] needle) {
            for (int index = 0; index < haystack.length; index++) {
                if (needle[0] == haystack[index]) {
                    for (int a = 1; a < needle.length; a ++ ) {

                   }
                }
            }
            return 0;
        }
    }
}

