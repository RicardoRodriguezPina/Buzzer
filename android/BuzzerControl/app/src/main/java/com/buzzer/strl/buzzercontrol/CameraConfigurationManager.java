package com.buzzer.strl.buzzercontrol;

import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.google.zxing.client.android.camera.CameraConfigurationUtils;

/**
 * @author Sean Owen
 */
final class CameraConfigurationManager {

    private static final String TAG = "CameraConfiguration";
    static final int ZOOM = 1;
    private CameraConfigurationManager() {
    }

    static void configure(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(1280, 720);
        //parameters.setPreviewSize(1920, 1080);
        camera.setDisplayOrientation(90);

        configureAdvanced(parameters);
        camera.setParameters(parameters);
        //logAllParameters(parameters);
    }

    private static void configureAdvanced(Camera.Parameters parameters) {
           CameraConfigurationUtils.setVideoStabilization(parameters);
            CameraConfigurationUtils.setFocusArea(parameters);
            CameraConfigurationUtils.setMetering(parameters);

        CameraConfigurationUtils.setBestPreviewFPS(parameters);
        CameraConfigurationUtils.setZoom(parameters, ZOOM);
    }

    private static void logAllParameters(Camera.Parameters parameters) {
        if (Log.isLoggable(TAG, Log.INFO)) {
            for (String line : CameraConfigurationUtils.collectStats(parameters).split("\n")) {
                Log.i(TAG, line);
            }
        }
    }

}