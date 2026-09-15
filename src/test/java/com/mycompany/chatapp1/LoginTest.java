package com.mycompany.chatapp1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    @Test
    public void testCheckUserName() {
        Login login = new Login("John", "Smith");

        assertTrue(login.checkUserName("user_"));
    }

    @Test
    public void testInvalidUserName() {
        Login login = new Login("John", "Smith");

        assertFalse(login.checkUserName("username"));
    }

    @Test
    public void testCheckPasswordComplexity() {
        Login login = new Login("John", "Smith");

        assertTrue(login.checkPasswordComplexity("Password1!"));
    }

    @Test
    public void testInvalidPasswordComplexity() {
        Login login = new Login("John", "Smith");

        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckCellPhoneNumber() {
        Login login = new Login("John", "Smith");

        assertTrue(login.checkCellPhoneNumber("+27665256123"));
    }

    @Test
    public void testInvalidCellPhoneNumber() {
        Login login = new Login("John", "Smith");

        assertFalse(login.checkCellPhoneNumber("0665256123"));
    }

    @Test
    public void testRegisterUser() {
        Login login = new Login("John", "Smith");

        String result = login.registerUser(
                "user_",
                "Password1!",
                "+27665256123"
        );

        assertEquals("Username successfully captured.", result);
    }

    @Test
    public void testLoginUser() {
        Login login = new Login("John", "Smith");

        login.registerUser(
                "user_",
                "Password1!",
                "+27665256123"
        );

        assertTrue(login.loginUser("user_", "Password1!"));
    }

    @Test
    public void testInvalidLoginUser() {
        Login login = new Login("John", "Smith");

        login.registerUser(
                "user_",
                "Password1!",
                "+27665256123"
        );

        assertFalse(login.loginUser("wrong_", "WrongPassword1!"));
    }

    @Test
    public void testReturnLoginStatusSuccess() {
        Login login = new Login("John", "Smith");

        login.registerUser(
                "user_",
                "Password1!",
                "+27665256123"
        );

        login.loginUser("user_", "Password1!");

        assertEquals(
                "Welcome John Smith, it is great to see you again.",
                login.returnLoginStatus()
        );
    }

    @Test
    public void testReturnLoginStatusFailed() {
        Login login = new Login("John", "Smith");

        login.registerUser(
                "user_",
                "Password1!",
                "+27665256123"
        );

        login.loginUser("wrong_", "WrongPassword1!");

        assertEquals(
                "Username or password incorrect, please try again.",
                login.returnLoginStatus()
        );
    }
}