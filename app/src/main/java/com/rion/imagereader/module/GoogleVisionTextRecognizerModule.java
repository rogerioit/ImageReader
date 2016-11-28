package com.rion.imagereader.module;

import android.content.Context;

import com.google.android.gms.vision.text.TextRecognizer;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * Criado por rogerio.junior em 28/11/2016.
 */
@Module
public class GoogleVisionTextRecognizerModule {

    @Inject protected Context context;

    @Provides
    public TextRecognizer provideTextRecognizer() {
        return new TextRecognizer.Builder(context).build();
    }
}
