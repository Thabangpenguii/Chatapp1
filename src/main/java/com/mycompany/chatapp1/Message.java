
package com.mycompany.chatapp1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Message {

    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private String sendStatus;

    private static int totalMessages = 0;
    private static final Set<String> USED_IDS = new HashSet<>();

    // Generates a unique 10-digit message ID.
    public String generateMessageID() {
        String id;

        do {
            long number = ThreadLocalRandom.current()
                    .nextLong(1_000_000_000L, 10_000_000_000L);
            id = String.valueOf(number);
        } while (USED_IDS.contains(id));

        USED_IDS.add(id);
        messageID = id;

        return messageID;
    }

    // Checks that the message ID contains exactly 10 digits.
    public boolean checkMessageID(String id) {
        return id != null && id.matches("\\d{10}");
    }

    // Validates the South African recipient cellphone number.
    public boolean checkRecipientCell(String cell) {
        return cell != null && cell.matches("\\+27\\d{9}");
    }

    // Accepts messages shorter than 250 characters.
    public boolean checkMessageLength(String text) {
        return text != null && text.length() < 250;
    }

    // Creates the message hash using the first two ID digits,
    // message number, first word and last word.
    public String createMessageHash(
            String id, int count, String text) {

        if (!checkMessageID(id)
                || text == null
                || text.trim().isEmpty()) {
            return "";
        }

        String[] words = text.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        String firstTwoID = id.substring(0, 2);

        messageHash = (firstTwoID + ":" + count + ":"
                + firstWord + lastWord).toUpperCase();

        return messageHash;
    }

    // Stores the recipient and message text.
    public void setMessageDetails(
            String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
    }

    // Sends, discards or stores a message.
    public String sendMessage(String choice) {

        if (choice == null) {
            return "Invalid choice.";
        }

        switch (choice.trim().toLowerCase()) {

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

    // Saves the message details to messages.json.
    public String storeMessageAsJson() {

        if (messageID == null || recipient == null
                || message == null || messageHash == null
                || sendStatus == null) {
            return "Message details are incomplete.";
        }

        Path file = Path.of("messages.json");

        try {
            String object = "{"
                    + "\"messageID\":\"" + escapeJson(messageID) + "\","
                    + "\"messageHash\":\"" + escapeJson(messageHash) + "\","
                    + "\"recipient\":\"" + escapeJson(recipient) + "\","
                    + "\"message\":\"" + escapeJson(message) + "\","
                    + "\"status\":\"" + escapeJson(sendStatus) + "\""
                    + "}";

            String existing = "[]";

            if (Files.exists(file)) {
                existing = Files.readString(
                        file, StandardCharsets.UTF_8).trim();

                if (existing.isEmpty()) {
                    existing = "[]";
                }
            }

            if (!existing.startsWith("[")
                    || !existing.endsWith("]")) {
                return "Could not store message: JSON file is invalid.";
            }

            String contents;

            if (existing.equals("[]")) {
                contents = "[" + object + "]";
            } else {
                String inside = existing.substring(
                        1, existing.length() - 1).trim();

                contents = "[" + inside + "," + object + "]";
            }

            Files.writeString(file, contents, StandardCharsets.UTF_8);

            return "Message saved to messages.json.";

        } catch (IOException e) {
            return "Could not store message: " + e.getMessage();
        }
    }

    // Escapes special characters for JSON.
    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Returns the total number of messages sent.
    public int returnTotalMessages() {
        return totalMessages;
    }

    // Displays the current message details.
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
