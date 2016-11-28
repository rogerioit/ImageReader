package com.rion.imagereader;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;
import com.rion.imagereader.application.AndroidApplication;
import com.rion.imagereader.di.component.OcrComponent;
import com.rion.imagereader.di.module.GoogleVisionModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 1001;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Inject
    TextRecognizer textRecognizer;

    @Inject
    CameraSource cameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        ((AndroidApplication) getApplication()).getComponent().inject(this);

        OcrComponent ocrComponent = DaggerOcrComponent.builder()
                .googleVisionModule(new GoogleVisionModule(this))
                .build();

        ocrComponent.inject(this);

        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.fab)
    public void buttonClickCamera() {

        Snackbar.make(null, textRecognizer.isOperational() ? "Operacional" : "Não operacional", Snackbar.LENGTH_LONG).show();

        if(EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            callOcr();
        } else {
            //todo implement callback request return!
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), REQUEST_CODE_PERMISSION_CAMERA, Manifest.permission.CAMERA);
        }
    }

    private void callOcr() {

        try {
            if(cameraSource != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                cameraSource.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
