package com.example.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;
    private static final int PERMISSION_REQUEST_CAMERA = 102;
    private static final int PERMISSION_REQUEST_STORAGE = 103;

    private Button captureButton, selectImageButton ,selectImageGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        captureButton = findViewById(R.id.captureButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        selectImageGallery = findViewById(R.id.selectImageGallery);

        // Demander les permissions nécessaires
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
        }


        // Bouton de capture d'image
        captureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        });

        // Bouton de sélection d'image depuis la galerie
        selectImageButton.setOnClickListener(v -> openGallery());


        selectImageGallery.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Gallery.class);
            startActivity(intent);
        });
    }

    // Ouvre l'appareil photo pour capturer une image
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    // Ouvre la galerie pour sélectionner une image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    // Gère le résultat de la capture ou de la sélection d'image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Bitmap selectedBitmap = null;

            if (requestCode == CAMERA_REQUEST) {
                selectedBitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == GALLERY_REQUEST) {
                try {
                    // Vérifie si l'URI de l'image est valide
                    if (data.getData() != null) {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        Toast.makeText(this, "image selected", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Si une image est sélectionnée ou capturée, envoyer l'image à ImageDisplayActivity
            if (selectedBitmap != null) {
                Intent intent = new Intent(this, ImageDisplayActivity.class);
                intent.putExtra("image_data", selectedBitmap);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Failed to select or capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Gère les résultats des permissions demandées
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSION_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Vous pouvez ajouter ici une action supplémentaire si nécessaire après l'autorisation
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
