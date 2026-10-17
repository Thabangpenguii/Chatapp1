
package com.mycompany.chatapp1;

import com.mycompany.chatapp1.Message;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testGenerateMessageID() {
        Message message = new Message();

        String id = message.generateMessageID();

        assertNotNull(id);
        assertTrue(id.matches("\\d{9}"),
                "Message ID must contain 9 digits.");
        assertTrue(message.checkMessageID(id));
    }

    @Test
    public void testCheckMessageID() {
        Message message = new Message();

        assertTrue(message.checkMessageID("123456789"));
        assertTrue(message.checkMessageID("1234567890"));
        assertFalse(message.checkMessageID("12345678901"));
    }

    @Test
    public void testCheckRecipientCell() {
        Message message = new Message();

        assertTrue(message.checkRecipientCell("+27718693002"));
        assertFalse(message.checkRecipientCell("0718693002"));
        assertFalse(message.checkRecipientCell("+27123"));
    }

    @Test
    public void testMessageLength() {
        Message message = new Message();

        String validMessage = "Hello";
        String maximumMessage = "a".repeat(250);
        String longMessage = "a".repeat(251);

        assertTrue(message.checkMessageLength(validMessage));
        assertTrue(message.checkMessageLength(maximumMessage));
        assertFalse(message.checkMessageLength(longMessage));
    }

    @Test
    public void testCreateMessageHash() {
        Message message = new Message();

        String hash = message.createMessageHash(
                "123456789", 1,
                "Hi Mike, can you join us for dinner tonight?");

        assertEquals("12:1:HITONIGHT?", hash);
    }

    @Test
    public void testSendMessage() {
        Message message = new Message();

        assertEquals(
                "Message successfully sent.",
                message.sendMessage("send"));

        assertEquals("Sent", message.getSendStatus());
    }

    @Test
    public void testDiscardMessage() {
        Message message = new Message();

        assertEquals(
                "Message discarded.",
                message.sendMessage("discard"));

        assertEquals("Discarded", message.getSendStatus());
    }

    @Test
    public void testStoreMessage() {
        Message message = new Message();

        assertEquals(
                "Message stored for later.",
                message.sendMessage("store"));

        assertEquals("Stored", message.getSendStatus());
    }

    @Test
    public void testPrintMessages() {
        Message message = new Message();

        String id = message.generateMessageID();
        String text = "Hi Keegan, did you receive the payment?";

        message.setMessageDetails("+27718693002", text);
        message.createMessageHash(id, 1, text);
        message.sendMessage("send");

        String output = message.printMessages();

        assertTrue(output.contains("Message ID:"));
        assertTrue(output.contains("Message Hash:"));
        assertTrue(output.contains("Recipient: +27718693002"));
        assertTrue(output.contains("Message: " + text));
        assertTrue(output.contains("Status: Sent"));
    }
}
