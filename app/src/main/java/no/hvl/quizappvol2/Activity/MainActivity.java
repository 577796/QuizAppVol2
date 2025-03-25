package no.hvl.quizappvol2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import no.hvl.quizappvol2.R;

public class MainActivity extends AppCompatActivity {

    Button galleryButton, quizButton; // Buttons for navigating to Gallery and Quiz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this); // Enables edge-to-edge content rendering

        setContentView(R.layout.activity_main); // Sets the layout to activity_main.xml

        // Connect UI buttons to their corresponding views
        galleryButton = findViewById(R.id.galleryButton);
        quizButton = findViewById(R.id.quizButton);

        // Adjusts layout padding based on system bars (e.g., notch, nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Handle click to open the gallery
        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GalleryActivity.class); // Create intent to start GalleryActivity
            startActivity(intent); // Launch the activity
        });

        // Handle click to start the quiz
        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity.class); // Create intent to start QuizActivity
            startActivity(intent); // Launch the activity
        });
    }
}
