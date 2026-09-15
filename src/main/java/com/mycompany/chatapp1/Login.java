package com.mycompany.chatapp1;

public class Login {

    private String registeredUsername;
    private String registeredPassword;
    private String registeredCellphone;
    private boolean loginStatus;

    // Checks that the username contains an underscore and is no more than 5 characters.
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // Checks that the password has at least 8 characters, a capital letter,
    // a number and a special character.
    public boolean checkPasswordComplexity(String password) {

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (int i = 0; i < password.length(); i++) {

            char c = password.charAt(i);

            if (Character.isUpperCase(c)) {
                hasCapital = true;
            }

            if (Character.isDigit(c)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return password.length() >= 8
                && hasCapital
                && hasNumber
                && hasSpecial;
    }

    // Checks that the cellphone number follows the South African international format.
    public boolean checkCellPhoneNumber(String cellphone) {
        return cellphone.matches("^\\+27\\d{9}$");
    }

    // Registers the user after validating the username, password and cellphone number.
    public String registerUser(String username, String password, String cellphone) {

        if (!checkUserName(username)) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (!checkPasswordComplexity(password)) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (!checkCellPhoneNumber(cellphone)) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        registeredUsername = username;
        registeredPassword = password;
        registeredCellphone = cellphone;

        return "Username successfully captured.";
    }

    // Checks whether the entered username and password match the registered details.
    public boolean loginUser(String username, String password) {

        if (username.equals(registeredUsername)
                && password.equals(registeredPassword)) {

            loginStatus = true;

        } else {

            loginStatus = false;
        }

        return loginStatus;
    }

    // Returns the current login status.
    public boolean returnLoginStatus() {
        return loginStatus;
    }
}