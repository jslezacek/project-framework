package org.project.ordergateway;

public class OrderGatewayRunner
{
    public static void main( String[] args )
    {

        OrderGateway testOrderGateway = new OrderGateway(10000, "127.0.0.1");
//        testOrderGateway.run();
        Thread orderGatewayThread = new Thread(testOrderGateway);
        orderGatewayThread.start();
    }
}
