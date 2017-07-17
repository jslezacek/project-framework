package org.project;

/**
 * Created by jojo on 7/2/17.
 */
public class TestRunner {

    public static void main(String[] args) {
        MulticastPublisher feedMulticastPublisher = new MulticastPublisher("224.0.0.1", 2000);
        feedMulticastPublisher.generateFeed(100, 1);
    }
}
