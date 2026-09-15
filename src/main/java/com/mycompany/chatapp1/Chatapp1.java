package com.mycompany.chatapp1;
import java.util.Scanner;

public class Chatapp1 {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        Login login = new Login();

        // Registration
        System.out.println("=== Chat App Registration ===");

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

        if (loginResult) {
            System.out.println("Welcome to the Chat App!");
        } else {
            System.out.println("Username or password is incorrect.");
        }

        input.close();
    }
}