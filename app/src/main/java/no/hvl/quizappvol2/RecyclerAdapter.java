package no.hvl.quizappvol2; // Declares the package for this class

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView; // RecyclerView for efficient scrolling UI

import com.bumptech.glide.Glide; // Glide for loading images

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import no.hvl.quizappvol2.DAO.ImageItemDAO;

// RecyclerAdapter is a custom adapter for RecyclerView to display a list of ImageItems
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Context context; // Android context (used to access resources, DB, etc.)
    private List<ImageItem> imageList; // The list of images to display
    private final ImageItemDAO imageItemDAO; // DAO for performing database operations
    private final ExecutorService executorService; // Used for background threading

    // Constructor initializes context, list, DAO and executor service
    public RecyclerAdapter(Context context, List<ImageItem> imageList) {
        this.context = context;
        this.imageList = imageList;
        this.imageItemDAO = ImageDatabase.getInstance(context).imageItemDAO(); // Gets DAO from singleton DB instance
        this.executorService = Executors.newSingleThreadExecutor(); // Creates a single-thread executor for background tasks
    }

    // Called when a new ViewHolder needs to be created
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout for a single image item in the list
        View view = LayoutInflater.from(context).inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view); // Returns a new ViewHolder object
    }

    // Binds data to the views in a ViewHolder at a given position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem item = imageList.get(position); // Gets the item at the current position
        holder.descriptionText.setText(item.getDescription()); // Sets the description text

        // Uses Glide to load image from file path into the ImageView
        Glide.with(context)
                .load(item.getImagePath())
                .into(holder.imageView);

        // Sets the click listener for the delete button
        holder.deleteButton.setOnClickListener(v -> deleteImage(item, position));
    }

    // Returns the number of items in the list
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // Updates the list and refreshes the RecyclerView
    public void updateData(List<ImageItem> newList) {
        this.imageList = newList;
        notifyDataSetChanged();
    }

    // Deletes an image both from the database and the UI
    private void deleteImage(ImageItem imageItem, int position) {
        executorService.execute(() -> { // Runs DB operation in background thread
            imageItemDAO.deleteImage(imageItem); // Deletes the image from database
            imageList.remove(position); // Removes the item from the local list

            // Ensures the UI update runs on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                notifyItemRemoved(position); // Notifies RecyclerView to remove the item visually
            });
        });
    }

    // ViewHolder class that holds references to the views inside each item layout
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionText;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image); // Image view from layout
            descriptionText = itemView.findViewById(R.id.description); // Text view for description
            deleteButton = itemView.findViewById(R.id.btnDelete); // Button to delete the item
        }
    }
}
