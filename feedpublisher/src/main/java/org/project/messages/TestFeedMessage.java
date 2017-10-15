package org.project.messages;

import java.util.Arrays;

public class TestFeedMessage {
    private final int len = 15;
    private final String msgType = "T";
    private final int seqNo;
    private final String product;
    private final int price;

    public TestFeedMessage(int seqNumber, String product, int price) {
        this.seqNo = seqNumber;
        this.product = product;
        this.price = price;
    }

    public String getMsgType() {
        return msgType;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public String getProduct() {
        return product;
    }

    public int getPrice() {
        return price;
    }

    public int getLen() {
        return len;
    }

    public byte[] getBytes() {
        byte[] byteMsg = new byte[this.getLen()];

        byte[] length = Codecs.encodeUInt16(this.getLen());
        byte[] type = Codecs.encodeAscii(this.getMsgType());
        byte[] seqNo = Codecs.encodeUInt16(this.getSeqNo());
        byte[] product = Codecs.encodeAscii(this.getProduct());
        byte[] price = Codecs.encodeUInt16(this.getPrice());

        System.arraycopy(length, 0, byteMsg, 0, 2);
        System.arraycopy(type, 0, byteMsg, 2, 1);
        System.arraycopy(seqNo, 0, byteMsg, 3, 2);
        System.arraycopy(product, 0, byteMsg, 5, 8);
        System.arraycopy(price, 0, byteMsg, 13, 2);
        return byteMsg;
    }

    public static TestFeedMessage parse(byte[] bytearray) {
        Integer seqNum = Codecs.decodeUInt16(Arrays.copyOfRange(bytearray, 3, 5 ));
        String product = Codecs.decodeAscii(Arrays.copyOfRange(bytearray, 5, 13 ));
        Integer price = Codecs.decodeUInt16(Arrays.copyOfRange(bytearray, 13, 15 ));
        TestFeedMessage result = new TestFeedMessage(seqNum, product, price);
        return result;
    }
}
