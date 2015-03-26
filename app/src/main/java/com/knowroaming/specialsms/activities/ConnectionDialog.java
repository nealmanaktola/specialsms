package com.knowroaming.specialsms.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.knowroaming.specialsms.R;
import com.knowroaming.specialsms.helpers.SpecialSMSHelper;
import com.knowroaming.specialsms.interfaces.ConnectionDialogListener;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.net.ssl.SSLSocket;


public class ConnectionDialog extends DialogFragment {
    private EditText etAddress;
    private EditText etPort;
    private Button btnConnect;
    private ProgressBar progressBar;
    private char[] password;
    private SSLSocket sslSocket = null;

    public ConnectionDialog() {
        // Empty constructor required for DialogFragment
    }

    public static ConnectionDialog newInstance() {
        ConnectionDialog frag = new ConnectionDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    private void setupViews(View view)
    {
        etAddress = (EditText) view.findViewById(R.id.etAddress);
        etPort = (EditText) view.findViewById(R.id.etPort);
        btnConnect = (Button) view.findViewById(R.id.btnConnect);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(false);
        View view = inflater.inflate(R.layout.fragment_connection_settings, container);

        setupViews(view);

        password = getString(R.string.password).toCharArray();

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = etAddress.getText().toString();
                String portNumber = etPort.getText().toString();

                //Before connecting, ensure input are valid
                if (SpecialSMSHelper.isValidSettings(ipAddress, portNumber)) {
                    new SSLConnectAsyncTask().execute(ipAddress, portNumber);

                } else {
                    Toast.makeText(getActivity(), "Invalid hostname/port. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        etAddress.requestFocus();

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

    //AsyncTask to connect with SSL
    private class SSLConnectAsyncTask extends AsyncTask<String, Void, SSLSocket> {
        ObjectOutputStream out;
        protected void onPreExecute() {

            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        protected SSLSocket doInBackground(String... strings) {
            // Some long-running task like downloading an image.
            KeyStore ks = null;
            String ipAddress = strings[0];
            SSLSocket socket = null;

            int portNum = Integer.parseInt(strings[1]);
            try {
                //Client Key "sms_app.bks"
                InputStream clientKey = getResources().openRawResource(R.raw.sms_app);
                ks = KeyStore.getInstance("BKS");
                ks.load(clientKey, password);

                //Allowing al hostname verification for simplicity.
                SSLSocketFactory socketFactory = new SSLSocketFactory(ks);
                socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                socket = (SSLSocket) socketFactory.createSocket(new Socket(strings[0],Integer.parseInt(strings[1])), strings[0], Integer.parseInt(strings[1]), false);

                //Client Encryption, cipher selected: SSL_RSA_WITH_RC4_128_MD5
                String pickedCipher[] ={"SSL_RSA_WITH_RC4_128_MD5"};
                socket.setEnabledCipherSuites(pickedCipher);

                socket.startHandshake();

                out =  new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return socket;
        }

        protected void onPostExecute(SSLSocket result) {
            // This method is executed in the UIThread
            // with access to the result of the long running task
            sslSocket = result;
            // Hide the progress bar
            progressBar.setVisibility(ProgressBar.INVISIBLE);

            //Inform MainActivity that connection is complete and can begin listening for SpecialSMS
            if (sslSocket != null && sslSocket.isConnected()) {
                ConnectionDialogListener listener = (ConnectionDialogListener) getActivity();
                listener.onConnectionComplete(out);
                getDialog().dismiss();

            }

        }
    }


}
