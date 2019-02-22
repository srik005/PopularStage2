package database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import java.util.List;

public class FavViewModel extends AndroidViewModel {
    private MovieDao movieDao;
    private LiveData<List<FavouriteMovie>> mAllmovies;
    MovieDatabase movieDatabase;

    public FavViewModel(Application application) {
        super(application);
        movieDatabase = MovieDatabase.getMovieInstance(application);
    }

    public LiveData<List<FavouriteMovie>> getmAllmovies() {
        if (mAllmovies == null) {
            mAllmovies = new MutableLiveData<>();
            loadFavMovies();
        }
        return mAllmovies;
    }

    private void loadFavMovies() {
        mAllmovies = movieDatabase.movieDao().getAll();
    }

    public void insert(FavouriteMovie movie) {
        new insertMovieAsyncTask(movieDatabase).execute(movie);

    }

    public void delete(FavouriteMovie movie) {
        new deleteMovieAsyncTask(movieDatabase).execute(movie);
    }

    private static class insertMovieAsyncTask extends AsyncTask<FavouriteMovie, Void, Void> {
        public MovieDatabase insertDB;

        public insertMovieAsyncTask(MovieDatabase movieDB) {
            insertDB = movieDB;

        }

        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            insertDB.movieDao().insert(favouriteMovies[0]);
            return null;
        }
    }

    private static class deleteMovieAsyncTask extends AsyncTask<FavouriteMovie, Void, Void> {
        public MovieDatabase deleteDB;

        public deleteMovieAsyncTask(MovieDatabase movieDB) {
            deleteDB = movieDB;
        }

        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            deleteDB.movieDao().delete(favouriteMovies[0]);
            return null;
        }
    }
}
