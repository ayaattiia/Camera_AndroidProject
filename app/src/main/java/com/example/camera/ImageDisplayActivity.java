package com.example.camera;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;

public class ImageDisplayActivity extends AppCompatActivity {


    private Bitmap displayedBitmap; // Holds the original or captured bitmap
    private Bitmap filteredBitmap; // Holds the filtered image
    private ImageView displayImageView;
    private Button applyFilterButton;
    private Button saveImageButton,recordVideoButton;
    private Uri imageUri; // Holds the URI if the image is from the gallery

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        // Initialize UI components
        displayImageView = findViewById(R.id.displayImageView);
        applyFilterButton = findViewById(R.id.applyFilterButton);
        saveImageButton = findViewById(R.id.saveImageButton);


        // Retrieve data from the intent
        Bitmap capturedBitmap = getIntent().getParcelableExtra("captured_bitmap");
        String uriString = getIntent().getStringExtra("image_uri");

        if (capturedBitmap != null) {
            displayedBitmap = capturedBitmap;
        } else if (uriString != null) {
            imageUri = Uri.parse(uriString);
            try {
                displayedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                Log.e("ImageDisplayActivity", "Error loading image", e);
                Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (displayedBitmap != null) {
            displayImageView.setImageBitmap(displayedBitmap);
        } else {
            Toast.makeText(this, "No image to display", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set up button actions
        applyFilterButton.setOnClickListener(v -> showFilterDialog());
        saveImageButton.setOnClickListener(v -> saveImage());
    }

    private void showFilterDialog() {
        String[] filters = {"Grayscale", "Sepia", "Invert", "None"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Filter");
        builder.setItems(filters, (dialog, which) -> {
            String selectedFilter = filters[which];
            applyColorFilter(selectedFilter);
        });

        builder.create().show();
    }

    private void applyColorFilter(String filterType) {
        if (displayedBitmap == null) return;

        ColorMatrix colorMatrix = new ColorMatrix();
        switch (filterType) {
            case "Grayscale":
                colorMatrix.setSaturation(0);
                break;
            case "Sepia":
                colorMatrix.setScale(1.2f, 1.0f, 0.8f, 1.0f);
                break;
            case "Invert":
                colorMatrix.set(new float[]{
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0
                });
                break;
            default:
                colorMatrix.setSaturation(1);
                break;
        }

        filteredBitmap = applyColorMatrix(displayedBitmap, colorMatrix);
        displayImageView.setImageBitmap(filteredBitmap);
    }

    private Bitmap applyColorMatrix(Bitmap bitmap, ColorMatrix colorMatrix) {
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        android.graphics.Canvas canvas = new android.graphics.Canvas(result);
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(result, 0, 0, paint);
        return result;
    }

    private void saveImage() {
        Bitmap bitmapToSave = (filteredBitmap != null) ? filteredBitmap : displayedBitmap;
        if (bitmapToSave == null) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/AppCamera");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            bitmapToSave.compress(Bitmap.CompressFormat.JPEG, 85, out);
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("ImageDisplayActivity", "Error saving image", e);
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }
}