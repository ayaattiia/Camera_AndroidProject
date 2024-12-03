package com.example.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends Activity {
    private static final int VIDEO_REQUEST = 102;
    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;


    private static final int PERMISSION_REQUEST_AUDIO = 105;
    private static final int PERMISSION_REQUEST = 102;
    private static final int PERMISSION_REQUEST_CAMERA = 103;


    private Button captureButton, selectImageButton, recordVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = findViewById(R.id.captureButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        recordVideoButton = findViewById(R.id.recordVideoButton);

        // Request necessary permissions
        checkPermissions();

        // Camera capture
        captureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST);
            }
        });

        // Gallery selection
        selectImageButton.setOnClickListener(v -> openGallery());
        // Record Video button click listener
        recordVideoButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                recordVideo();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO},
                        PERMISSION_REQUEST_CAMERA);
            }
        });

    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
                break;
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap capturedBitmap = (Bitmap) data.getExtras().get("data");
                if (capturedBitmap != null) {
                    Intent intent = new Intent(this, ImageDisplayActivity.class);
                    intent.putExtra("captured_bitmap", capturedBitmap);
                    startActivity(intent);
                }
            } else if (requestCode == GALLERY_REQUEST) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    Intent intent = new Intent(this, ImageDisplayActivity.class);
                    intent.putExtra("image_uri", imageUri.toString());
                    startActivity(intent);
                }
            }else if (requestCode == VIDEO_REQUEST) {
                // Handle video recording
                Intent intent = new Intent(this, VideoDisplayActivity.class);
                intent.putExtra("video_uri", data.getData().toString());
                startActivity(intent);
            }

        } else {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions are required to use this app", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }else if (requestCode == PERMISSION_REQUEST_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }
}