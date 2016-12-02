package com.rion.imagereader.di.module;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.rion.imagereader.ocr.OcrGraphic;
import com.rion.imagereader.ocr.OcrTextBlockDetectorProcessor;
import com.rion.imagereader.ocr.camera.CameraSource;
import com.rion.imagereader.ocr.camera.GraphicOverlay;

import dagger.Module;
import dagger.Provides;

/**
 * Criado por rogerio.junior em 28/11/2016.
 */
@Module
public class GoogleVisionModule {

	private static final String TAG = GoogleVisionModule.class.getName();

	public static final String EXTRA_OCR_DATA_RESULT = "EXTRA_OCR_DATA_RESULT";

	private final Activity activity;
    private final TextRecognizer textRecognizer;
    private final GraphicOverlay<OcrGraphic> mGraphicOverlay;

    public GoogleVisionModule(Activity activity, GraphicOverlay<OcrGraphic> mGraphicOverlay) {
        this.activity = activity;
        this.textRecognizer = new TextRecognizer.Builder(activity).build();
        this.mGraphicOverlay = mGraphicOverlay;
    }

    @Provides
    public TextRecognizer provideTextRecognizer() {
        return textRecognizer;
    }

    @Provides
    public CameraSource provideCameraSource() {
        return new CameraSource.Builder(activity, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(5.0f)
                .setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .build();
    }

    @Provides
    public Detector.Processor<TextBlock> provideTextBlockProcessor() {
        return new OcrTextBlockDetectorProcessor(mGraphicOverlay);
    }

    @Provides
    public GestureDetector provideGestureDetector() {
        return new GestureDetector(activity, new CaptureGestureListener());
    }

	/**
	 * onTap is called to capture the first TextBlock under the tap location and return it to
	 * the Initializing Activity.
	 *
	 * @param rawX - the raw position of the tap
	 * @param rawY - the raw position of the tap.
	 * @return true if the activity is ending.
	 */
	private boolean onTap(float rawX, float rawY) {
		OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
		TextBlock text = null;
		if (graphic != null) {
			text = graphic.getTextBlock();

			if (text != null && text.getValue() != null) {
				Intent data = new Intent();
				data.putExtra(EXTRA_OCR_DATA_RESULT, text.getValue());
				activity.setResult(CommonStatusCodes.SUCCESS, data);
				activity.finish();
			} else {
				Log.d(TAG, "text data is null");
			}

		} else {
			Log.d(TAG,"no text detected");
		}

		return text != null;
	}

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }
}
