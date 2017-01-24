package com.buzzer.strl.buzzercontrol;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;


public class BuzzerServer extends Service {

    private static final String TAG = "BuzzerServer";

    private boolean isRunning  = false;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        EventBus.getDefault().register(this);
        isRunning = true;
    }
    WebSocket ws;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();

            }
        }).start();

        return Service.START_STICKY;
    }
    public void connect()
    {
        SharedPreferences pref = this.getSharedPreferences("BUZZER_PREF", Context.MODE_PRIVATE);
        String code="None";
        code=pref.getString("CODE","None");
        Log.d("BUZZER_PREF_WEBSOCKET",code);
        if(!code.equals("None")) {
            try {
                Log.d("BUZZER_EBSOCKET","Starting");
                ws = new WebSocketFactory().createSocket("ws://104.198.10.152/client/"+code);
                ws.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) throws Exception {
                        Log.i(TAG, message);

                        EventBus.getDefault().post(new ToClientEvent("EXT:"+message));
//                            Thread.sleep(80000);
//                            ws.sendText(message);
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket,
                                               WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                               boolean closedByServer) throws Exception {
                        connect();
                    }
                });
                ws.connect();
                Log.d("BUZZER_EBSOCKET","Waiting");
                ws.sendText("Hola Peyote");

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("BUZZER_EBSOCKET",e.toString());
            } catch (WebSocketException e) {
                try {
                    Thread.sleep(600000);
                    connect();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ToServerEvent event) {
        Log.d("Buzzr_Toserver",event.message);
        if(ws.isOpen())
        {
            Log.d("Buzzr_Websocket","WS is Open");
        }else
        {
            Log.d("Buzzr_Websocket","WS is not Open");
            connect();

        }
        ws.sendText(event.message);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }
}