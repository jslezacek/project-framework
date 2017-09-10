package org.project;

import com.google.gson.Gson;
import org.project.feedpublisher.KafkaPublisher;
import org.project.feedpublisher.MulticastPublisher;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.logging.Logger;

public class TestRunner {
    public final static Logger logger = Logger.getLogger("Framework");
    private enum TestInfo { START, STOP};
    private KafkaPublisher kafkaBus;
    private String testName = null;

    public TestRunner(KafkaPublisher kafkaBus, String testName) {
        this.kafkaBus = kafkaBus;
        this.testName = testName;
    }

    public static void main(String[] args) throws InterruptedException {
        KafkaPublisher kafkaBus = new KafkaPublisher("framework:9092", "measurements");
        TestRunner testRun = new TestRunner(kafkaBus, "test_2");
        MulticastPublisher feedMulticastPublisher = new MulticastPublisher("224.0.0.1", 2000, kafkaBus);

        testRun.notify(TestInfo.START);
        feedMulticastPublisher.generateFeed(5000, 1);
        testRun.notify(TestInfo.STOP);
    }

    public void notify(TestInfo testInfo) throws InterruptedException {
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
}
