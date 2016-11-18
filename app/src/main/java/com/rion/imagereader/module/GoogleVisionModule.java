package com.rion.imagereader.module;

import android.content.Context;

import com.google.android.gms.vision.text.TextRecognizer;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * Criado por rogerio.junior em 18/11/2016.
 */
@Module
public class GoogleVisionModule {

    @Inject protected Context context;

    @Provides
    public TextRecognizer providesTextRecognizer() {
        return new TextRecognizer.Builder(context).build();
    }
}
