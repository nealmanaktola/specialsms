package com.knowroaming.specialsms.helpers;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Neal on 3/24/2015.
 */
public class SpecialSMSHelper {
    //MAX_MESSAGE_SIZE is defined by SMS length * max_size of each character (2 characters)
    private static final int MAX_MESSAGE_SIZE = 160 * 2;

    //Checks to see if Message is Special
    public static boolean isValidSettings(String ipAddress, String port) {

        Pattern pattern;
        Matcher matcher;

        String IPADDRESS_PATTERN =
                "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";


        pattern = Pattern.compile(IPADDRESS_PATTERN);

        matcher = pattern.matcher(ipAddress);
        if (matcher.matches()) {
             try {
                 Integer.parseInt(port);
             } catch (NumberFormatException e) {
                Log.d("SpecialSMS", "Integer Parse Error");
                return false;
             }

            return true;
        }
        return false;
    }
    public static String encode(String message) {
        int messageLength = message.length();

        StringBuilder sb = new StringBuilder(MAX_MESSAGE_SIZE);

        for (int i = 0; i < messageLength; i++) {
            char c = Character.toUpperCase(message.charAt(i));

            if (c >= 'A' && c <= 'Z') {
                int val = (int) c - 'A' + 1; //Encodes A/a-Z/z -> 1 -> 26
                String valString = String.valueOf(val);
                sb.append(valString);
            }
        }

        return sb.toString();
    }
    public static boolean isSpecial(String message) {
        int messageLength = message.length();

        //Ensures last letter is not a z/Z;
        if (Character.toUpperCase(message.charAt(messageLength - 1)) == 'Z') return false;

        String encodedMessage = encode(message);

        Log.d("SpecialSMS", "Encoded Message" + encodedMessage);
        return encodedMessage.contains("43110");
    }
}
