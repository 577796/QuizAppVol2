package no.hvl.quizappvol2.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import no.hvl.quizappvol2.DAO.ImageItemDAO;
import no.hvl.quizappvol2.ImageDatabase;
import no.hvl.quizappvol2.ImageItem;
import no.hvl.quizappvol2.RecyclerAdapter;
import no.hvl.quizappvol2.R;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView; // UI list to show all images
    private RecyclerAdapter adapter; // Adapter to bind data to RecyclerView
    private ImageItemDAO imageItemDAO; // DAO for accessing database
    private ActivityResultLauncher<String> imagePickerLauncher; // Launcher for selecting multiple images
    private List<Uri> selectedImageUris = new ArrayList<>(); // Stores URIs of user-selected images
    private TextView photos; // Displays number of images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view); // Set the layout with RecyclerView

        recyclerView = findViewById(R.id.recyclerView); // Get RecyclerView from layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Display items in vertical list
        photos = findViewById(R.id.photos); // Get reference to the TextView for image count

        imageItemDAO = ImageDatabase.getInstance(this).imageItemDAO(); // Get DAO from Room DB
        adapter = new RecyclerAdapter(this, new ArrayList<>()); // Create adapter with empty list
        recyclerView.setAdapter(adapter); // Attach adapter to RecyclerView

        // Observe changes in the image table using LiveData
        imageItemDAO.getAllImages().observe(this, imageList -> {
            adapter.updateData(imageList); // Update adapter when data changes
            photosCount(imageList.size()); // Update image count display
        });

        // Initialize image picker launcher for selecting multiple images
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetMultipleContents(),
                uris -> {
                    if (uris != null && !uris.isEmpty()) {
                        selectedImageUris.clear(); // Clear previous selections
                        selectedImageUris.addAll(uris); // Add selected images
                        showDescriptionDialog(); // Show input fields for descriptions
                    }
                }
        );
    }

    // Triggered when user taps the "add images" button
    public void openImagePicker(View view) {
        imagePickerLauncher.launch("image/*"); // Launches system image picker for multiple image selection
    }

    // Entry point to the image description process
    // Starts a loop of dialogs, one for each selected image
    private void showDescriptionDialog() {
        showNextDescriptionDialog(0); // Start with the first image in the list
    }

    // Recursively displays a dialog for one image at a time, asking the user to add a description
    private void showNextDescriptionDialog(int index) {
        if (index >= selectedImageUris.size()) {
            // All images processed → update UI
            refreshGallery();
            return;
        }

        // Get current image URI to display
        Uri currentUri = selectedImageUris.get(index);

        // Set up the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Description");

        // Create a vertical layout to hold the image and description input
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Show the selected image using Glide
        ImageView imageView = new ImageView(this);
        Glide.with(this).load(currentUri).into(imageView);
        layout.addView(imageView);

        // Input field for user to type in a description
        EditText input = new EditText(this);
        input.setHint("Enter description");
        layout.addView(input);

        // Attach the layout to the dialog
        builder.setView(layout);

        // Save button: persist image + description, then continue to next image
        builder.setPositiveButton("Save", (dialog, which) -> {
            String imagePath = currentUri.toString();
            String description = input.getText().toString().trim();
            ImageItem item = new ImageItem(imagePath, description); // Create a new item for DB

            // Save to DB in background thread
            new Thread(() -> {
                try {
                    // Request long-term permission to access the image
                    getContentResolver().takePersistableUriPermission(
                            currentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    Log.e("GalleryActivity", "URI permission error", e);
                }

                // Insert item into database
                imageItemDAO.insertImage(item);

                // Proceed to the next image on the UI thread
                runOnUiThread(() -> showNextDescriptionDialog(index + 1));
            }).start();
        });

        // Cancel button: skip current image and move on
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            runOnUiThread(() -> showNextDescriptionDialog(index + 1));
        });

        // Show the dialog
        builder.show();
    }


    // Forces the RecyclerView to refresh by re-observing LiveData
    private void refreshGallery() {
        runOnUiThread(() -> {
            imageItemDAO.getAllImages().observe(this, imageList -> {
                adapter.updateData(imageList); // Update list in RecyclerView
                photosCount(imageList.size()); // Update image count
            });
        });
    }

    // Displays the current number of images
    private void photosCount(int count) {
        photos.setText("Total Images: " + count); // Show in TextView
    }
}
