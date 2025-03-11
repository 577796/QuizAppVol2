package no.hvl.quizappvol2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

import no.hvl.quizappvol2.DAO.ImageItemDAO;
import no.hvl.quizappvol2.ImageItem;

@Database(entities = {ImageItem.class}, version = 2, exportSchema = false)
public abstract class ImageDatabase extends RoomDatabase {

    private static ImageDatabase INSTANCE;

    public abstract ImageItemDAO imageItemDAO();

    public static synchronized ImageDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ImageDatabase.class, "image_database.db")
                    .fallbackToDestructiveMigration()
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadExecutor().execute(() -> {
                                ImageDatabase database = getInstance(context);
                                ImageItemDAO dao = database.imageItemDAO();

                                dao.insertImage(new ImageItem("file:///android_asset/images/gulbil.jpg", "Gul Bil"));
                                dao.insertImage(new ImageItem("file:///android_asset/images/rodbil.jpg", "Rød Bil"));
                                dao.insertImage(new ImageItem("file:///android_asset/images/hvitbil.jpg", "Hvit bil"));
                            });
                        }
                    })
                    .build();
        }
        return INSTANCE;
    }
}
