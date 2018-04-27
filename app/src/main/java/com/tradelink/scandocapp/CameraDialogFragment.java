package com.tradelink.scandocapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tradelink.scandocapp.utils.ImageContainer;

import net.doo.snap.camera.AutoSnappingController;
import net.doo.snap.camera.CameraOpenCallback;
import net.doo.snap.camera.ContourDetectorFrameHandler;
import net.doo.snap.camera.PictureCallback;
import net.doo.snap.camera.ScanbotCameraView;
import net.doo.snap.lib.detector.ContourDetector;
import net.doo.snap.ui.PolygonView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * {@link ScanbotCameraView} integrated in {@link DialogFragment} example
 */
public class CameraDialogFragment extends DialogFragment implements PictureCallback {
    private ScanbotCameraView cameraView;
    private ImageView resultView;
    public static final String CAPTURED_IMAGE = "com.tradelink.ekyc.scanner.captured.image";

    boolean flashEnabled = false;

    /**
     * Create a new instance of CameraDialogFragment
     */
    static CameraDialogFragment newInstance() {
        return new CameraDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View baseView =  getActivity().getLayoutInflater().inflate(R.layout.scanbot_camera_view, container, false);

        cameraView = (ScanbotCameraView) baseView.findViewById(R.id.camera);

        /*cameraView.setCameraOpenCallback(new CameraOpenCallback() {
            @Override
            public void onCameraOpened() {
                cameraView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cameraView.continuousFocus();
                        cameraView.useFlash(flashEnabled);
                    }
                }, 700);
            }
        });*/
        cameraView.setCameraOpenCallback(new CameraOpenCallback() {
            @Override
            public void onCameraOpened() {
                cameraView.stopPreview();
                List<Camera.Size> supportedPictureSizes = cameraView.getSupportedPictureSizes();
                cameraView.setPictureSize(supportedPictureSizes.get(0));
                List<Camera.Size> supportedPreviewSizes = cameraView.getSupportedPreviewSizes();
                cameraView.setPreviewSize(supportedPreviewSizes.get(0));
                cameraView.startPreview();
            }
        });

        resultView = (ImageView) baseView.findViewById(R.id.result);

        ContourDetectorFrameHandler contourDetectorFrameHandler = ContourDetectorFrameHandler.attach(cameraView);

        PolygonView polygonView = (PolygonView) baseView.findViewById(R.id.polygonView);
        contourDetectorFrameHandler.addResultHandler(polygonView);

        AutoSnappingController.attach(cameraView, contourDetectorFrameHandler);

        cameraView.addPictureCallback(this);

        baseView.findViewById(R.id.snap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.takePicture(false);
            }
        });

        baseView.findViewById(R.id.flash).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                flashEnabled = !flashEnabled;
                cameraView.useFlash(flashEnabled);
            }
        });

        return baseView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            Log.d("cameraView", "width " + width + " height " + height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraView.onPause();
    }

    @Override
    public void onPictureTaken(final byte[] image, int imageOrientation) {
        // Here we get the full image from the camera.
        // Implement a suitable async(!) detection and image handling here.
        // This is just a demo showing detected image as downscaled preview image.

        // Decode Bitmap from bytes of original image:
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; // use 1 for original size (if you want no downscale)!
        // in this demo we downscale the image to 1/8 for the preview.
        Bitmap originalBitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);
        Log.d("originalImage", "width " + originalBitmap.getWidth() + " height " + originalBitmap.getHeight());

        // rotate original image if required:
        if (imageOrientation > 0) {
            final Matrix matrix = new Matrix();
            matrix.setRotate(imageOrientation, originalBitmap.getWidth() / 2f, originalBitmap.getHeight() / 2f);
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);
        }

        ImageContainer imageContainer = ImageContainer.getInstance();
        imageContainer.setOrigin(originalBitmap);

        createFileFromBitmap(originalBitmap, "original");
        // Run document detection on original image:
        final ContourDetector detector = new ContourDetector();
        detector.detect(originalBitmap);
        final Bitmap documentImage = detector.processImageAndRelease(originalBitmap, detector.getPolygonF(), ContourDetector.IMAGE_FILTER_NONE);

        imageContainer.setDocument(documentImage);
        documentImage.recycle();
//        createFileFromBitmap(documentImage, "document");
        /*resultView.post(new Runnable() {
            @Override
            public void run() {
                resultView.setImageBitmap(documentImage);
                cameraView.continuousFocus();
                cameraView.startPreview();
            }
        });*/
//        Log.d("detectedImage", "width " + documentImage.getWidth() + " height " + documentImage.getHeight());
        Intent intent = new Intent(getActivity(), EditPolygonActivity.class);
        startActivity(intent);
    }

    public static File createFileFromBitmap(Bitmap bitmap, String fileName) {

        if (bitmap == null) return null;

        File photoFile=null;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File photoStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (photoStorage!=null){
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH) + 1;
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            int hr = c.get(Calendar.HOUR);
            int min = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);
            photoFile = new File(photoStorage, fileName + ".jpg");
            try {
                //f.createNewFile();
                FileOutputStream fo = new FileOutputStream(photoFile);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                Log.e("iamge", "Error saving image ", e);
            }
        }

        return photoFile;
    }
}

