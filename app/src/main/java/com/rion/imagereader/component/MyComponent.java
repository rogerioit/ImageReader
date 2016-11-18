package com.rion.imagereader.component;

import com.google.android.gms.vision.text.TextRecognizer;
import com.rion.imagereader.MainActivity;
import com.rion.imagereader.module.AndroidModule;
import com.rion.imagereader.module.GoogleVisionModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Criado por rogerio.junior em 18/11/2016.
 */

@Singleton
@Component(modules = {
        AndroidModule.class,
        GoogleVisionModule.class
})
public interface MyComponent {

    // provide the dependency for dependent components
    // (not needed for single-component setups)
    TextRecognizer provideTextRecognizer();

    // allow to inject into our Main class
    // method name not important
    void inject(MainActivity mainActivity);
}
