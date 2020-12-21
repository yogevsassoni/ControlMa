package com.example.magshimim.controlma;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.http.Headers;
import android.net.http.RequestQueue;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// Imports the Google Cloud client library


public class MainActivity extends AppCompatActivity {
    private static final String REQUESTTAG = "String request first";
    private MediaRecorder myAudioRecorder;
    private Button sendRequest;
    private Button playButton;
    private RequestQueue mRequestQueue;
    private StringRequest stringRequest;
    private static final String TAG = "Rofl";
    private String audioPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rofl.3gp";
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 2;
    private final String ip = "192.168.43.29";
    private TextView txtResponse;
    private boolean RECORDING = false;
    String URL = "http:" + ip + ":9176";
    String ipESP = "192.168.43.217";
    String lightOnURL = "http:" + ipESP + "/on";
    String lightOffURL = "http:" + ipESP + "/off";
    File file;
    private ByteArrayOutputStream outputType;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    com.android.volley.RequestQueue queuer;
    com.android.volley.RequestQueue RequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println(audioPath);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE) //gets permission to use wifi
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_WIFI_STATE},
                        2);


            }
        } else {
            // Permission has already been granted
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) //gets permission to record audio
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);


            }
        } else {
            // Permission has already been granted

        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) //gets permission to write files
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);


            }
        } else {
            // Permission has already been granted

        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) //gets permission to activate camera
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);


            }
        } else {
            // Permission has already been granted

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playButton = (Button) findViewById(R.id.btnPlay);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //play button plays last recorded audio file

                MediaPlayer media = new MediaPlayer();
                try {
                    media.setDataSource(audioPath);
                    media.prepare();
                    media.start();
                } catch (IOException ex) {
                    Log.d("ERROR", ex.getMessage());
                }

            }
        });
        sendRequest = (Button) findViewById(R.id.btnRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //records and then sends

                playAudio();


            }
        });


    }
//sends the request to the server and gets response
    private void sendReQuestAndPrintResponse() {

        txtResponse = (TextView) findViewById(R.id.txtResponse);
        file = new File(audioPath);
        queuer = Volley.newRequestQueue(getApplicationContext());
        outputType = new ByteArrayOutputStream();

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        txtResponse.setText(response);
                        handleResponse(response); //handles response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        txtResponse.setText(error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                int size = (int) file.length();
                byte[] bytes = new byte[size];
                char[] hexdata;
                String data;
                try {
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                    buf.read(bytes, 0, bytes.length); //gets data from audio file

                    buf.close();
                    hexdata = new char[bytes.length * 2];
                    for (int j = 0; j < bytes.length; j++) { //converts bytes to hex bytes that are readable
                        int v = bytes[j] & 0xFF;
                        hexdata[j * 2] = hexArray[v >>> 4];
                        hexdata[j * 2 + 1] = hexArray[v & 0x0F];
                    }

                    data = new String(bytes);
                    params.put("name", new String(hexdata));

                } catch (IOException e) {
                    txtResponse.setText(e.getMessage());
                }


                return params;
            }
        };
        Log.i("rofl","requested");
        queuer.add(postRequest);
    }


//when you click on send request
    public void playAudio() {
        if (RECORDING) { //if it was recording
            myAudioRecorder.stop();
            try {
                sendReQuestAndPrintResponse(); //sends recording to server
                Log.d("SENDING", "VERY GOOD IT SEND");
            } catch (Exception e) {
                Log.i(TAG, "not good");
                e.printStackTrace();
            }

        } else { //if wasnt recording
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            myAudioRecorder.setOutputFile(audioPath);

            try {
                myAudioRecorder.prepare();
                myAudioRecorder.start(); //starts recording
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RECORDING = !RECORDING;
    }




        @Override
        public void onRequestPermissionsResult ( int requestCode, //checks that all permissions are granted
        String permissions[], int[] grantResults){
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {

                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    } else {

                    }
                    return;
                }


            }
        }
//opens the camera on the phone
        public void openCamera () {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        }
        public void handleResponse(String command) //handles responses from server
        {
            if(command.contains("camera")) //handles camera command
            {
                openCamera();
            }
            if(command.contains("light") && command.contains("on"))
            {
                RequestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, lightOnURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                            }

                        },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }});
                RequestQueue.add(stringRequest);
            }
            else if (command.contains("light") && command.contains("off"))
            {

                RequestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, lightOffURL,new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Log.i("rofl","its working");
                            }
                        },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("rofl","its not working");
                    }});

                RequestQueue.add(stringRequest);
                }
            }

        }