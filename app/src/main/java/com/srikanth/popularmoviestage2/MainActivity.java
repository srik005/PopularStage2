package com.srikanth.popularmoviestage2;

import android.arch.lifecycle.MutableLiveData;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapter.ImageRecycleAdapter;
import database.FavViewModel;
import database.FavoriteAdapter;
import database.FavouriteMovie;
import models.MovieViewModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.MovieResponse;
import retrofit.RetrofitResult;
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
    List<RetrofitResult> mMovieList;
    FavoriteAdapter favoriteAdapter;
    MovieViewModel viewModel;
    ImageRecycleAdapter imageRecycleAdapter;
    MutableLiveData<List<FavViewModel>> mutableMovieList;

    /**
     * Don't declare the receiver in the Manifest for app targeting API 26 & above
     * for listening to Network Connectivity Changes
     * the receiver will not be called
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = findViewById(R.id.img_grid_view);
        constraintLayout = findViewById(R.id.snackbarView);
        LinearLayoutManager lm = new GridLayoutManager(this, 2);
        // lm.setOrientation(LinearLayoutManager.VERTICAL);
        mGridView.setHasFixedSize(true);
        mGridView.setLayoutManager(lm);
        mSort = "now_playing";
        mMovieList = new ArrayList<>();
        mFavList = new ArrayList<>();
        checkInternetConnection();
        FavViewModel movieViewModel = ViewModelProviders.of(this).get(FavViewModel.class);
        movieViewModel.getmAllmovies().observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovie> favouriteMovies) {
                if (mutableMovieList == null) {
                    mutableMovieList = new MutableLiveData<>();
                    loadMovies();
                }
            }
        });
    }

    private void loadMovies() {
        Handler mHandler = new Handler();

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
                                getPosterPath(mSort);
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
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        if (mSort.equals("top_rated") || mSort.equals("popular")) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(client).build();
            final MovieInterface movieInterface = retrofit.create(MovieInterface.class);
            final Call<MovieResponse> movieResponseCall = movieInterface.getNowPlaying(sort, "7eac19859fbd0741e0e038be3466e17b");
            movieResponseCall.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                    if (response.body() != null && response.code() == 200) {
                        List<RetrofitResult> movieResponses = response.body().getResults();
                        Log.d("Response", "" + response.body());
                        Log.d("Total number of movies", "" + movieResponses.size());
                        imageRecycleAdapter = new ImageRecycleAdapter(movieResponses, MainActivity.this);
                        mGridView.setAdapter(imageRecycleAdapter);
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {

                }
            });
        } else {
            mFavList = new ArrayList<>();
            for (int i = 0; i < mFavList.size(); i++) {
                RetrofitResult movie = new RetrofitResult(
                        mFavList.get(i).getVote_average(),
                        mFavList.get(i).getTitle(),
                        mFavList.get(i).getPoster_path(),
                        mFavList.get(i).getOverview(),
                        mFavList.get(i).getRelease_date()
                );
                mMovieList.add(movie);
            }
            mGridView.setAdapter(new ImageRecycleAdapter(new ArrayList<RetrofitResult>(), MainActivity.this));
            if (mMovieList != null)
                imageRecycleAdapter.setImage(mMovieList);
        }

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
                mSort = "favorite";
                getPosterPath(mSort);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
