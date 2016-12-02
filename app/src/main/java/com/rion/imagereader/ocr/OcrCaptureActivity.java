/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rion.imagereader.ocr;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.rion.imagereader.R;
import com.rion.imagereader.di.component.DaggerOcrComponent;
import com.rion.imagereader.di.component.OcrComponent;
import com.rion.imagereader.di.module.GoogleVisionModule;
import com.rion.imagereader.ocr.camera.CameraSource;
import com.rion.imagereader.ocr.camera.CameraSourcePreview;
import com.rion.imagereader.ocr.camera.GraphicOverlay;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Activity for the multi-tracker app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrCaptureActivity extends AppCompatActivity {
    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    @BindView(R.id.preview) CameraSourcePreview mPreview;
    @BindView(R.id.graphicOverlay) GraphicOverlay<OcrGraphic> mGraphicOverlay;

    @Inject TextRecognizer mTextRecognizer;
    @Inject CameraSource mCameraSource;
	@Inject Detector.Processor<TextBlock> mTextBlockProcessor;

	// Helper objects for detecting taps and pinches.
	@Inject GestureDetector gestureDetector;
	@Inject ScaleGestureDetector scaleGestureDetector;



    /**
     * Initializes the UI and creates the detector pipeline.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_capture);

        ButterKnife.bind(this);

        OcrComponent ocrComponent = DaggerOcrComponent.builder()
                .googleVisionModule(new GoogleVisionModule(this, mCameraSource, mGraphicOverlay))
                .build();

        ocrComponent.inject(this);

		if(EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
			prepareOcr();
		} else {
			EasyPermissions.requestPermissions(this, getString(R.string.rationale_camera), RC_HANDLE_CAMERA_PERM, Manifest.permission.CAMERA);
		}

        Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom", Snackbar.LENGTH_LONG).show();
    }

	@AfterPermissionGranted(RC_HANDLE_CAMERA_PERM)
	private void prepareOcr() {

		// A text recognizer is created to find text.  An associated processor instance
		// is set to receive the text recognition results and display graphics for each text block
		// on screen.
		mTextRecognizer.setProcessor(mTextBlockProcessor);

		if (!mTextRecognizer.isOperational()) {
			// Note: The first time that an app using a Vision API is installed on a
			// device, GMS will download a native libraries to the device in order to do detection.
			// Usually this completes before the app is run for the first time.  But if that
			// download has not yet completed, then the above call will not detect any text,
			// barcodes, or faces.
			//
			// isOperational() can be used to check if the required native libraries are currently
			// available.  The detectors will automatically become operational once the library
			// downloads complete on device.
			Log.w(TAG, "Detector dependencies are not yet available.");

			// Check for low storage.  If there is low storage, the native library will not be
			// downloaded, so detection will not become operational.
			IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
			boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

			if (hasLowStorage) {
				Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
				Log.w(TAG, getString(R.string.low_storage_error));
			}
		}
	}

	private void startOcr() {

		// Check that the device has play services available.
		int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
				getApplicationContext());
		if (code != ConnectionResult.SUCCESS) {
			Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
			dlg.show();
		}

		if(mCameraSource != null && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
			try {
				mPreview.start(mCameraSource, mGraphicOverlay);

			} catch (IOException e) {
				Log.e(TAG, "Unable to start camera source.", e);
				mCameraSource.release();
				mCameraSource = null;
			}
		}
	}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || super.onTouchEvent(e);
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startOcr();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }
}