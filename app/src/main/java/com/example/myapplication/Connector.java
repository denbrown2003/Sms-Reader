package com.example.myapplication;
import android.util.Log;
import android.view.View;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class Connector {

    public String user;
    public String host;
    public String ststus_from_server;
    private Thread checker;

    public Connector(String user_name, String host_addr){
        user = user_name;
        host = host_addr;
    }

    public void SendSocket(final String text, final int port){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{

                    Socket s = new Socket(host , port);

                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                    String message = "us:"+user+";"+text+";end#";

                    dos.writeUTF(message);

                    InputStream stream1 = s.getInputStream();
                    byte[] data = new byte[100];
                    int count = stream1.read(data);
                    String response = new String(data).substring(0, count);

                    get_command(text, response);

                    dos.close();
                    s.close();

                }catch (IOException e){
                    e.printStackTrace();
                    Log.e("error", "error");
                    System.out.println(e.toString());
                }

            }};


        Thread thread = new Thread(runnable);
        thread.start(); }

    public void get_command(final String text, final String response){

        if(text == "check"){


        }

    }

    public void StartConnector() {
        System.out.println("Button start");
        SendSocket("start", 4443);

        try { TimeUnit.SECONDS.sleep(2); }
        catch (
                InterruptedException ex) {
            Thread.currentThread().interrupt(); }

        Runnable cmd_sender = new Runnable() {
            @Override
            public void run(){
                while (true) {
                    while(!Thread.currentThread().isInterrupted()) {

                        SendSocket("check", 4443);

                        try {
                            TimeUnit.SECONDS.sleep(10);
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }};
        checker = new Thread(cmd_sender);
        checker.start();

    }

    public void StopConnector(){
        SendSocket("stop", 4443);
        checker.interrupt();
    }

}
