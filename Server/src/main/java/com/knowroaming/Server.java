package com.knowroaming;

//Server to Respond to Requests
import com.knowroaming.specialsms.models.SpecialMessage;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
    private static final String SERVER_KEY = "sms_server.jks";
    private static final String SERVER_KEY_PASSWD = "knowroaming";

    //Sender, SpecialMessage map
    private static HashMap<String, ArrayList<SpecialMessage>> senderMessageMap;

    public static void main(String[] args) {
        int portNum;
        SSLServerSocket serverSocket = null;

        //Checking command line arguments for port, otherwise use default of 4444
        try {
            portNum = Integer.parseInt(args[0]);
        } catch (Exception e) {
            portNum = 4444;
            System.out.println("Invalid Port Number, using 4444");
        }

        try {
            char [] password = SERVER_KEY_PASSWD.toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(SERVER_KEY), password);


            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            //Specify SSL Params
            SSLContext ctx = SSLContext.getInstance("SSL");
            ctx.init(kmf.getKeyManagers(), null, null);

            ServerSocketFactory ssf = ctx.getServerSocketFactory();

            serverSocket = (SSLServerSocket) ssf.createServerSocket(portNum);

            //Client will use SSL_RSA_WITH_RC4_128_MD5
            String[] enCiphersuite= serverSocket.getEnabledCipherSuites();
            //System.out.println("Enabled ciphersuites are: "+ Arrays.toString(enCiphersuite));

            senderMessageMap = new HashMap<String, ArrayList<SpecialMessage>>();
            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("SpecialSMS Client Connected");
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                SpecialMessage smsObject = null;
                try {
                    while ((smsObject = (SpecialMessage) in.readObject()) != null) {
                        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                        smsObject.setTimestamp(timeStamp);

                        String sender = smsObject.getSender();
                        String encodedMessage = smsObject.getEncodedMessage();

                        if (!senderMessageMap.containsKey(sender)) {
                            senderMessageMap.put(sender, new ArrayList<SpecialMessage>());
                        }

                        //Add to Database
                        senderMessageMap.get(sender).add(smsObject);

                        System.out.println(smsObject.toString());

                        //Checking for Delete Message
                        if (encodedMessage.contains("431373")) {
                            senderMessageMap.clear();
                            System.out.println("Delete message detected, deleting db");
                        }
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println("Object is not of type SMS");
                    System.exit(-1);
                } catch (EOFException e) {
                    System.out.println("SpecialSMS Client Disconnected");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not listen on port" + String.valueOf(portNum));
            System.exit(-1);
        } catch (KeyStoreException e) {
            System.out.println("Could not get key store");
            System.exit(-1);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("There is no algorithm in ks.load");
            e.printStackTrace();
            System.exit(-1);
        } catch (CertificateException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (UnrecoverableKeyException e) {
            System.out.println("init - no key");
            System.exit(-1);
        } catch (KeyManagementException e) {
            System.out.println("Key Management Exception");
            System.exit(-1);
        }
    }
}
