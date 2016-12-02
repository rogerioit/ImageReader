package com.rion.imagereader.di.module;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

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
    private final CameraSource mCameraSource;
    private final GraphicOverlay<OcrGraphic> mGraphicOverlay;

    public GoogleVisionModule(Activity activity, CameraSource mCameraSource, GraphicOverlay<OcrGraphic> mGraphicOverlay) {
        this.activity = activity;
        this.textRecognizer = new TextRecognizer.Builder(activity).build();
        this.mCameraSource = mCameraSource;
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
                .setRequestedFps(15.0f)
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

    @Provides
    public ScaleGestureDetector provideScaleDetector() {
        return new ScaleGestureDetector(activity, new ScaleListener());
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

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }
}
