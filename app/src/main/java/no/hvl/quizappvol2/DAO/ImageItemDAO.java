package no.hvl.quizappvol2.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;
import no.hvl.quizappvol2.ImageItem;

@Dao
public interface ImageItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(ImageItem imageItem);

    @Delete
    void deleteImage(ImageItem imageItem);

    @Query("SELECT COUNT(*) FROM image_table")
    LiveData<Integer> getImageCountLive();


    @Query("SELECT * FROM image_table")
    LiveData<List<ImageItem>> getAllImages();


    @Query("DELETE FROM image_table WHERE id = :imageId")
    void deleteImageById(int imageId);

    @Insert
    void insertMultipleImages(List<ImageItem> imageItems);

    @Query("SELECT * FROM image_table")
    List<ImageItem> getAllImagesSync();

}
