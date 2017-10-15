package org.project;

import com.google.gson.Gson;
import org.project.network.KafkaPublisher;
import org.project.network.MulticastPublisher;
import com.google.gson.GsonBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

public class FeedPublisherRunner {
    public final static Logger logger = Logger.getLogger("Framework");
    private enum TestInfo { START, STOP};
    private KafkaPublisher kafkaBus;
    private MulticastPublisher feedPublisher;
    private String testName = null;

    public FeedPublisherRunner(KafkaPublisher kafkaBus, MulticastPublisher feedPublisher, String testName) {
        this.kafkaBus = kafkaBus;
        this.testName = testName;
        this.feedPublisher = feedPublisher;
    }

    public void run(int noMessages, int throttling) throws InterruptedException {
        this.notify(TestInfo.START);
        this.feedPublisher.generateFeed(noMessages, throttling);
        this.notify(TestInfo.STOP);
    }

    private void notify(TestInfo testInfo) throws InterruptedException {
        long timestamp = System.currentTimeMillis();
        HashMap benchmarkInfo = new HashMap();
        benchmarkInfo.put("testId", this.testName);
        switch (testInfo) {
            case START:
                benchmarkInfo.put("START", String.valueOf(timestamp));
                break;
            case STOP:
                benchmarkInfo.put("STOP", String.valueOf(timestamp));
                Thread.sleep(2000);
                break;
        }

        Gson gson = new GsonBuilder().create();
        this.kafkaBus.send(gson.toJson(benchmarkInfo));
        this.kafkaBus.flush();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int numMessages = 0;
        String testName = null;
        if (args.length == 2) {
            testName = args[0];
            numMessages = Integer.parseInt(args[1]);
        } else {
            logger.warning("Require 2 mandatory arguments: [testName] [numMessages]");
            System.exit(1);
        }

        InputStream configFile = FeedPublisherRunner.class.getClassLoader().getResourceAsStream("config.properties");
        Properties prop = new Properties();
        prop.load(configFile);

        String kafkaTopic = prop.getProperty("kafka-topic");
        String kafkaBootstrap = prop.getProperty("kafka-bootstrap");
        String feedMcastGroup = prop.getProperty("feed-mcast-group");
        int feedMcastPort = Integer.parseInt(prop.getProperty("mcast-port"));

        KafkaPublisher kafkaBus = new KafkaPublisher(kafkaBootstrap, kafkaTopic);
        MulticastPublisher feedPublisher = new MulticastPublisher(feedMcastGroup, feedMcastPort, kafkaBus);
        FeedPublisherRunner testRun = new FeedPublisherRunner(kafkaBus, feedPublisher, testName);
        testRun.run(numMessages, 0);
    }
}