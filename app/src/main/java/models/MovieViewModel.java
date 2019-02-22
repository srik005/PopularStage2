package models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import database.FavouriteMovie;
import database.MovieDatabase;

public class MovieViewModel extends AndroidViewModel {
    LiveData<List<FavouriteMovie>> movies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        MovieDatabase movieDatabase = MovieDatabase.getMovieInstance(this.getApplication());
        movies = movieDatabase.movieDao().getAll();
    }

    public LiveData<List<FavouriteMovie>> getMovies() {
        return movies;
    }
}
