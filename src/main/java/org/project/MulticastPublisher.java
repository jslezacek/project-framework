package org.project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

/**
 * Created by jojo on 6/13/17.
 */
public class MulticastPublisher {

    private static int MULTICAST_PORT;
    private static String MULTICAST_GROUP;
    private static String msg = "Test feed message %d";

    public MulticastPublisher(String mcastGroup, int mcastPort) {
        this.MULTICAST_PORT = mcastPort;
        this.MULTICAST_GROUP = mcastGroup;
    }

    public void generateFeed(int noPackets, int throttling) {
        System.out.println("Starting feedGeneration: packets" + noPackets);
        try {
            InetAddress McastAddress = InetAddress.getByName(MULTICAST_GROUP);
            MulticastSocket mcastSocket = new MulticastSocket(MULTICAST_PORT);
            mcastSocket.setNetworkInterface(NetworkInterface.getByName("lo"));
            for (int i = 0; i < noPackets; i++) {
                System.out.println("feed sent ns: " + System.nanoTime());
                String testFeedMsg = String.format(this.msg, (i));

                DatagramPacket packet = new DatagramPacket(testFeedMsg.getBytes(StandardCharsets.US_ASCII), testFeedMsg.getBytes().length);
                packet.setAddress(McastAddress);
                packet.setPort(MULTICAST_PORT);
                mcastSocket.send(packet);
                Thread.sleep(throttling);
            }

        } catch (IOException e) {
            System.out.println(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}