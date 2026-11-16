
package com.mycompany.chatapp1;

import java.util.Scanner;

public class Chatapp1 {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        // Registration
        System.out.println("=== Chat App Registration ===");

        System.out.print("Enter first name: ");
        String firstName = input.nextLine();

        System.out.print("Enter last name: ");
        String lastName = input.nextLine();

        Login login = new Login(firstName, lastName);

        System.out.print("Enter username: ");
        String username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        System.out.print("Enter cellphone number: ");
        String cellphone = input.nextLine();

        String registrationMessage =
                login.registerUser(username, password, cellphone);

        System.out.println(registrationMessage);

        // Login
        System.out.println("\n=== Login ===");

        System.out.print("Enter username: ");
        String loginUsername = input.nextLine();

        System.out.print("Enter password: ");
        String loginPassword = input.nextLine();

        boolean loginResult =
                login.loginUser(loginUsername, loginPassword);

        System.out.println("Login status: " + loginResult);
        System.out.println(login.returnLoginStatus());

        // Messaging menu
        if (loginResult) {

            Message message = new Message();

            // Set the sender's name
            message.setSender(firstName + " " + lastName);

            boolean running = true;

            while (running) {

                System.out.println("\n=== Messaging Menu ===");
                System.out.println("1. Send a message");
                System.out.println("2. View total messages sent");
                System.out.println("3. Exit");
                System.out.println("4. Stored Messages");
                System.out.print("Choose an option: ");

                String choice = input.nextLine();

                switch (choice) {

                    case "1":

                        System.out.print(
                                "Enter recipient cellphone (+27...): ");
                        String recipient = input.nextLine();

                        System.out.print(
                                "Enter your message (maximum 250 characters): ");
                        String text = input.nextLine();

                        if (!message.checkRecipientCell(recipient)) {
                            System.out.println(
                                    "Invalid recipient cellphone number.");
                            break;
                        }

                        if (!message.checkMessageLength(text)) {
                            System.out.println(
                                    "Please enter a message of less than 250 characters.");
                            break;
                        }

                        String id = message.generateMessageID();

                        message.setMessageDetails(recipient, text);

                        String hash = message.createMessageHash(
                                id,
                                message.returnTotalMessages() + 1,
                                text
                        );

                        System.out.println("Message ID: " + id);
                        System.out.println("Message Hash: " + hash);

                        System.out.println(
                                "Choose: send, discard, or store");
                        String action = input.nextLine();

                        System.out.println(message.sendMessage(action));

                        if ("send".equalsIgnoreCase(action)) {
                            System.out.println(message.printMessages());
                        }

                        if ("store".equalsIgnoreCase(action)) {
                            System.out.println(
                                    message.storeMessageAsJson());
                        }

                        break;

                    case "2":

                        System.out.println(
                                "Total messages sent: "
                                + message.returnTotalMessages());
                        break;

                    case "3":

                        running = false;
                        System.out.println("Exiting Chat App.");
                        break;

                    case "4":

                        boolean storedMenu = true;

                        while (storedMenu) {

                            System.out.println("\n=== Stored Messages ===");
                            System.out.println("1. Display stored messages");
                            System.out.println("2. Display the longest message");
                            System.out.println("3. Search for a message by ID");
                            System.out.println("4. Search for messages by recipient");
                            System.out.println("5. Delete a message using its hash");
                            System.out.println("6. Display complete message report");
                            System.out.println("7. Return to main menu");

                            System.out.print("Choose an option: ");
                            String storedChoice = input.nextLine();

                            switch (storedChoice) {

                                case "1":
                                    Message.loadStoredMessages();
                                    System.out.println(
                                            message.displayStoredMessages());
                                    break;

                                case "2":
                                    Message.loadStoredMessages();
                                    System.out.println(
                                            message.findLongestMessage());
                                    break;

                                case "3":
                                    System.out.print("Enter Message ID: ");
                                    String searchID = input.nextLine();

                                    Message.loadStoredMessages();
                                    System.out.println(
                                            message.searchMessageByID(searchID));
                                    break;

                                case "4":
                                    System.out.print(
                                            "Enter recipient cellphone number: ");
                                    String searchRecipient = input.nextLine();

                                    Message.loadStoredMessages();
                                    System.out.println(
                                            message.searchMessagesByRecipient(
                                                    searchRecipient));
                                    break;

                                case "5":
                                    System.out.print("Enter Message Hash: ");
                                    String deleteHash = input.nextLine();

                                    Message.loadStoredMessages();
                                    System.out.println(
                                            message.deleteMessageByHash(
                                                    deleteHash));
                                    break;

                                case "6":
                                    Message.loadStoredMessages();
                                    System.out.println(
                                            message.generateReport());
                                    break;

                                case "7":
                                    storedMenu = false;
                                    break;

                                default:
                                    System.out.println(
                                            "Invalid option. Please try again.");
                            }
                        }

                        break;

                    default:
                        System.out.println(
                                "Invalid option. Please try again.");
                }
            }
        }

        input.close();
    }
}

