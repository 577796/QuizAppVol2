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

    Button galleryButton, quizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        galleryButton = findViewById(R.id.galleryButton);
        quizButton = findViewById(R.id.quizButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
            startActivity(intent);
        });

        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            startActivity(intent);
        });
    }
}