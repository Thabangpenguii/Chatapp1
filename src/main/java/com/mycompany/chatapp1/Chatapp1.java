
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

        // Register user
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

        // Display messaging menu only after successful login
        if (loginResult) {

            Message message = new Message();
            boolean running = true;

            while (running) {

                System.out.println("\n=== Messaging Menu ===");
                System.out.println("1. Send a message");
                System.out.println("2. View total messages sent");
                System.out.println("3. Exit");
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

                        // Validate recipient cellphone
                        if (!message.checkRecipientCell(recipient)) {
                            System.out.println(
                                    "Invalid recipient cellphone number.");
                            break;
                        }

                        // Validate message length
                        if (!message.checkMessageLength(text)) {
                            System.out.println(
                                    "Please enter a message of less than 250 characters.");
                            break;
                        }

                        // Generate message ID
                        String id = message.generateMessageID();

                        // Store message details
                        message.setMessageDetails(recipient, text);

                        // Generate message hash
                        String hash = message.createMessageHash(
                                id,
                                message.returnTotalMessages() + 1,
                                text
                        );

                        System.out.println("Message ID: " + id);
                        System.out.println("Message Hash: " + hash);

                        // Choose what to do with the message
                        System.out.println(
                                "Choose: send, discard, or store");
                        String action = input.nextLine();

                        System.out.println(
                                message.sendMessage(action));

                        // Display sent messages
                        if ("send".equalsIgnoreCase(action)) {
                            System.out.println(message.printMessages());
                        }

                        // Store message as JSON
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

                    default:
                        System.out.println(
                                "Invalid option. Please try again.");
                }
            }
        }

        input.close();
    }
}

