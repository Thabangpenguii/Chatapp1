
package com.mycompany.chatapp1;

import java.util.Random;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Message {

    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private String sendStatus;

    private static int totalMessages = 0;

    public String generateMessageID() {
        Random random = new Random();
        messageID = String.valueOf(
                100000000L + random.nextInt(900000000));
        return messageID;
    }

    public boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }

    public boolean checkRecipientCell(String cell) {
        return cell != null && cell.matches("\\+27\\d{9}");
    }

    public boolean checkMessageLength(String text) {
        return text != null && text.length() <= 250;
    }

    public String createMessageHash(
            String id, int count, String text) {

        if (id == null || text == null
                || text.trim().isEmpty()) {
            return "";
        }

        String[] words = text.trim().split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];
        String firstTwoID =
                id.substring(0, Math.min(2, id.length()));

        messageHash = (firstTwoID + ":" + count + ":"
                + firstWord + lastWord).toUpperCase();

        return messageHash;
    }

    public void setMessageDetails(
            String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
    }

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

    // Saves this message to messages.json.
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
                String inside =
                        existing.substring(1, existing.length() - 1).trim();

                contents = "[" + inside + "," + object + "]";
            }

            Files.writeString(
                    file, contents, StandardCharsets.UTF_8);

            return "Message saved to messages.json.";

        } catch (IOException e) {
            return "Could not store message: " + e.getMessage();
        }
    }

    // Escapes special characters so the output remains valid JSON.
    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public int returnTotalMessages() {
        return totalMessages;
    }

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
