package no.hvl.quizappvol2;

import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import no.hvl.quizappvol2.DAO.ImageItemDAO;

@Database(entities = {ImageItem.class}, version = 2, exportSchema = false)
public abstract class ImageDatabase extends RoomDatabase {

    private static ImageDatabase INSTANCE;

    public abstract ImageItemDAO imageItemDAO();

    public static synchronized ImageDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ImageDatabase.class, "image_database.db")
                    .createFromAsset("image_database.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}