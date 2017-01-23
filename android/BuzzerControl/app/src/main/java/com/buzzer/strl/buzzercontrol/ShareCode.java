package com.buzzer.strl.buzzercontrol;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;

import java.io.IOException;


public class ShareCode extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = ShareCode.class.getSimpleName();
    private static final String SCAN_ACTION = "com.google.zxing.client.android.SCAN";
    private boolean hasSurface;
    private boolean returnResult;
    private SurfaceHolder holderWithCallback;
    private Camera camera;
    private DecodeRunnable decodeRunnable;
    private Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        returnResult = intent != null && SCAN_ACTION.equals(intent.getAction());
        if(returnResult){
            Log.d(TAG, "MustResult" );
        }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_share_code);

    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder?");
        }
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            holderWithCallback = surfaceHolder;
        }
    }

    @Override
    public synchronized void onPause() {
        result = null;
        if (decodeRunnable != null) {
            decodeRunnable.stop();
            decodeRunnable = null;
        }
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if (holderWithCallback != null) {
            holderWithCallback.removeCallback(this);
            holderWithCallback = null;
        }
        super.onPause();
    }

    @Override
    public synchronized void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "Surface created");
        holderWithCallback = null;
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public synchronized void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public synchronized void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Surface destroyed");
        holderWithCallback = null;
        hasSurface = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (result != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    handleResult(result);
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    reset();

                    if (returnResult) {
                        Intent scanResult = new Intent("com.google.zxing.client.android.SCAN");
                        setResult(RESULT_CANCELED, scanResult);
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initCamera(SurfaceHolder holder) {
        if (camera != null) {
            throw new IllegalStateException("Camera not null on initialization");
        }
        camera = Camera.open();
        if (camera == null) {
            throw new IllegalStateException("Camera is null");
        }

        CameraConfigurationManager.configure(camera);

        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Cannot start preview", e);
        }

        decodeRunnable = new DecodeRunnable(this, camera);
        new Thread(decodeRunnable).start();
        reset();
    }

    void setResult(Result result) {
        if (returnResult) {
            Intent scanResult = new Intent("com.google.zxing.client.android.SCAN");
            scanResult.putExtra("SCAN_RESULT", result.getText());
            setResult(RESULT_OK, scanResult);
            finish();
        } else {
            TextView statusView = (TextView) findViewById(R.id.status_view);
            String text = result.getText();
            statusView.setText(text);
            statusView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Math.max(14, 56 - text.length() / 4));
            statusView.setVisibility(View.VISIBLE);
            this.result = result;
        }
    }

    private void handleResult(Result result) {
        ParsedResult parsed = ResultParser.parseResult(result);
        Intent intent;
        if (parsed.getType() == ParsedResultType.URI) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(((URIParsedResult) parsed).getURI()));
        } else {
            intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra("query", ((TextParsedResult) parsed).getText());
        }
        startActivity(intent);
    }

    private synchronized void reset() {
        TextView statusView = (TextView) findViewById(R.id.status_view);
        statusView.setVisibility(View.GONE);
        result = null;
        decodeRunnable.startScanning();
    }

}
