package no.hvl.quizappvol2;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image_table")
public class ImageItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imagePath;
    private String description;

    public ImageItem(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
