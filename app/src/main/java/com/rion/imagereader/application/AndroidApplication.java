package com.rion.imagereader.application;

import android.app.Application;

import com.rion.imagereader.di.module.AndroidModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Criado por rogerio.junior em 18/11/2016.
 */

public class AndroidApplication extends Application {

    @Singleton
    @Component(modules = {
            AndroidModule.class
    })
    public interface ApplicationComponent {
        void inject(AndroidApplication androidApplication);
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
