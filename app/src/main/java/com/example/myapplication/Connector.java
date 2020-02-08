package com.example.myapplication;
import android.util.Log;
import android.widget.TextView;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.lang.Object;
import org.json.*;





public class Connector {

    public String user;
    public String host;
    public TextView main_status;
    public String server_status;
    private Thread checker;
    public Object[] sms_array = {};
    Socket s;

    public Connector(String user_name, String host_addr){
        user = user_name;
        host = host_addr;
    }

     void SendSocket(final String text, final int port){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try{

                    s = new Socket(host , port);
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    String message = "us:"+user+";"+text+";end#";
                    dos.writeUTF(message);

                    try {
                        InputStream stream1 = s.getInputStream();
                        byte[] data = new byte[100];
                        int count = stream1.read(data);
                        String response = new String(data).substring(0, count);

                        String echo = get_command(text, response);

                        if(echo.length()> 0)
                            dos.writeUTF(echo);

                    }
                    catch (StringIndexOutOfBoundsException e){
                        System.out.println("Server didn't send a response");
                    }

                    dos.close();
                    s.close();

                }catch (IOException e){
                    e.printStackTrace();
                    Log.e("Connection Error", "Server has not responding");
                    System.out.println(e.toString());
                }

            }};


        Thread thread = new Thread(runnable);
        thread.start();}

     String get_command(final String text, final String response){
        String out = "";
        if(text == "check"){
            try {
                JSONObject json = new JSONObject(response);
                main_status.setText(json.get("status").toString());
                String cmd = json.get("cmd").toString();
                System.out.println(cmd);

                switch (cmd) {
                    case "getall":

                        String sms = "";
                        for(int i=0; i < sms_array.length; i++)
                            sms += sms_array[i].toString();

                        out = sms;

                        break;
                    case "green":


                        break;
                    default:
                        System.out.println("Color not found");
                }

            }
            catch (JSONException e){
                System.out.println(e.toString());
            }
        }
        return out;
    }


     void StartConnector() {
        System.out.println("Button start");
        main_status.setText("Running");
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

     void StopConnector(){
        SendSocket("stop", 4443);
        checker.interrupt();
        main_status.setText("Stopped");
    }

     void add_sms(final String sms){
        Object[] objArr = add(sms_array, sms);
        sms_array = objArr;
    }

    private static Object[] add(Object[] arr, Object... elements){
        Object[] tempArr = new Object[arr.length+elements.length];
        System.arraycopy(arr, 0, tempArr, 0, arr.length);

        for(int i=0; i < elements.length; i++)
            tempArr[arr.length+i] = elements[i];
        return tempArr;

    }

}
