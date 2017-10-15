package org.project.messages;
import com.google.common.annotations.VisibleForTesting;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestFeedMessageTest extends TestCase {

    public void testGetBytes() throws Exception {
        TestFeedMessage testMsg = new TestFeedMessage(10000, "TEST PRD", 100);
        byte[] byteMsg = testMsg.getBytes();
        TestFeedMessage parsedMsg = TestFeedMessage.parse(byteMsg);
        assertEquals(parsedMsg.getProduct(), testMsg.getProduct());
        System.out.println(parsedMsg.getSeqNo());
    }
}