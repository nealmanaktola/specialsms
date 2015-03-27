package com.knowroaming.specialsms.activities;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.knowroaming.specialsms.R;
import com.knowroaming.specialsms.helpers.SpecialSMSHelper;
import com.knowroaming.specialsms.interfaces.ConnectionDialogListener;
import com.knowroaming.specialsms.models.SpecialMessage;
import com.knowroaming.specialsms.receivers.SmsReceiver;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends ActionBarActivity implements ConnectionDialogListener {
    private BroadcastReceiver smsReceiver;
    private ObjectOutputStream out;

    private View drawRed;
    private View drawGreen;

    private char[] password = null;

    private Handler indicatorDelayHandler;
    private Runnable indicatorRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Red and Green Indicators
        drawRed = findViewById(R.id.redShape);
        drawGreen = findViewById(R.id.greenShape);

        indicatorDelayHandler = new Handler();

        //Runnable timed task to ensure the Green Indicator appears for 5 seconds
        indicatorRunnable = new Runnable() {
            @Override
            public void run() {
                drawGreen.setVisibility(View.INVISIBLE);
            }
        };

        password = getString(R.string.password).toCharArray();

        //Initializes with a connection to Server Dialog
        showConnectionDialog();
    }

    private void showConnectionDialog() {
        FragmentManager fm = getFragmentManager();
        ConnectionDialog connectionDialog = ConnectionDialog.newInstance();
        connectionDialog.show(fm, "fragment_connection_settings");
    }

    @Override
    public void onConnectionComplete(ObjectOutputStream out) {
        this.out = out;

    }
    //MessageAsyncTask to send message on background thread
    private class SendMessageAsyncTask extends AsyncTask<String, Void, String> {
        Exception error = null;

        protected String doInBackground(String... strings) {

            String sender = strings[0];
            String message = strings[1];

            try {
                out.writeObject(new SpecialMessage(sender,message, SpecialSMSHelper.encode(message)));
            } catch (Exception e) {
                error = e;
            }
            return message;
        }

        protected void onPostExecute(String message) {

            if (error == null)
                Toast.makeText(getApplicationContext(), "Sent to Server",Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
                showConnectionDialog();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //IntentFilter receive SmsReceiver actions
        IntentFilter filter = new IntentFilter(SmsReceiver.ACTION);

        //on Special SMS Handler from SMSReceiver
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sender = intent.getStringExtra("sender");
                String message = intent.getStringExtra("message");

                drawGreen.setVisibility(View.VISIBLE);

                indicatorDelayHandler.removeCallbacks(indicatorRunnable);
                indicatorDelayHandler.postDelayed(indicatorRunnable, 5000);

                Toast.makeText(MainActivity.this, "Special SMS Received", Toast.LENGTH_SHORT).show();
                new SendMessageAsyncTask().execute(sender,message);
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
