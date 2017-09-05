package org.project;

import org.project.feedpublisher.MulticastPublisher;

import java.util.logging.Logger;

/**
 * Created by jojo on 7/2/17.
 */
public class TestRunner {
    public final static Logger logger = Logger.getLogger("Framework");
    public static void main(String[] args) {
        MulticastPublisher feedMulticastPublisher = new MulticastPublisher("224.0.0.1", 2000);
        feedMulticastPublisher.generateFeed(1, 1);
    }
}
