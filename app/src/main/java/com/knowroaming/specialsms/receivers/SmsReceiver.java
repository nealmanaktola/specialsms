package com.knowroaming.specialsms.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.knowroaming.specialsms.helpers.SpecialSMSHelper;

/**
 * Created by Neal on 3/20/2015.
 */
public class SmsReceiver extends BroadcastReceiver {
    //IntentFilter Action
    public static final String ACTION = "com.knowroaming.specialsms";
    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle extras = intent.getExtras();

            if (extras != null){
                Object[] pdus = (Object []) extras.get("pdus");

                for (int i = 0; i < pdus.length; i++) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte []) pdus[i]);

                    String sender = smsMessage.getOriginatingAddress();
                    String body = smsMessage.getMessageBody();
                    Log.d("SpecialSMS", "Message Received:" + body);

                    //Checks if Message is Special
                    if (SpecialSMSHelper.isSpecial(body)) {
                       Log.d("SpecialSMS", "Special SMS Intercepted!");

                       Intent in = new Intent(ACTION);
                       in.putExtra("sender", sender);
                       in.putExtra("message", body);

                       //Broadcasts message to MainActivity
                       context.sendBroadcast(in);
                    }
                }
            }
        }
    }
}