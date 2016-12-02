package com.rion.imagereader.di.component;

import com.rion.imagereader.di.module.GoogleVisionModule;
import com.rion.imagereader.ocr.OcrCaptureActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Criado por rogerio.junior em 18/11/2016.
 */
@Singleton
@Component(modules = {
        GoogleVisionModule.class
})
public interface OcrComponent {
    void inject(OcrCaptureActivity ocrCaptureActivity);
}
