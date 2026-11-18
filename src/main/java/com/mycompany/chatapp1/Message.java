
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

    private static final ArrayList<String> sentMessages = new ArrayList<>();
    private static final ArrayList<String> disregardedMessages = new ArrayList<>();
    private static final ArrayList<String> storedMessages = new ArrayList<>();
    private static final ArrayList<String> messageHashes = new ArrayList<>();
    private static final ArrayList<String> messageIDs = new ArrayList<>();

    private static final ArrayList<String> storedRecipients = new ArrayList<>();
    private static final ArrayList<String> storedSenders = new ArrayList<>();
    private static final ArrayList<String> storedStatuses = new ArrayList<>();
    private static final ArrayList<String> storedHashes = new ArrayList<>();
    private static final ArrayList<String> storedIDs = new ArrayList<>();

    private static final Path FILE = Path.of("messages.json");

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

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

    public boolean checkMessageID(String id) {
        return id != null && id.matches("\\d{10}");
    }

    // Original South African international format validation.
    public boolean checkRecipientCell(String cell) {
        return cell != null && cell.matches("\\+27\\d{9}");
    }

    public boolean checkMessageLength(String text) {
        return text != null && text.length() < 250;
    }

    public String createMessageHash(String id, int count, String text) {

        if (id == null || id.length() < 2
                || text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] words = text.trim().split("\\s+");

        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        messageHash = (id.substring(0, 2) + ":"
                + count + ":" + firstWord + lastWord).toUpperCase();

        return messageHash;
    }

    public void setMessageDetails(String recipient, String message) {
        this.recipient = recipient;
        this.message = message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

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

            JsonArray messages = readJsonMessages();

            JsonObject object = new JsonObject();

            object.addProperty("messageID", messageID);
            object.addProperty("messageHash", messageHash);
            object.addProperty("sender",
                    sender == null ? "Not recorded" : sender);
            object.addProperty("recipient", recipient);
            object.addProperty("message", message);
            object.addProperty("status", sendStatus);

            messages.add(object);

            writeJsonMessages(messages);
            loadStoredMessages();

            return "Message saved to messages.json.";

        } catch (IOException | RuntimeException e) {
            return "Could not store message: " + e.getMessage();
        }
    }

    private static JsonArray readJsonMessages() throws IOException {

        if (!Files.exists(FILE)) {
            return new JsonArray();
        }

        String contents = Files.readString(
                FILE, StandardCharsets.UTF_8).trim();

        if (contents.isEmpty()) {
            return new JsonArray();
        }

        JsonElement parsed = JsonParser.parseString(contents);

        if (!parsed.isJsonArray()) {
            throw new IOException("Invalid messages.json format.");
        }

        return parsed.getAsJsonArray();
    }

    private static void writeJsonMessages(JsonArray messages)
            throws IOException {

        Files.writeString(
                FILE,
                GSON.toJson(messages),
                StandardCharsets.UTF_8
        );
    }

    public static int loadStoredMessages() {

        storedMessages.clear();
        storedRecipients.clear();
        storedSenders.clear();
        storedStatuses.clear();
        storedHashes.clear();
        storedIDs.clear();

        messageHashes.clear();
        messageIDs.clear();

        try {

            JsonArray messages = readJsonMessages();

            for (JsonElement element : messages) {

                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject object = element.getAsJsonObject();

                String id = getJsonValue(object, "messageID");
                String hash = getJsonValue(object, "messageHash");
                String status = getJsonValue(object, "status");

                if (!id.equals("Not recorded")) {
                    USED_IDS.add(id);
                }

                if (!status.equalsIgnoreCase("Stored")) {
                    continue;
                }

                storedMessages.add(getJsonValue(object, "message"));
                storedRecipients.add(getJsonValue(object, "recipient"));
                storedSenders.add(getJsonValue(object, "sender"));
                storedStatuses.add(status);
                storedHashes.add(hash);
                storedIDs.add(id);

                if (!id.equals("Not recorded")) {
                    messageIDs.add(id);
                }

                if (!hash.equals("Not recorded")) {
                    messageHashes.add(hash);
                }
            }

        } catch (IOException | RuntimeException e) {

            System.out.println(
                    "Error loading stored messages: " + e.getMessage());
        }

        return storedMessages.size();
    }

    private static String getJsonValue(JsonObject object, String field) {

        if (object.has(field) && !object.get(field).isJsonNull()) {
            return object.get(field).getAsString();
        }

        return "Not recorded";
    }

    // A: Display sender and recipient.
    public String displayStoredMessages() {

        loadStoredMessages();

        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < storedMessages.size(); i++) {

            result.append("\nStored Message ")
                    .append(i + 1).append("\n");

            result.append("Sender: ")
                    .append(storedSenders.get(i)).append("\n");

            result.append("Recipient: ")
                    .append(storedRecipients.get(i)).append("\n");

            result.append("Message: ")
                    .append(storedMessages.get(i)).append("\n");

            result.append("--------------------------\n");
        }

        return result.toString();
    }

    // B: Find longest stored message.
    public String findLongestMessage() {

        loadStoredMessages();

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

    // C: Search by message ID.
    public String searchMessageByID(String id) {

        loadStoredMessages();

        for (int i = 0; i < storedIDs.size(); i++) {

            if (storedIDs.get(i).equals(id)) {

                return "Message found:\n"
                        + "Recipient: " + storedRecipients.get(i)
                        + "\nMessage: " + storedMessages.get(i);
            }
        }

        return "No stored message found with ID: " + id;
    }

    // D: Search by recipient.
    public String searchMessagesByRecipient(String recipient) {

        loadStoredMessages();

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
            return "No stored messages found for recipient: " + recipient;
        }

        return result.toString();
    }

    // E: Delete using hash.
    public String deleteMessageByHash(String hash) {

        try {

            JsonArray messages = readJsonMessages();
            boolean deleted = false;

            for (int i = 0; i < messages.size(); i++) {

                JsonObject object = messages.get(i).getAsJsonObject();

                if (getJsonValue(object, "messageHash").equals(hash)
                        && getJsonValue(object, "status")
                        .equalsIgnoreCase("Stored")) {

                    messages.remove(i);
                    deleted = true;
                    break;
                }
            }

            if (!deleted) {
                return "No stored message found with that hash.";
            }

            writeJsonMessages(messages);
            loadStoredMessages();

            return "Stored message successfully deleted.";

        } catch (IOException | RuntimeException e) {
            return "Could not delete message: " + e.getMessage();
        }
    }

    // F: Generate report.
    public String generateReport() {

        loadStoredMessages();

        if (storedMessages.isEmpty()) {
            return "No stored messages available for the report.";
        }

        StringBuilder report = new StringBuilder();

        report.append("========== STORED MESSAGE REPORT ==========\n");
        report.append("Total stored messages: ")
                .append(storedMessages.size()).append("\n");

        for (int i = 0; i < storedMessages.size(); i++) {

            report.append("\nStored Message ")
                    .append(i + 1).append("\n");

            report.append(formatStoredMessage(i));
            report.append("\n-------------------------------------------\n");
        }

        report.append("============== END OF REPORT ==============");

        return report.toString();
    }

    private String formatStoredMessage(int index) {

        return "Message ID: " + storedIDs.get(index)
                + "\nMessage Hash: " + storedHashes.get(index)
                + "\nSender: " + storedSenders.get(index)
                + "\nRecipient: " + storedRecipients.get(index)
                + "\nMessage: " + storedMessages.get(index)
                + "\nStatus: " + storedStatuses.get(index);
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
