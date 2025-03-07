package no.hvl.quizappvol2.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import no.hvl.quizappvol2.ImageItem;

@Dao
public interface ImageItemDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ImageItem imageItem);

    @Query("SELECT COUNT(*) FROM image_table")
    LiveData<Integer> getImageCountLive(); // Return LiveData for observation


    @Query("SELECT * FROM image_table")
    List<ImageItem> getAllImages();  // Retrieve all images

    @Delete
    void deleteImage(ImageItem imageItem);
}
