package com.tradelink.scandocapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.tradelink.scandocapp.utils.BitmapHelper;

import net.doo.snap.lib.detector.ContourDetector;
import net.doo.snap.lib.detector.DetectionResult;
import net.doo.snap.lib.detector.Line2D;
import net.doo.snap.ui.EditPolygonImageView;
import net.doo.snap.ui.MagnifierView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class EditPolygonActivity extends AppCompatActivity {

    private EditPolygonImageView editPolygonView;
    private MagnifierView magnifierView;
    private Bitmap originalBitmap;
    private ImageView resultImageView;
    private Button cropButton;
    private Button backButton;
    private Button doneButton;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanbot_edit_polygon_view);

        getSupportActionBar().hide();

        Bitmap origin;

        if (getIntent().hasExtra(CameraDialogFragment.CAPTURED_IMAGE)) {
            Log.d("origin", getIntent().getStringExtra(CameraDialogFragment.CAPTURED_IMAGE));
            origin = BitmapHelper.getBitmapFromStorage(getIntent().getStringExtra(CameraDialogFragment.CAPTURED_IMAGE));

            editPolygonView = (EditPolygonImageView) findViewById(R.id.polygonView);
            if (!origin.isRecycled()) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                if (origin.getWidth() > origin.getHeight()) {
                    origin = Bitmap.createBitmap(origin, 0, 0, origin.getWidth(), origin.getHeight(), matrix, false);
                }
                editPolygonView.setImageBitmap(Bitmap.createBitmap(origin));
                originalBitmap = origin;
            } else {
                editPolygonView.setImageResource(R.drawable.test_receipt);
                originalBitmap = ((BitmapDrawable) editPolygonView.getDrawable()).getBitmap();
            }


//            editPolygonView.setImageResource(R.drawable.test_receipt);
//        editPolygonView.setImageBitmap(mImageContainer.getOrigin());
//        originalBitmap = mImageContainer.getOrigin();


            magnifierView = (MagnifierView) findViewById(R.id.magnifier);
            // MagifierView should be set up every time when editPolygonView is set with new image
            magnifierView.setupMagnifier(editPolygonView);

            resultImageView = (ImageView) findViewById(R.id.resultImageView);
            resultImageView.setVisibility(View.GONE);

            cropButton = (Button) findViewById(R.id.cropButton);
            cropButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crop();
                }
            });

            backButton = (Button) findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backButton.setVisibility(View.GONE);
                    resultImageView.setVisibility(View.GONE);

                    editPolygonView.setVisibility(View.VISIBLE);
                    cropButton.setVisibility(View.VISIBLE);
                }
            });

            doneButton = findViewById(R.id.doneButton);
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra(CameraDialogFragment.CAPTURED_IMAGE, imagePath);
                    setResultAndFinish(intent);
                }
            });

            new InitImageViewTask().executeOnExecutor(Executors.newSingleThreadExecutor(), originalBitmap);
        }
    }

    private void crop() {
        // crop & warp image by selected polygon (editPolygonView.getPolygon())
        final Bitmap documentImage = new ContourDetector().processImageF(
                originalBitmap, editPolygonView.getPolygon(), ContourDetector.IMAGE_FILTER_NONE);

        editPolygonView.setVisibility(View.GONE);
        cropButton.setVisibility(View.GONE);

        resultImageView.setImageBitmap(documentImage);
        resultImageView.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.VISIBLE);

        try {
            imagePath = BitmapHelper.saveToFile(getApplicationContext(), documentImage,"document.jpg");
        } catch (IOException e) {
            e.printStackTrace();
            imagePath = "null";
        }
    }

    private void setResultAndFinish(Intent intent) {
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * Detects horizontal and vertical lines and polygon of the given bitmap image.
     * Initializes EditPolygonImageView with detected lines and polygon.
     */
    class InitImageViewTask extends AsyncTask<Bitmap, Void, InitImageResult> {

        @Override
        protected InitImageResult doInBackground(Bitmap... params) {
            Bitmap image = params[0];
            ContourDetector detector = new ContourDetector();
            final DetectionResult detectionResult = detector.detect(image);
            Pair<List<Line2D>, List<Line2D>> linesPair = null;
            List<PointF> polygon = new ArrayList<>(EditPolygonImageView.DEFAULT_POLYGON);
            switch (detectionResult) {
                case OK:
                case OK_BUT_BAD_ANGLES:
                case OK_BUT_TOO_SMALL:
                case OK_BUT_BAD_ASPECT_RATIO:
                    linesPair = new Pair<>(detector.getHorizontalLines(), detector.getVerticalLines());
                    polygon = detector.getPolygonF();
                    break;
            }

            return new InitImageResult(linesPair, polygon);
        }

        @Override
        protected void onPostExecute(final InitImageResult initImageResult) {
            // set detected polygon and lines into EditPolygonImageView
            editPolygonView.setPolygon(initImageResult.polygon);
            if (initImageResult.linesPair != null) {
                editPolygonView.setLines(initImageResult.linesPair.first, initImageResult.linesPair.second);
            }
        }
    }

    class InitImageResult {
        final Pair<List<Line2D>, List<Line2D>> linesPair;
        final List<PointF> polygon;

        InitImageResult(final Pair<List<Line2D>, List<Line2D>> linesPair, final List<PointF> polygon) {
            this.linesPair = linesPair;
            this.polygon = polygon;
        }
    }
}
