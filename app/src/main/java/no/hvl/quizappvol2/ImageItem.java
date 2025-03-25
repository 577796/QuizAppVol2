package no.hvl.quizappvol2; // Declares the package this class belongs to

import androidx.room.Entity; // Imports the @Entity annotation from Room for database use
import androidx.room.PrimaryKey; // Imports the @PrimaryKey annotation for setting primary keys in Room

@Entity(tableName = "image_table") // Marks this class as a database entity with a table name "image_table"
public class ImageItem {

    @PrimaryKey(autoGenerate = true) // Marks this field as the primary key and auto-generates unique values
    private int id; // Unique ID for each image item

    private String imagePath; // Stores the path to the image file
    private String description; // Stores the user-provided description of the image

    // Constructor to initialize the object with an image path and description
    public ImageItem(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    // Getter for the id
    public int getId() {
        return id;
    }

    // Setter for the id (used by Room when auto-generating ID)
    public void setId(int id) {
        this.id = id;
    }

    // Getter for the image path
    public String getImagePath() {
        return imagePath;
    }

    // Setter for the image path
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Getter for the description
    public String getDescription() {
        return description;
    }

    // Setter for the description
    public void setDescription(String description) {
        this.description = description;
    }
}
