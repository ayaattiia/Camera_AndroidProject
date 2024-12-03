package com.example.camera;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoDisplayActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);

        videoView = findViewById(R.id.videoView);

        // Retrieve the video URI from the Intent
        String videoUriString = getIntent().getStringExtra("video_uri");
        if (videoUriString != null) {
            Uri videoUri = Uri.parse(videoUriString);

            // Set the video URI to the VideoView
            videoView.setVideoURI(videoUri);

            // Add media controls
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            // Start the video
            videoView.setOnPreparedListener(mp -> videoView.start());

            // Handle errors
            videoView.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "Failed to play video", Toast.LENGTH_SHORT).show();
                return true;
            });
        } else {
            Toast.makeText(this, "No video to display", Toast.LENGTH_SHORT).show();
        }
    }
}