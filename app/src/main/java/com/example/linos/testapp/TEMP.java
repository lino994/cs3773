package com.example.linos.testapp;

import java.util.Random;

/**
 * Created by mike_ on 11/30/2016.
 */

public class TEMP {

//    public String generatePassword() {
//        Random r = new Random();
//        final int length = r.nextInt(16 - 8) + 8;
//        final String charList = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM!@#$&%?^*";
//        final String upperChar = "QWERTYUIOPASDFGHJKLZXCVBNM";
//        final String digits = "0123456789";
//        final String specials = "!@#$&%?^*";
//        char currentSymbol;
//        boolean hasUpper = false;
//        boolean hasSpecial = false;
//        boolean hasNumber = false;
//
//        StringBuilder password = new StringBuilder(length);
//        for(int i = 0; i < length; i++) {
//            currentSymbol = charList.charAt(r.nextInt(charList.length()));
//            password.append(currentSymbol);
//            if(upperChar.contains(currentSymbol)) hasUpper = true;
//            if(digits.contains(currentSymbol)) hasNumber = true;
//            if(specials.contains(currentSymbol)) hasSpecial = true;
//        }
//
//        return password.toString();
//    }

    /**
     * Checks is a password is valid.
     * @param password password being checked
     * @return true if valid, false if invalid
     */
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
