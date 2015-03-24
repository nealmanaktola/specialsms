package com.knowroaming.specialsms.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.knowroaming.specialsms.R;
import com.knowroaming.specialsms.receivers.SmsReceiver;


public class MainActivity extends ActionBarActivity {

    private BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(SmsReceiver.ACTION);

        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sender = intent.getStringExtra("sender");
                String message = intent.getStringExtra("message");

                Toast.makeText(MainActivity.this, sender + "\n" + message, Toast.LENGTH_SHORT).show();
            }
        };

        this.registerReceiver(smsReceiver,filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(smsReceiver);
    }
}
