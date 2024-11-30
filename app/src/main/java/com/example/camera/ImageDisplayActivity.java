package com.example.camera;

import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class ImageDisplayActivity extends AppCompatActivity {

    private Bitmap selectedBitmap;
    private ImageView displayImageView;
    private Button applyFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        // Initialize UI components
        displayImageView = findViewById(R.id.displayImageView);
        applyFilterButton = findViewById(R.id.applyFilterButton);

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

        switch (selectedFilter) {
            case "Grayscale":
                colorMatrix.setSaturation(0);
                break;
            case "Sepia":
                colorMatrix.setSaturation(1);
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
        displayImageView.setColorFilter(filter);
    }
}
