package org.project;

import org.project.network.OrderGateway;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class OrderGatewayRunner
{
    public static void main( String[] args ) throws IOException {
        InputStream configFile = OrderGatewayRunner.class.getClassLoader().getResourceAsStream("config.properties");
        Properties prop = new Properties();
        prop.load(configFile);

        String kafkaTopic = prop.getProperty("kafka-topic");
        String kafkaBootstrap = prop.getProperty("kafka-bootstrap");
        String orderListenIp = prop.getProperty("order-listen-ip");
        int orderPort = Integer.parseInt(prop.getProperty("order-port"));

        OrderGateway testOrderGateway = new OrderGateway(orderPort, orderListenIp);
        Thread orderGatewayThread = new Thread(testOrderGateway);
        orderGatewayThread.start();
    }
}
