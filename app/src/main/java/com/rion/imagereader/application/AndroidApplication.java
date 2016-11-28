package com.rion.imagereader.application;

import android.app.Application;

import com.rion.imagereader.MainActivity;
import com.rion.imagereader.module.AndroidModule;
import com.rion.imagereader.module.GoogleVisionCameraSourceModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Criado por rogerio.junior em 18/11/2016.
 */

public class AndroidApplication extends Application {

    @Singleton
    @Component(modules = {
            AndroidModule.class,
            GoogleVisionCameraSourceModule.class
    })
    public interface ApplicationComponent {
        void inject(AndroidApplication androidApplication);
        void inject(MainActivity mainActivity);
    }

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAndroidApplication_ApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .build();

        component.inject(this);
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
