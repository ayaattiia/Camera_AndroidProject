package com.example.camera;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
public class Gallery extends AppCompatActivity {

        private GridView gridView;
        private ImageAdapter imageAdapter;
        private ArrayList<String> imagePaths;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gallery);

            gridView = findViewById(R.id.gridView);
            imagePaths = new ArrayList<>();

            // Charger les images enregistrées dans le dossier "/Ayouta"
            loadImages();

            // Créer un adaptateur pour afficher les images dans une GridView
            imageAdapter = new ImageAdapter(this, imagePaths);
            gridView.setAdapter(imageAdapter);
        }

        private void loadImages() {
            // Le chemin où les images sont enregistrées
            File directory = new File(getExternalFilesDir(null), "Ayouta");
            if (directory.exists() && directory.isDirectory()) {
                // Parcourir tous les fichiers du répertoire
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".png")) {
                            imagePaths.add(file.getAbsolutePath());
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
            }
        }
    }

