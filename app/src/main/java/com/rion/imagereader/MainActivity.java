package com.rion.imagereader;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextRecognizer;
import com.rion.imagereader.di.component.DaggerOcrComponent;
import com.rion.imagereader.di.component.OcrComponent;
import com.rion.imagereader.di.module.GoogleVisionModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_CODE_PERMISSION_CAMERA = 1001;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton floatingActionButton;

    @Inject
    TextRecognizer textRecognizer;

    @Inject
    CameraSource cameraSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        OcrComponent ocrComponent = DaggerOcrComponent.builder()
                .googleVisionModule(new GoogleVisionModule(this))
                .build();

        ocrComponent.inject(this);

        setSupportActionBar(toolbar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @OnClick(R.id.fab)
    public void buttonClickCamera() {

        Snackbar.make(floatingActionButton, textRecognizer.isOperational() ? "Operacional" : "Não operacional", Snackbar.LENGTH_LONG).show();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Snackbar.make(floatingActionButton, getString(R.string.low_storage_error), 10000).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        if(EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            callOcr();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), REQUEST_CODE_PERMISSION_CAMERA, Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSION_CAMERA)
    private void callOcr() {

        try {
            //todo set Processor
            //@see <https://codelabs.developers.google.com/codelabs/mobile-vision-ocr/index.html?index=..%2F..%2Findex#5>
            if(cameraSource != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                cameraSource.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
