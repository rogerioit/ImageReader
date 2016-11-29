package com.rion.imagereader.di.module;

import android.content.Context;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;

import dagger.Module;
import dagger.Provides;

/**
 * Criado por rogerio.junior em 28/11/2016.
 */
@Module
public class GoogleVisionModule {

    private final Context context;
    private final TextRecognizer textRecognizer;

    public GoogleVisionModule(Context context) {
        this.context = context;
        this.textRecognizer = new TextRecognizer.Builder(context).build();
    }

    @Provides
    public TextRecognizer provideTextRecognizer() {
        return textRecognizer;
    }

    @Provides
    public CameraSource provideCameraSource() {
        return new CameraSource.Builder(context, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(15.0f)
                .setAutoFocusEnabled(true)
                .build();
    }
}
