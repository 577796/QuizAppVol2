package no.hvl.quizappvol2;

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
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import no.hvl.quizappvol2.DAO.ImageItemDAO;
import no.hvl.quizappvol2.ImageDatabase;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<ImageItem> imageList;
    private final ImageItemDAO imageItemDAO;
    private final ExecutorService executorService;

    public RecyclerAdapter(Context context, List<ImageItem> imageList) {
        this.context = context;
        this.imageList = imageList;
        this.imageItemDAO = ImageDatabase.getInstance(context).imageItemDAO();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem item = imageList.get(position);
        holder.descriptionText.setText(item.getDescription());

        Glide.with(context)
                .load(item.getImagePath())
                .into(holder.imageView);

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> deleteImage(item, position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void updateData(List<ImageItem> newList) {
        this.imageList = newList;
        notifyDataSetChanged();
    }

    private void deleteImage(ImageItem imageItem, int position) {
        executorService.execute(() -> {
            imageItemDAO.deleteImage(imageItem);
            imageList.remove(position);

            // Ensure UI updates are done on the main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                notifyItemRemoved(position);
            });
        });
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionText;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            descriptionText = itemView.findViewById(R.id.description);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
