package com.example.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;
    private static final int PERMISSION_REQUEST = 102;

    private Button captureButton, selectImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = findViewById(R.id.captureButton);
        selectImageButton = findViewById(R.id.selectImageButton);

        // Capture Image button
        captureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
            }
        });

        // Select Image button
        selectImageButton.setOnClickListener(v -> openGallery());
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bitmap selectedBitmap = null;
            if (requestCode == CAMERA_REQUEST) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == GALLERY_REQUEST) {
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (selectedBitmap != null) {
                // Send the image to ImageDisplayActivity
                Intent intent = new Intent(this, ImageDisplayActivity.class);
                intent.putExtra("image_data", selectedBitmap);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }
}
