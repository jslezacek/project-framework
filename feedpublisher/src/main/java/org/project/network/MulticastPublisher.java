package org.project.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.project.FeedPublisherRunner;
import org.project.messages.TestFeedMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

public class MulticastPublisher {

    private static int MULTICAST_PORT;
    private static String MULTICAST_GROUP;
    private static String msg = "FeedID %d";
    private KafkaPublisher kafkaBus;
    private String sourceName = "FeedPublisher";

    public MulticastPublisher(String mcastGroup, int mcastPort, KafkaPublisher kafkaBus) {
        this.MULTICAST_PORT = mcastPort;
        this.MULTICAST_GROUP = mcastGroup;
        this.kafkaBus = kafkaBus;
    }

    public void generateFeed(int noPackets, int throttling) {
        System.out.println("Starting feedGeneration: packets" + noPackets);
        try {
            InetAddress McastAddress = InetAddress.getByName(MULTICAST_GROUP);
            MulticastSocket mcastSocket = new MulticastSocket();
//            mcastSocket.setNetworkInterface(NetworkInterface.getByName("eth1"));
            mcastSocket.setNetworkInterface(NetworkInterface.getByName("vboxnet2"));
            for (int i = 0; i < noPackets; i++) {
                int feedId = 10000 + i;
                TestFeedMessage testFeedMsg = new TestFeedMessage(feedId, "TEST PRD", 100);

                DatagramPacket packet = new DatagramPacket(testFeedMsg.getBytes(), testFeedMsg.getBytes().length);
                packet.setAddress(McastAddress);
                packet.setPort(MULTICAST_PORT);

                mcastSocket.send(packet);
                sendBenchmark(feedId);

                Thread.sleep(throttling);
            }

        } catch (IOException e) {
            System.out.println(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendBenchmark(int feedId) {
        long timestamp = System.nanoTime();
        HashMap benchmarkMsg = new HashMap();
        benchmarkMsg.put("feedId", String.valueOf(feedId));
        benchmarkMsg.put("feedTs", String.valueOf(timestamp));
        benchmarkMsg.put("sourceId", this.sourceName);
        Gson gson = new GsonBuilder().create();
        kafkaBus.send(gson.toJson(benchmarkMsg));
    }

    private int getFeedId() {
        Random rnd = new Random();
        int minUID = 10000;
        return minUID + rnd.nextInt(90000);
    }
}