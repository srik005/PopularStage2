package database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {FavouriteMovie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    public static final Object LOCK = new Object();

    private static MovieDatabase movieInstance;

    public static MovieDatabase getMovieInstance(final Context context) {

        if (movieInstance == null) {
            synchronized (LOCK) {
                movieInstance = Room.databaseBuilder(context.getApplicationContext(), MovieDatabase.class, "movie_database").build();
            }
        }
        return movieInstance;
    }

    public abstract MovieDao movieDao();
}
