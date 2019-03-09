package database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/*https://stackoverflow.com/questions/45789325/update-some-specific-field-of-an-entity-in-android-room*/

@Dao
public interface MovieDao {
    @Query("SELECT * FROM FavoriteMovie ORDER BY id ")
    LiveData<List<FavouriteMovie>> getAll();

    @Query("SELECT * FROM FavoriteMovie WHERE title = :title")
    List<FavouriteMovie> loadAll(String title);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavouriteMovie movie);

    @Update
    void update(FavouriteMovie movie);

    @Delete
    void delete(FavouriteMovie movie);

    @Query("SELECT*FROM FavoriteMovie WHERE id= :id")
    FavouriteMovie getMovieId(int id);
}
