package org.project;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.project.messages.OrderMessage;

import java.nio.charset.StandardCharsets;

public class OrderMessageParserTest extends TestCase {
    String singleMessage = "\u00018=FIX-5.0|35=ZZ|11=trader1-001|55=APPL|44=100.20\u0001";
    String multiMessage = "\u00018=FIX-5.0|35=ZZ|11=trader1-001|55=APPL|44=100.20\u0001\u00018=FIX-5.0|35=ZZ|11=trader1-001|55=APPL|44=100.20\u0001";
    byte[] MESSAGE_DELIMITER = "\u0001".getBytes(StandardCharsets.US_ASCII);

    public void testOrderSingleMessageParsing() {
        System.out.println("Hello");
        byte[] buffer = singleMessage.getBytes(StandardCharsets.US_ASCII);
        int receivedBytes = buffer.length;
        int messageStart = 0;
        for (int delimiter_index = 0; delimiter_index <= receivedBytes - 1; delimiter_index++) {
            if (buffer[delimiter_index] == MESSAGE_DELIMITER[0] && delimiter_index > 0) { // if first character delimiter, continue reading
                String message = new String(buffer, messageStart, delimiter_index);     // parse message from last delimiter to current delimiter
                System.out.println(message);
                System.out.println("Found delimiter position" + delimiter_index);
                System.out.println(System.nanoTime());
                messageStart = delimiter_index; // start reading from last delimiter position.
            }
        }
    }

    public void testMultiMessageByteParse() {
        byte[] buffer = multiMessage.getBytes(StandardCharsets.US_ASCII);
        int receivedBytes = buffer.length;

        int messageStart = 0;
        for (int delimiter_index = 0; delimiter_index <= receivedBytes - 1; delimiter_index++) {                    // read all data from buffer
            if (buffer[delimiter_index] == MESSAGE_DELIMITER[0] && delimiter_index - messageStart > 1) { // if first character delimiter, continue reading
                delimiter_index += 1; // include ending delimiter
                String message = new String(buffer, messageStart, delimiter_index - messageStart);     // parse message from last delimiter to current delimiter
                System.out.println(message);
                System.out.println("Found delimiter position " + delimiter_index);
                System.out.println(System.nanoTime());
                messageStart = delimiter_index; // start reading from last delimiter position. (+1 include delimiter)
            }
        }
    }

    public void testMultiMessageByteParse2() {
        byte[] buffer = multiMessage.getBytes(StandardCharsets.US_ASCII);
        int receivedBytes = buffer.length;
        OrderMessage orderMsg = new OrderMessage();

        orderMsg.parse(receivedBytes, buffer);

    }
}
