package com.buzzer.strl.buzzercontrol;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.transition.Visibility;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.cryptonode.jncryptor.AES256JNCryptor;
import org.cryptonode.jncryptor.CryptorException;
import org.cryptonode.jncryptor.JNCryptor;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    Context mySelft;
    AudioTrack m_track1;
    int SAMPLE_RATE1 = 44100;
    int buffersize1=1024;
    BeepManager bpm;
    String code="None";
    String uid="None";
    String password="None";
    LinearLayout line1 ;
    LinearLayout line2 ;
    LinearLayout line3 ;
    LinearLayout line4 ;
    final int SAMPLE_RATE = 44100; // The sampling rate
    boolean mShouldContinue_record=false; // Indicates if recording / playback should stop
    boolean mShouldContinue_play = false;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    final int RC_CAMERA_AND_WIFI = 666;
    private View mLayout;
    void updateVals()
    {
        SharedPreferences pref = this.getSharedPreferences("BUZZER_PREF", Context.MODE_PRIVATE);


        code=pref.getString("CODE","None");
        uid=pref.getString("UUID", "None");
        password=pref.getString("PASS", "None");
        Log.d("Value1",code);
        Log.d("Value2",uid);
        Log.d("Value3",password);
        this.runOnUiThread(new Runnable() {
            public void run() {
                try{
                    if(code.equalsIgnoreCase("None"))
                    {
                        line1.setVisibility(View.GONE);
                        line2.setVisibility(View.GONE);
                        line3.setVisibility(View.VISIBLE);
                        line4.setVisibility(View.GONE);
                    }else
                    {
                        line1.setVisibility(View.VISIBLE);
                        line2.setVisibility(View.VISIBLE);
                        line3.setVisibility(View.GONE);
                        line4.setVisibility(View.GONE);
                    }

                }
                catch(Exception e){}
            }
        });

        Intent myIntent = new Intent(this, BuzzerServer.class);
        this.stopService(myIntent);
        this.startService(myIntent);

    }
    void showHide()
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                try{
                    line1.setVisibility(View.GONE);
                    line2.setVisibility(View.GONE);
                    line4.setVisibility(View.VISIBLE);
                    line3.setVisibility(View.GONE);
                }
                catch(Exception e){}
            }
        });

    }
    void showErrorMsg(String msg)
    {
        final String msg2=msg;
        this.runOnUiThread(new Runnable() {
            public void run() {
                try{
                    Toast.makeText(mySelft, msg2, Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){}
            }
        });
    }
    private void startScanner()
    {
        Intent intent = new Intent(mySelft, ShareCode.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        startActivityForResult(intent, 3210);
    }
    private void startWizzard()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                showHide();

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"BUZZERAPP\"";
                conf.wepKeys[0] = "\"2work4fun!\"";
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                WifiManager wifiManager = (WifiManager)mySelft.getSystemService(Context.WIFI_SERVICE);
                wifiManager.addNetwork(conf);
                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for( WifiConfiguration i : list ) {
                    if(i.SSID != null && i.SSID.equals( "\"BUZZERAPP\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);
                        wifiManager.reconnect();

                        break;
                    }
                }
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Log.d("Connection", "Start Con !");
                ConnectivityManager cm = (ConnectivityManager) mySelft.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    try {
                        URL url = new URL("http://10.0.0.1/");   // Change to "http://google.com" for www  test.
                        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                        urlc.setConnectTimeout(5 * 1000);          // 10 s.
                        urlc.connect();
                        if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                            Log.d("Connection", "Success !");
                            Intent intent = new Intent(mySelft, BuzzerWizzard.class);

                            startActivity(intent);
                        } else {
                            showErrorMsg("Please Connect to BUZZR SSID");
                            updateVals();
                        }
                    } catch (MalformedURLException e1) {
                        showErrorMsg("Please Connect to BUZZR SSID");
                        updateVals();
                    } catch (IOException e) {
                        showErrorMsg("Please Connect to BUZZR SSID");
                        updateVals();
                    }
                }else
                {
                    showErrorMsg("Please Connect to BUZZR SSID");
                    updateVals();
                }


            }
        }).start();


    }
    public void startQRCode()
    {
        Intent intent = new Intent(mySelft, QrCodeActivity.class);
        startActivity(intent);
    }
    void  playDor()
    {
        for(int i=1; i<10;i++) {
            try {
                bpm.playBeepSoundAndVibrate();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpm= new BeepManager(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mySelft =this;
        StrictMode.setThreadPolicy(policy);
        mLayout = findViewById(R.id.mainid);

        FloatingActionButton new_home = (FloatingActionButton) findViewById(R.id.new_home);
        new_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWizzard();

            }
        });
        FloatingActionButton share_home = (FloatingActionButton) findViewById(R.id.share_home);
        share_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRCode();
            }
        });
        FloatingActionButton add_friend = (FloatingActionButton) findViewById(R.id.add_friend);
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScanner();

            }
        });
        TextView tv = (TextView) findViewById(R.id.sample_text);
        line1 = (LinearLayout)findViewById(R.id.line1);
        line2 = (LinearLayout)findViewById(R.id.line2);
        line3 = (LinearLayout)findViewById(R.id.line3);
        line4 = (LinearLayout)findViewById(R.id.line4);


        if(!isMyServiceRunning(BuzzerServer.class))
        {
            Intent myIntent = new Intent(this, BuzzerServer.class);
            this.startService(myIntent);
        }
        updateVals();
        ImageButton btnPlay = (ImageButton)findViewById(R.id.btnPlay);
        btnPlay.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("Buzzr"," is press");
                    view.getBackground().setColorFilter(0xe08C8C8C, PorterDuff.Mode.SRC_ATOP);
                    view.invalidate();

                    mShouldContinue_record=true;
                    String message  = encryptIt("Event:SPEAKER:PLAY:"+uid);
                    EventBus.getDefault().post(new ToServerEvent(message));
                    recordAudio();
                    return true;
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Log.d("Buzzr"," is not press");
                    view.getBackground().clearColorFilter();
                    view.invalidate();
                    String message  = encryptIt("Event:SPEAKER:STOP:NONE");
                    EventBus.getDefault().post(new ToServerEvent(message));
                    mShouldContinue_record=false;
                    return true;
                }
                return false;
            }
        });
        ImageButton opendoor = (ImageButton)findViewById(R.id.opendoor);

        opendoor.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d("Buzzr"," opendoor is press");
                    view.getBackground().setColorFilter(0xe08C8C8C, PorterDuff.Mode.SRC_ATOP);
                    view.invalidate();

                    String message  = encryptIt("Event:DOOR:OPEN");
                    EventBus.getDefault().post(new ToServerEvent(message));
                    return true;
                } else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Log.d("Buzzr"," opendoor is not press");
                    view.getBackground().clearColorFilter();
                    view.invalidate();
                    String message  = encryptIt("Event:DOOR:CLOSE");
                    EventBus.getDefault().post(new ToServerEvent(message));

                    return true;
                }
                return false;
            }
        });

            methodRequiresTwoPermission();


    }

    private String encryptIt(String enc)
    {
        JNCryptor cryptor = new AES256JNCryptor();



        byte[] bytedata = enc.getBytes();
        String message="";
        try {
            byte[] encryptData = cryptor.encryptData(bytedata,password.toCharArray());
            message =  Base64.encodeToString(encryptData,Base64.DEFAULT) ;
        } catch (CryptorException e) {
            e.printStackTrace();
        }
        return message;
    }
    private void methodRequiresTwoPermission() {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.RECORD_AUDIO};
            if (EasyPermissions.hasPermissions(this, perms)) {

            } else {

                EasyPermissions.requestPermissions(this, getString(R.string.camera_and_wifi_rationale),
                        RC_CAMERA_AND_WIFI, perms);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        }catch (EventBusException e){}
    }
    @Override
    public void onStop() {
        //EventBus.getDefault().unregister(this);
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ToClientEvent event) {
        //Toast.makeText(mySelft, event.message, Toast.LENGTH_SHORT).show();
        String msg = event.message;
        Log.d("Message",msg);
        Log.d("Value", Integer.toString(msg.indexOf("INT:")) );
        if(msg.indexOf("INT:")==0)
        {
            updateVals();
        }
        if(msg.indexOf("EXT:")==0)
        {
            try {
                String[] decr = decrypTed(msg.replace("EXT:", "")).split(":");
                if(decr[0]=="RING")
                {
                    playDor();
                }
                if(decr[0]=="PLAY")
                {
                    if(decr[1]=="START")
                    {
                        playAudio();
                    }
                    if(decr[1]=="STOP")
                    {
                        wsound.disconnect();
                        m_track1.stop();
                        m_track1.flush();
                        m_track1=null;
                    }
                }
            }catch (Exception e)
            {}
        }

       //
    }

    private String decrypTed(String enc) {
        String message="";
        JNCryptor cryptor = new AES256JNCryptor();
        byte[] decoded = Base64.decode(enc, Base64.DEFAULT);

        try {
            byte[] decryptData = cryptor.decryptData(decoded, password.toCharArray());
            message= new String(decryptData);


        } catch (CryptorException e) {
            e.printStackTrace();
        }
        return  message;
    }

    WebSocket wsound;
    void playAudio() {

        m_track1 = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLE_RATE1,
                AudioFormat.CHANNEL_OUT_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT,
                buffersize1 * 2,
                AudioTrack.MODE_STREAM);
        m_track1.setPlaybackRate(SAMPLE_RATE1);

        m_track1.play();

        try {
            wsound = new WebSocketFactory().createSocket("ws://104.198.10.152/stream/"+code+"/inof");
            wsound.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {

                    byte[] audio =  Base64.decode(message, Base64.DEFAULT);
                    Log.d("Buzzer","Data: " + audio.length);
                    m_track1.write(audio, 0, audio.length);

                }
                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception
                {

                }
                @Override
                public void onDisconnected(WebSocket websocket,
                                           WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                           boolean closedByServer) throws Exception
                {

                }
            });
            wsound.connect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {

        }
    }
    void recordAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                byte[] audioBuffer = new byte[bufferSize / 2];

                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e("Buzzr", "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();

                Log.v("Buzzr", "Start recording");

                long shortsRead = 0;
                WebSocket ws=null;
                try {
                    ws = new WebSocketFactory().createSocket("ws://104.198.10.152/stream/"+code+"/io");
                    ws.connect();


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                }

                while (mShouldContinue_record) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;
                    String dd=  Base64.encodeToString(audioBuffer,Base64.DEFAULT) ;
                    Log.d("Buzzr", dd);

                    ws.sendText(dd);
                }
                ws.disconnect();

                record.stop();
                record.release();

                Log.v("Buzzr", String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 3210) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("SCAN_RESULT");
                Log.d("Buzzer",result);
                String [] fromd=result.split("|");
                String code=fromd[0];
                String uid=fromd[1];
                String buzzpass=fromd[2];
                SharedPreferences pref = getSharedPreferences("BUZZER_PREF", Context.MODE_PRIVATE);
                pref.edit().putString("CODE", code).commit();
                pref.edit().putString("UUID", uid).commit();
                pref.edit().putString("PASS", buzzpass).commit();
                updateVals();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


}
