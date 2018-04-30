package com.tradelink.scandocapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tradelink.scandocapp.utils.BitmapHelper;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CAMERA = 314;
    public static final int RESPONSE_CODE = 100;
    private ImageView croppedDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        croppedDocument = findViewById(R.id.cropDocument);
        findViewById(R.id.show_dialog_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.CAMERA)) {
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSIONS_REQUEST_CAMERA);
                    }
                } else {
                    openCameraDialog();
                }
            }
        });
        float a4Width = 210, a4Height = 297;
        final DrawView drawView = (DrawView) findViewById(R.id.draw_view);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float ratio = size.x / 210;

        Log.d("MainActivity", "width " + size.x + " height " + size.y);
        drawView.setLayoutParams(new RelativeLayout.LayoutParams(size.x, (int) (a4Height * ratio)));
        Button addBoxBtn = findViewById(R.id.add_box_btn);
        addBoxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.addBox();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraDialog();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d("onActivityResult", "ok");
        if (resultCode == RESULT_OK) {
            if (intent.hasExtra(CameraDialogFragment.CAPTURED_IMAGE)) {
                String imagePath = intent.getStringExtra(CameraDialogFragment.CAPTURED_IMAGE);
                Bitmap document = BitmapHelper.getBitmapFromStorage(imagePath);
                croppedDocument.setImageBitmap(document);
            }
        }
    }

    private void openCameraDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = CameraDialogFragment.newInstance();
        newFragment.show(ft, "dialog");
    }
}