package no.hvl.quizappvol2.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import no.hvl.quizappvol2.ImageDatabase;
import no.hvl.quizappvol2.ImageItem;
import no.hvl.quizappvol2.R;
import no.hvl.quizappvol2.RecyclerAdapter;

public class GalleryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public TextView textView;
    Button addPhoto;
    List<ImageItem> imageList = new ArrayList<>();
    RecyclerAdapter adapter;
    ImageDatabase db;

    private static final int READ_PERMISSION = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        textView = findViewById(R.id.photos);
        recyclerView = findViewById(R.id.recyclerView);
        addPhoto = findViewById(R.id.addPhoto);
        db = ImageDatabase.getInstance(this);

        adapter = new RecyclerAdapter(imageList, db.imageItemDAO(), this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
        
        loadImagesFromDatabase();

        addPhoto.setOnClickListener(v -> openGallery());

        String requiredPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            requiredPermission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            requiredPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, requiredPermission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{requiredPermission}, READ_PERMISSION);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null) {
            List<Uri> selectedUris = new ArrayList<>();

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    selectedUris.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                selectedUris.add(data.getData());
            }

            for (Uri uri : selectedUris) {
                showDescriptionDialog(uri);
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return filePath;
    }

    private void showDescriptionDialog(Uri imageUri) {

        // Regular UI flow for normal users
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Description");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(this);
        imageView.setImageURI(imageUri);
        layout.addView(imageView);

        EditText input = new EditText(this);
        input.setHint("Enter description...");
        layout.addView(input);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String description = input.getText().toString().trim();
            if (description.isEmpty()) description = "Default Description"; // ✅ Default for normal users

            String imagePath = getRealPathFromURI(imageUri);
            if (imagePath != null) {
                saveImageToDatabase(imagePath, description);
            } else {
                Toast.makeText(this, "Failed to get image path", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show(); // Only show for normal users
    }

    private void saveImageToDatabase(String imagePath, String description) {
        new Thread(() -> {
            db.imageItemDAO().insert(new ImageItem(imagePath, description));
            runOnUiThread(() -> {
                loadImagesFromDatabase();
                updateImageCount(imageList.size());
            });
        }).start();
    }

    private void loadImagesFromDatabase() {
        new Thread(() -> {
            imageList.clear();
            imageList.addAll(db.imageItemDAO().getAllImages());

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                updateImageCount(imageList.size());
            });
        }).start();
    }


    public void updateImageCount(int count) {
        textView.setText("Total images: " + count);
    }

    public int getImageCount() {
        return imageList.size();
    }
}
