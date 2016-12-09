package com.example.linos.testapp;

/**
 * Checks that the password the user created
 * is valid it has to contain:
 *     - 8 to 10 Characters
 *     - One UpperCase
 *     - One LowerCase
 *     - One Special Char
 *     - One Number
 */

public class PasswordChecker {
    public boolean isValidPassword(String password) {
        final String lowerChars = "qwertyuiopasdfghjklzxcvbnm";
        final String upperChars = "QWERTYUIOPASDFGHJKLZXCVBNM";
        final String digits = "0123456789";
        final String specials = "!@#$%^&*? ";
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;


        if(password.length() > 15 || password.length() < 8) {
            return false;
        }

        for(int i = 0; i < password.length(); i++) {

            String currentSymbol = password.charAt(i) + "";
            if(lowerChars.contains(currentSymbol)) {
                hasLower = true;
            }

            if(upperChars.contains(currentSymbol)) {
                hasUpper = true;
            }

            if(digits.contains(currentSymbol)) {
                hasDigit = true;
            }

            if(specials.contains(currentSymbol)) {
                hasSpecial = true;
            }
        }

        if(!hasLower || !hasUpper || !hasDigit || !hasSpecial) {
            return false;
        } else return true;
    }
}
