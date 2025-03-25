package no.hvl.quizappvol2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database; // Annotation to declare a Room database
import androidx.room.Room; // Used to build the Room database
import androidx.room.RoomDatabase; // Base class for Room databases
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

import no.hvl.quizappvol2.DAO.ImageItemDAO;

// Declares a Room database with the entity ImageItem, version 2, and schema export turned off
@Database(entities = {ImageItem.class}, version = 2, exportSchema = false)
public abstract class ImageDatabase extends RoomDatabase {

    private static ImageDatabase INSTANCE; // Singleton instance of the database

    // Abstract method to access the DAO
    public abstract ImageItemDAO imageItemDAO();

    // Returns the singleton instance of the database (thread-safe)
    public static synchronized ImageDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            // Build the database using Room
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ImageDatabase.class, "image_database.db") // Name of the database file
                    .fallbackToDestructiveMigration() // Deletes and recreates database on version mismatch
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Executes initialization logic in a background thread after DB is first created
                            Executors.newSingleThreadExecutor().execute(() -> {
                                ImageDatabase database = getInstance(context);
                                ImageItemDAO dao = database.imageItemDAO();

                                // Insert initial "stock" data when the database is created
                                dao.insertImage(new ImageItem("file:///android_asset/images/gulbil.jpg", "Gul Bil"));
                                dao.insertImage(new ImageItem("file:///android_asset/images/rodbil.jpg", "Rød Bil"));
                                dao.insertImage(new ImageItem("file:///android_asset/images/hvitbil.jpg", "Hvit bil"));
                            });
                        }
                    })
                    .build(); // Finalize and build the database
        }
        return INSTANCE;
    }
}
