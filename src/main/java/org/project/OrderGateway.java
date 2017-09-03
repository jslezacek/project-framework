package org.project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jojo on 6/13/17.
 */
public class OrderGateway implements Runnable {

    final private int port;
    final private String ipAddress;
    private int connectionCounter = 0;
    private ExecutorService service;
    private Integer MAX_MESSAGE_LEN = 1024;
    private Integer MAX_MESSAGE_COUNT = 10;
    private byte[] MESSAGE_DELIMITER = "\u0001".getBytes(StandardCharsets.US_ASCII);

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
                Thread t = new Thread(new clientConnection(socket));
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
        byte[] buffer = new byte[MAX_MESSAGE_COUNT * MAX_MESSAGE_LEN]; //buffer to hold messages
        String name;

        public clientConnection(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {

            this.name = Thread.currentThread().getName();
            System.out.println("Starting: " + this.name);
            try (InputStream inStream = this.clientSocket.getInputStream()) {
//                this.clientSocket.setReceiveBufferSize(1024);
//                this.clientSocket.setTcpNoDelay(true);
                //response is the number of bytes that were read.
                int receivedBytes = 0;
                System.out.println("response: " + String.valueOf(receivedBytes));
                int bytesToRead = 50;
                byte[] input = new byte[bytesToRead];

                // magic number
                ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();

                while(activeStream(receivedBytes)) { // permanently listen on TCP data stream until connection broken.
                    receivedBytes = inStream.read(buffer, 0, buffer.length); // read some bytes, we don't know if full message or multple messages.
                    int messageStart = 0;
                    for (int delimiter_index = 0; delimiter_index <= receivedBytes - 1; delimiter_index++) {
                        if (buffer[delimiter_index] == MESSAGE_DELIMITER[0] && delimiter_index > 0) { // if first character delimiter, continue reading
                            String message = new String(buffer, messageStart, delimiter_index - messageStart);     // parse message from last delimiter to current delimiter
                            System.out.println(message);
                            System.out.println("Found delimiter position " + delimiter_index);
                            System.out.println(System.nanoTime());
                            messageStart = delimiter_index; // start reading from last delimiter position.
                        }
                    }
                }
                System.out.println("Connection lost: " + this.name);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public boolean activeStream(Integer receivedBytes) {
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

