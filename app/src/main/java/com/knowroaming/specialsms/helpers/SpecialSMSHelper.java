package com.knowroaming.specialsms.helpers;

import android.util.Log;

/**
 * Created by Neal on 3/24/2015.
 */
public class SpecialSMSHelper {

    private static final int MAX_MESSAGE_SIZE = 160 * 2;

    //Checks to see if Message is Special
    public static boolean isSpecial(String message) {
        int messageLength = message.length();

        //Ensures last letter is not a z/Z;
        if (Character.toUpperCase(message.charAt(messageLength - 1)) == 'Z') return false;

        StringBuilder sb = new StringBuilder(MAX_MESSAGE_SIZE);

        for (int i = 0; i < messageLength; i++) {
            char c = Character.toUpperCase(message.charAt(i));

            if (c >= 'A' && c <= 'Z') {
                int val = (int) c - 'A' + 1; //Encodes A/a-Z/z -> 1 -> 26
                String valString = String.valueOf(val);
                sb.append(valString);
            }
        }

        Log.d("SpecialSMS", "Encoded Message" + sb.toString());
        return sb.toString().contains("43110");
    }
}
