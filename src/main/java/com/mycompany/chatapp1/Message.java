
package com.mycompany.chatapp1;

import java.util.Random;

public class Message {

    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private String sendStatus;

    private static int totalMessages = 0;

    // Generates a message ID of no more than 10 digits.
    public String generateMessageID() {
        Random random = new Random();

        messageID = String.valueOf(
                100000000L + random.nextInt(900000000)
        );

        return messageID;
    }

    // Checks whether the message ID is no longer than 10 characters.
    public boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }

    // Checks the recipient's South African international cellphone number.
    public boolean checkRecipientCell(String cell) {
        return cell != null && cell.matches("\\+27\\d{9}");
    }

    // Checks whether a message is 250 characters or fewer.
    public boolean checkMessageLength(String text) {
        return text != null && text.length() <= 250;
    }

    // Creates a message hash.
    public String createMessageHash(
            String id, int count, String text) {

        if (id == null || text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] words = text.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        String firstTwoID = id.substring(
                0, Math.min(2, id.length()));

        messageHash = (firstTwoID + ":"
                + count + ":"
                + firstWord + lastWord).toUpperCase();

        return messageHash;
    }

    // Sets the message details.
    public void setMessageDetails(
            String recipient, String message) {

        this.recipient = recipient;
        this.message = message;
    }

    // Allows the user to send, discard, or store a message.
    public String sendMessage(String choice) {

        if (choice == null) {
            return "Invalid choice.";
        }

        switch (choice.toLowerCase()) {

            case "send":
                sendStatus = "Sent";
                totalMessages++;
                return "Message successfully sent.";

            case "discard":
                sendStatus = "Discarded";
                return "Message discarded.";

            case "store":
                sendStatus = "Stored";
                return "Message stored for later.";

            default:
                return "Invalid choice.";
        }
    }

    // Returns the total number of messages sent.
    public int returnTotalMessages() {
        return totalMessages;
    }

    // Displays the message details.
    public String printMessages() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message
                + "\nStatus: " + sendStatus;
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getSendStatus() {
        return sendStatus;
    }
}
