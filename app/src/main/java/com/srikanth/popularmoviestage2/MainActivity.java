package com.srikanth.popularmoviestage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.ImageRecycleAdapter;
import database.FavoriteAdapter;
import database.FavouriteMovie;
import models.MovieViewModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.MovieResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ConstraintLayout constraintLayout;
    private RecyclerView mGridView;
    public static final String URL = "https://api.themoviedb.org/3/";
    private String mSort;
    private BroadcastReceiver broadcastReceiver;
    List<FavouriteMovie> mFavList;
    List<Movie> mMovieList;
    FavoriteAdapter favoriteAdapter;
    MovieViewModel viewModel;
    ImageRecycleAdapter imageRecycleAdapter;

    /**
     * Don't declare the receiver in the Manifest for app targeting API 26 & above
     * for listening to Network Connectivity Changes
     * the receiver will not be called
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.snackbarView);
        mGridView = findViewById(R.id.img_grid_view);
        GridLayoutManager lm = new GridLayoutManager(MainActivity.this, 2);
        mGridView.setLayoutManager(lm);
        mGridView.setHasFixedSize(true);
        mMovieList = new ArrayList<>();
        mFavList = new ArrayList<>();
        checkInternetConnection();
        mSort = "now_playing";
        imageRecycleAdapter = new ImageRecycleAdapter(mMovieList, this);
        mGridView.setAdapter(imageRecycleAdapter);
        imageRecycleAdapter.notifyDataSetChanged();
        getPosterPath(mSort);

    }

    private void checkInternetConnection() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        NetworkInfo networkInfo = extras.getParcelable("networkInfo");
                        if (networkInfo != null) {
                            NetworkInfo.State state = networkInfo.getState();
                            if (state == NetworkInfo.State.CONNECTED) {
                                //getPosterPath(mSort);
                                Log.d("connected internet", "connected internaet");
                                Snackbar.make(constraintLayout, "Internet is connected", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(constraintLayout, "No Connection", Snackbar.LENGTH_SHORT).show();
                                Log.d("no internet", "no internet");
                            }
                        }
                    }

                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }


    private void getPosterPath(String sort) {
        mGridView = findViewById(R.id.img_grid_view);
        GridLayoutManager lm = new GridLayoutManager(MainActivity.this, 2);
        mGridView.setLayoutManager(lm);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        //if (mSort.equals("top_rated") || mSort.equals("popular")) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        final MovieInterface movieInterface = retrofit.create(MovieInterface.class);
        final Call<MovieResponse> movieResponseCall = movieInterface.getNowPlaying(sort, "7eac19859fbd0741e0e038be3466e17b");
        movieResponseCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (response.body() != null && response.code() == 200) {
                    List<Movie> movieResponses = response.body().getResults();
                    Log.d("Response", "" + response.body());
                    Log.d("Total number of movies", "" + movieResponses.size());
                    //imageRecycleAdapter = new ImageRecycleAdapter(movieResponses, MainActivity.this);
                    mGridView.setAdapter(new ImageRecycleAdapter(movieResponses, MainActivity.this));
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topRated:
                Toast.makeText(MainActivity.this, "Top Rated", Toast.LENGTH_LONG).show();
                mSort = "top_rated";
                getPosterPath(mSort);
                break;
            case R.id.mostPopular:
                Toast.makeText(MainActivity.this, "Most Popular", Toast.LENGTH_LONG).show();
                mSort = "popular";
                getPosterPath(mSort);
                break;
            case R.id.favorite:
                Log.d("Inside Favorite", "Fav clicked");
                //  mSort = "favorite";
                Toast.makeText(MainActivity.this, "inside favorite", Toast.LENGTH_LONG).show();
                //  getPosterPath(mSort);
                setUpViewModel();
                break;
        }
        return true;
    }

    private void setUpViewModel() {
        Log.d("View Model", "view model");
        MovieViewModel movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
        movieViewModel.getMovies().observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovie> favouriteMovies) {

                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null) {
                    for (FavouriteMovie favMovie : favouriteMovies) {
                        Movie movie = new Movie();
                        movie.setId(favMovie.getId());
                        movie.setOverview(favMovie.getOverview());
                        movie.setTitle(favMovie.getTitle());
                        movie.setVote_average(favMovie.getVote_average());
                        movies.add(movie);
                    }
                    imageRecycleAdapter.setImage(movies);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
