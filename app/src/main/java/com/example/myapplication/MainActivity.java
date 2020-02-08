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
    Connector conn;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText  host_edit = findViewById(R.id.editText);
        EditText  username_edit = findViewById(R.id.editText2);

        String host = host_edit.getText().toString();
        String user = username_edit.getText().toString();

        System.out.println(user);

        status = findViewById(R.id.textView2);
        conn = new Connector(user, host);
        conn.main_status = status;


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

                        conn.add_sms(text);
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
        conn.SendSocket("hello", 4444);
        Object[] sms = conn.sms_array;
        System.out.println(sms.toString());

    }

    public void StartButton(View view) {
        System.out.println("Button start");
        conn.StartConnector();


    }

    public void StopButton(View view) {
        System.out.println("Button stop");
        conn.StopConnector();

    }


}
