package com.example.camera;

import android.Manifest;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class ImageDisplayActivity extends AppCompatActivity {

    private Bitmap selectedBitmap;
    private Bitmap filteredBitmap; // To store the filtered image
    private ImageView displayImageView;
    private Button applyFilterButton;
    private Button saveImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        // Initialize UI components
        displayImageView = findViewById(R.id.displayImageView);
        applyFilterButton = findViewById(R.id.applyFilterButton);
        saveImageButton = findViewById(R.id.saveImageButton);

        // Check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Set listener for the save button
        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filteredBitmap != null) {
                    saveToGallery(filteredBitmap);
                } else {
                    Log.d("ImageDisplay", "No filter applied, saving original image");
                    saveToGallery(selectedBitmap); // Save the original if no filter is applied
                }
            }
        });

        // Get the bitmap passed from MainActivity
        selectedBitmap = getIntent().getParcelableExtra("image_data");

        if (selectedBitmap != null) {
            // Set the image to the ImageView
            displayImageView.setImageBitmap(selectedBitmap);
        }

        // Set listener for the apply filter button
        applyFilterButton.setOnClickListener(v -> showFilterDialog());
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

    // Apply selected filter to the image
    private void applyColorFilter(String selectedFilter) {
        if (selectedBitmap == null) {
            return;
        }

        ColorMatrix colorMatrix = new ColorMatrix();
        Bitmap newBitmap = selectedBitmap.copy(Bitmap.Config.ARGB_8888, true); // Copy the original bitmap to apply filter

        switch (selectedFilter) {
            case "Grayscale":
                colorMatrix.setSaturation(0);
                break;
            case "Sepia":
                colorMatrix.setScale(1.2f, 1.0f, 0.8f, 1.0f);
                break;
            case "Invert":
                colorMatrix.set(new float[]{
                        -1,  0,  0,  0, 255,
                        0, -1,  0,  0, 255,
                        0,  0, -1,  0, 255,
                        0,  0,  0,  1,   0
                });
                break;
            default:
                colorMatrix.setSaturation(1); // No filter
                break;
        }

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        newBitmap = applyColorFilterToBitmap(newBitmap, filter); // Apply the filter to the new bitmap
        filteredBitmap = newBitmap; // Save the filtered bitmap
        displayImageView.setImageBitmap(filteredBitmap); // Display the filtered bitmap
    }

    private Bitmap applyColorFilterToBitmap(Bitmap bitmap, ColorMatrixColorFilter filter) {
        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        android.graphics.Paint paint = new android.graphics.Paint();
        paint.setColorFilter(filter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmap;
    }

    private void saveToGallery(Bitmap bitmap) {
        // Create a ContentValues object to insert the image into the MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "image_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Ayouta");

        // Insert the image into the MediaStore
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Write the bitmap to the content provider
        try (OutputStream outStream = getContentResolver().openOutputStream(uri)) {
            if (outStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                Log.d("ImageDisplay", "Image saved to gallery: " + uri);
                outStream.flush();
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
