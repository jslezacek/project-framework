package org.project.messages;
import org.project.feedpublisher.KafkaPublisher;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderMessage {

    private static final String MESSAGE_DELIMITER_STR = "\u0001";
    private static final byte MESSAGE_DELIMITER_BYTE = "\u0001".getBytes(StandardCharsets.US_ASCII)[0];
    private static final String FIELD_DELIMITER = "\\|";
    private static KafkaPublisher kafkaBus;
    HashMap<String, String> fieldMap = new HashMap<>();
    HashMap<String, String> fields = new HashMap<>();

    public OrderMessage() {
        fieldMap.put("35", "MsgType");
        fieldMap.put("OrderId", "11");
        fieldMap.put("Product", "55");
        fieldMap.put("44", "Price");
        kafkaBus = new KafkaPublisher("framework:9092");
    }

    public void parse(int receivedBytes, byte[] buffer) {
        List<String> result = new ArrayList<String>();
        int messageStart = 0;
        for (int delimiter_index = 0; delimiter_index <= receivedBytes - 1; delimiter_index++) {
            if (buffer[delimiter_index] == this.MESSAGE_DELIMITER_BYTE
                    && delimiter_index - messageStart > 1 ) { // if first character delimiter, continue reading
                delimiter_index += 1; // include ending delimiter
                String message = new String(buffer, messageStart, delimiter_index - messageStart);     // parse message from last delimiter to current delimiter
                messageStart = delimiter_index; // start reading from last delimiter position.
                System.out.println(stripDelimiter(message));
                long timestamp = System.nanoTime();
                this.fields = parseFields(message);
                String benchmarkMsg = "OrderId: " + this.fields.get(fieldMap.get("OrderId")) + " " + timestamp;
                kafkaBus.send("test", benchmarkMsg);
                System.out.println(benchmarkMsg);
            }
        }
    }

    public void setKafkaBus(String bootstrapServer) {
        this.kafkaBus = new KafkaPublisher("framework:9092");
    }

    private String[] splitField(String messageBody, String delimiter) {
        return messageBody.split(delimiter);
    }

    private HashMap<String, String> parseFields(String message) {
        HashMap<String, String> result = new HashMap<String, String>();
        String[] allFields = splitField(stripDelimiter(message), FIELD_DELIMITER);
        for (String field: allFields) {
            String[] s_field = splitField(field, "=");
            result.put(s_field[0],s_field[1]);
        }
        return result;
    }

    private String stripDelimiter(String messageBody) {
        return messageBody.replaceAll(MESSAGE_DELIMITER_STR, "");
    }
}
