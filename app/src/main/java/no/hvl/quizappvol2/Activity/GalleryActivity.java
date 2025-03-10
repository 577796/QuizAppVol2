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
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ImageItemDAO imageItemDAO;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private TextView photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        photos = findViewById(R.id.photos);

        imageItemDAO = ImageDatabase.getInstance(this).imageItemDAO();
        adapter = new RecyclerAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // ✅ Observe changes in database and update RecyclerView
        imageItemDAO.getAllImages().observe(this, imageList -> {
            adapter.updateData(imageList);
            photosCount(imageList.size());
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), uris -> {
            if (uris != null && !uris.isEmpty()) {
                selectedImageUris.clear();
                selectedImageUris.addAll(uris);
                showDescriptionDialog();
            }
        });
    }


    public void openImagePicker(View view) {
        imagePickerLauncher.launch("image/*");
    }

    private void showDescriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Description");

        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(layout);

        List<EditText> descriptionInputs = new ArrayList<>();

        for (Uri uri : selectedImageUris) {
            ImageView imageView = new ImageView(this);
            Glide.with(this).load(uri).into(imageView);
            layout.addView(imageView);

            EditText input = new EditText(this);
            input.setHint("Enter description");
            layout.addView(input);
            descriptionInputs.add(input);
        }

        builder.setView(scrollView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            List<ImageItem> imageItems = new ArrayList<>();
            for (int i = 0; i < selectedImageUris.size(); i++) {
                String imagePath = selectedImageUris.get(i).toString();
                String description = descriptionInputs.get(i).getText().toString().trim();
                imageItems.add(new ImageItem(imagePath, description));
            }
            saveImages(imageItems);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveImages(List<ImageItem> imageItems) {
        new Thread(() -> {
            for (ImageItem item : imageItems) {
                Uri imageUri = Uri.parse(item.getImagePath());
                try {
                    getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    Log.e("GalleryActivity", "Failed to persist URI permission", e);
                }
                imageItemDAO.insertImage(item);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Images added successfully!", Toast.LENGTH_SHORT).show();
                refreshGallery(); // Ensure UI update happens on the main thread
            });
        }).start();
    }


    private void refreshGallery() {
        runOnUiThread(() -> { // Ensure LiveData observe is on the main thread
            imageItemDAO.getAllImages().observe(this, imageList -> {
                adapter.updateData(imageList);
                photosCount(imageList.size()); // Update count when images refresh
            });
        });
    }

    private void photosCount(int count) {
        photos.setText("Total Images: " + count);
    }
}
