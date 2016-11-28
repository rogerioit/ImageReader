package com.rion.imagereader.module;

import android.content.Context;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

import javax.inject.Inject;

import dagger.Module;

/**
 * Criado por rogerio.junior em 18/11/2016.
 */
@Module
public class GoogleVisionCameraSourceModule {

    @Inject protected Context context;

    @Inject protected TextRecognizer textRecognizer;

    public CameraSource provideCameraSource(boolean autoFocus) {
        return new CameraSource.Builder(context, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(autoFocus)
                .build();
    }
}
