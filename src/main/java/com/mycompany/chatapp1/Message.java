
package com.mycompany.chatapp1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Message {

    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private String sendStatus;
    private String sender;

    private static int totalMessages = 0;
    private static final Set<String> USED_IDS = new HashSet<>();

    // Part 3 arrays
    private static ArrayList<String> sentMessages = new ArrayList<>();
    private static ArrayList<String> disregardedMessages = new ArrayList<>();
    private static ArrayList<String> storedMessages = new ArrayList<>();
    private static ArrayList<String> messageHashes = new ArrayList<>();
    private static ArrayList<String> messageIDs = new ArrayList<>();

    // Additional arrays for stored message details
    private static ArrayList<String> storedRecipients = new ArrayList<>();
    private static ArrayList<String> storedSenders = new ArrayList<>();
    private static ArrayList<String> storedStatuses = new ArrayList<>();

    private static final Path FILE = Path.of("messages.json");

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

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

    // Checks the message ID.
    public boolean checkMessageID(String id) {
        return id != null && id.matches("\\d{10}");
    }

    // Validates recipient cellphone number.
    public boolean checkRecipientCell(String cell) {
        return cell != null && cell.matches("\\+27\\d{9}");
    }

    // Checks message length.
    public boolean checkMessageLength(String text) {
        return text != null && text.length() < 250;
    }

    // Creates message hash.
    public String createMessageHash(
            String id, int count, String text) {

        if (id == null || text == null
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

    // Stores recipient and message details.
    public void setMessageDetails(
            String recipient, String message) {

        this.recipient = recipient;
        this.message = message;
    }

    // Sets sender name.
    public void setSender(String sender) {
        this.sender = sender;
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

                sentMessages.add(message);

                return "Message successfully sent.";

            case "discard":

                sendStatus = "Discarded";
                disregardedMessages.add(message);

                return "Message discarded.";

            case "store":

                sendStatus = "Stored";

                return "Message stored for later.";

            default:

                return "Invalid choice.";
        }
    }

    // Saves a stored message to messages.json.
    public String storeMessageAsJson() {

        if (messageID == null || recipient == null
                || message == null || messageHash == null
                || sendStatus == null) {

            return "Message details are incomplete.";
        }

        if (!sendStatus.equals("Stored")) {
            return "Only stored messages can be saved.";
        }

        try {

            JsonArray messages = new JsonArray();

            if (Files.exists(FILE)) {

                String existing = Files.readString(
                        FILE, StandardCharsets.UTF_8).trim();

                if (!existing.isEmpty()) {

                    JsonElement parsed = JsonParser.parseString(existing);

                    if (parsed.isJsonArray()) {
                        messages = parsed.getAsJsonArray();
                    }
                }
            }

            JsonObject object = new JsonObject();

            object.addProperty("messageID", messageID);
            object.addProperty("messageHash", messageHash);
            object.addProperty("sender",
                    sender == null ? "Not recorded" : sender);
            object.addProperty("recipient", recipient);
            object.addProperty("message", message);
            object.addProperty("status", sendStatus);

            messages.add(object);

            Files.writeString(
                    FILE,
                    GSON.toJson(messages),
                    StandardCharsets.UTF_8
            );

            loadStoredMessages();

            return "Message saved to messages.json.";

        } catch (IOException | RuntimeException e) {

            return "Could not store message: " + e.getMessage();
        }
    }

    // Loads stored messages from JSON into the arrays.
    public static int loadStoredMessages() {

        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        storedRecipients.clear();
        storedSenders.clear();
        storedStatuses.clear();

        if (!Files.exists(FILE)) {
            return 0;
        }

        try {

            String contents = Files.readString(
                    FILE, StandardCharsets.UTF_8);

            if (contents.trim().isEmpty()) {
                return 0;
            }

            JsonElement parsed = JsonParser.parseString(contents);

            if (!parsed.isJsonArray()) {
                return 0;
            }

            JsonArray messages = parsed.getAsJsonArray();

            for (JsonElement element : messages) {

                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject object = element.getAsJsonObject();

                String status = getJsonValue(object, "status");

                if (!"Stored".equalsIgnoreCase(status)) {
                    continue;
                }

                storedMessages.add(
                        getJsonValue(object, "message"));

                messageHashes.add(
                        getJsonValue(object, "messageHash"));

                messageIDs.add(
                        getJsonValue(object, "messageID"));

                storedRecipients.add(
                        getJsonValue(object, "recipient"));

                storedSenders.add(
                        getJsonValue(object, "sender"));

                storedStatuses.add(status);
            }

        } catch (IOException | RuntimeException e) {

            System.out.println(
                    "Error loading stored messages: " + e.getMessage());
        }

        return storedMessages.size();
    }

    // Safely reads a JSON field.
    private static String getJsonValue(
            JsonObject object, String field) {

        if (object.has(field) && !object.get(field).isJsonNull()) {
            return object.get(field).getAsString();
        }

        return "Not recorded";
    }

    // Displays all stored messages.
    public String displayStoredMessages() {

        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < storedMessages.size(); i++) {

            result.append("\nMessage ")
                    .append(i + 1)
                    .append("\n");

            result.append(formatStoredMessage(i));
            result.append("\n--------------------------\n");
        }

        return result.toString();
    }

    // Finds the longest stored message.
    public String findLongestMessage() {

        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        int longestIndex = 0;

        for (int i = 1; i < storedMessages.size(); i++) {

            if (storedMessages.get(i).length()
                    > storedMessages.get(longestIndex).length()) {

                longestIndex = i;
            }
        }

        return "Longest stored message:\n"
                + formatStoredMessage(longestIndex)
                + "\nMessage length: "
                + storedMessages.get(longestIndex).length()
                + " characters.";
    }

    // Searches for a stored message using its ID.
    public String searchMessageByID(String id) {

        for (int i = 0; i < messageIDs.size(); i++) {

            if (messageIDs.get(i).equals(id)) {

                return "Message found:\n"
                        + formatStoredMessage(i);
            }
        }

        return "No message found with ID: " + id;
    }

    // Searches for stored messages using recipient number.
    public String searchMessagesByRecipient(String recipient) {

        StringBuilder result = new StringBuilder();
        boolean found = false;

        for (int i = 0; i < storedRecipients.size(); i++) {

            if (storedRecipients.get(i).equals(recipient)) {

                found = true;

                result.append("\n")
                        .append(formatStoredMessage(i))
                        .append("\n--------------------------\n");
            }
        }

        if (!found) {
            return "No messages found for recipient: " + recipient;
        }

        return result.toString();
    }

    // Deletes a stored message using its hash.
    public String deleteMessageByHash(String hash) {

        if (!Files.exists(FILE)) {
            return "No stored messages file found.";
        }

        try {

            String contents = Files.readString(
                    FILE, StandardCharsets.UTF_8);

            JsonArray messages =
                    JsonParser.parseString(contents).getAsJsonArray();

            boolean deleted = false;

            for (int i = 0; i < messages.size(); i++) {

                JsonObject object = messages.get(i).getAsJsonObject();

                String savedHash =
                        getJsonValue(object, "messageHash");

                String status =
                        getJsonValue(object, "status");

                if (savedHash.equals(hash)
                        && status.equalsIgnoreCase("Stored")) {

                    messages.remove(i);
                    deleted = true;
                    break;
                }
            }

            if (!deleted) {
                return "No stored message found with that hash.";
            }

            Files.writeString(
                    FILE,
                    GSON.toJson(messages),
                    StandardCharsets.UTF_8
            );

            loadStoredMessages();

            return "Stored message successfully deleted.";

        } catch (IOException | RuntimeException e) {

            return "Could not delete message: " + e.getMessage();
        }
    }

    // Generates a complete report of stored messages.
    public String generateReport() {

        if (storedMessages.isEmpty()) {
            return "No stored messages available for the report.";
        }

        StringBuilder report = new StringBuilder();

        report.append("========== STORED MESSAGE REPORT ==========\n");

        report.append("Total stored messages: ")
                .append(storedMessages.size())
                .append("\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            report.append("\nMessage ")
                    .append(i + 1)
                    .append("\n");

            report.append(formatStoredMessage(i));

            report.append("\n-------------------------------------------\n");
        }

        report.append("============== END OF REPORT ==============");

        return report.toString();
    }

    // Formats the details of one stored message.
    private String formatStoredMessage(int index) {

        return "Message ID: " + messageIDs.get(index)
                + "\nMessage Hash: " + messageHashes.get(index)
                + "\nSender: " + storedSenders.get(index)
                + "\nRecipient: " + storedRecipients.get(index)
                + "\nMessage: " + storedMessages.get(index)
                + "\nStatus: " + storedStatuses.get(index);
    }

    // Returns the total number of messages sent.
    public int returnTotalMessages() {
        return totalMessages;
    }

    // Displays current message details.
    public String printMessages() {

        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message
                + "\nStatus: " + sendStatus;
    }

    // Part 3 array getters.
    public static ArrayList<String> getSentMessages() {
        return sentMessages;
    }

    public static ArrayList<String> getDisregardedMessages() {
        return disregardedMessages;
    }

    public static ArrayList<String> getStoredMessages() {
        return storedMessages;
    }

    public static ArrayList<String> getMessageHashes() {
        return messageHashes;
    }

    public static ArrayList<String> getMessageIDs() {
        return messageIDs;
    }

    // Existing getters.
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
