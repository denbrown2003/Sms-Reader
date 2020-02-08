package com.example.myapplication;

import android.util.Log;

import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpResponse;
import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;

public class HttpRequests {

    public void SendPost() {

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
}
