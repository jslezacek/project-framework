package org.project.feedpublisher;

import org.project.TestRunner;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Created by jojo on 6/13/17.
 */
public class MulticastPublisher {

    private static int MULTICAST_PORT;
    private static String MULTICAST_GROUP;
    private static String msg = "FeedID %d";
    private KafkaPublisher kafkaBus;

    public MulticastPublisher(String mcastGroup, int mcastPort) {
        this.MULTICAST_PORT = mcastPort;
        this.MULTICAST_GROUP = mcastGroup;
        this.kafkaBus = new KafkaPublisher("framework:9092");
    }

    public void generateFeed(int noPackets, int throttling) {
        System.out.println("Starting feedGeneration: packets" + noPackets);
        try {
            InetAddress McastAddress = InetAddress.getByName(MULTICAST_GROUP);
            MulticastSocket mcastSocket = new MulticastSocket(MULTICAST_PORT);
            mcastSocket.setNetworkInterface(NetworkInterface.getByName("lo"));
            for (int i = 0; i < noPackets; i++) {
                String feedSentTimestamp = String.valueOf(System.nanoTime());
                String testFeedMsg = String.format(this.msg, (100+i)) + feedSentTimestamp;
                TestRunner.logger.log(Level.INFO, testFeedMsg);
                kafkaBus.send("test", testFeedMsg);
                DatagramPacket packet = new DatagramPacket(testFeedMsg.getBytes(StandardCharsets.US_ASCII), testFeedMsg.getBytes().length);
                //TODO: datagram vs stream. We have to set destination on each datagram packet.
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