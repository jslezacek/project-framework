package org.project;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        OrderGateway testOrderGateway = new OrderGateway(10000, "127.0.0.1");
        Thread orderGatewayThread = new Thread(testOrderGateway);
        orderGatewayThread.start();
        System.out.println("Not blocked");
//        MulticastPublisher feedMulticastPublisher = new MulticastPublisher("224.0.0.1", 2000);
//        feedMulticastPublisher.generateFeed(10);
    }
}
