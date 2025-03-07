package no.hvl.quizappvol2;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
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

import java.io.File;
import java.util.List;

import no.hvl.quizappvol2.Activity.GalleryActivity;
import no.hvl.quizappvol2.DAO.ImageItemDAO;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private List<ImageItem> imageList;
    private ImageItemDAO dao;
    private GalleryActivity galleryActivity;

    public RecyclerAdapter(List<ImageItem> imageList, ImageItemDAO dao, GalleryActivity galleryActivity) {
        this.imageList = imageList;
        this.dao = dao;
        this.galleryActivity = galleryActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_single_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageItem item = imageList.get(position);

        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(item.getImagePath()));
        holder.description.setText(item.getDescription());
        holder.deleteBtn.setOnClickListener(v -> deleteImage(holder.getAdapterPosition()));
    }

    public void deleteImage(int position) {
        ImageItem item = imageList.get(position);
        new Thread(() -> {
            dao.deleteImage(item);
            new Handler(Looper.getMainLooper()).post(() -> {
                imageList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, imageList.size());

                if (galleryActivity != null) {
                    galleryActivity.updateImageCount(imageList.size());
                }
            });
        }).start();
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView description;
        Button deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);
            deleteBtn = itemView.findViewById(R.id.btnDelete);
        }
    }
}
