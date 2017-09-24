package org.project;

import com.google.gson.Gson;
import org.project.network.KafkaPublisher;
import org.project.network.MulticastPublisher;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.logging.Logger;

public class TestRunner {
    public final static Logger logger = Logger.getLogger("Framework");
    private enum TestInfo { START, STOP};
    private KafkaPublisher kafkaBus;
    private MulticastPublisher feedPublisher;
    private String testName = null;

    public TestRunner(KafkaPublisher kafkaBus, MulticastPublisher feedPublisher,  String testName) {
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


    public static void main(String[] args) throws InterruptedException {
        KafkaPublisher kafkaBus = new KafkaPublisher("framework:9092", "measurements");
        MulticastPublisher feedPublisher = new MulticastPublisher("224.0.0.1", 2000, kafkaBus);
        TestRunner testRun = new TestRunner(kafkaBus, feedPublisher, "test_1");
        testRun.run(2, 1);
    }
}
