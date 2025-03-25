package no.hvl.quizappvol2.DAO; // Defines the package for DAO (Data Access Object) classes

import androidx.lifecycle.LiveData; // LiveData allows data to be observed for changes (used with ViewModels)
import androidx.room.Dao; // Annotation to mark this interface as a DAO for Room
import androidx.room.Insert; // Annotation to mark insert operations
import androidx.room.OnConflictStrategy; // Strategy used to handle conflicts on insert
import androidx.room.Query; // Annotation for writing SQL queries
import androidx.room.Delete; // Annotation to mark delete operations

import java.util.List; // Java's List collection for holding multiple items
import no.hvl.quizappvol2.ImageItem; // Import the ImageItem class used in the DAO methods

@Dao // Marks this interface as a Data Access Object for Room database
public interface ImageItemDAO {

    // Inserts a single ImageItem, replaces it if it already exists based on primary key
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(ImageItem imageItem);

    // Deletes the specified ImageItem from the database
    @Delete
    void deleteImage(ImageItem imageItem);

    // Returns a LiveData object containing the count of images in the table
    @Query("SELECT COUNT(*) FROM image_table")
    LiveData<Integer> getImageCountLive();

    // Retrieves all images in the table as LiveData (to be observed)
    @Query("SELECT * FROM image_table")
    LiveData<List<ImageItem>> getAllImages();

    // Deletes an image based on its ID
    @Query("DELETE FROM image_table WHERE id = :imageId")
    void deleteImageById(int imageId);

    // Inserts a list of ImageItems into the table
    @Insert
    void insertMultipleImages(List<ImageItem> imageItems);

    // Retrieves all images in the table as a plain List (non-LiveData, synchronous access)
    @Query("SELECT * FROM image_table")
    List<ImageItem> getAllImagesSync();
}
