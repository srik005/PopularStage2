package com.srikanth.popularmoviestage2;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import adapter.ImageAdapter;
import adapter.ReviewAdapter;
import adapter.TrailerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import database.FavViewModel;
import database.FavouriteMovie;
import database.MovieDatabase;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit.RetrofitResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofitreview.ReviewInterface;
import retrofitreview.ReviewModel;
import retrofitreview.ReviewResults;
import retrofittrailer.TrailerInterface;
import retrofittrailer.TrailerModel;
import retrofittrailer.TrailerResults;


public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.txtTitle)
    TextView mTxtTitle;

    @BindView(R.id.txtReleaseDate)
    TextView mReleaseDate;

    @BindView(R.id.txtVoteAverage)
    TextView mTxtVoteAverage;

    @BindView(R.id.txtSynopsis)
    TextView mTxtOverview;

    @BindView(R.id.poster_path)
    ImageView mImageView;

    boolean isButtonClicked = false;
    MovieDatabase movieDatabase;
    Movie movie;
    ImageView favoImageView;
    RetrofitResult movieList;
    FavViewModel favViewModel;
    FavouriteMovie favouriteMovie;
    public static final String URL = "https://api.themoviedb.org/3/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        favoImageView = findViewById(R.id.favButton);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        movie = getIntent().getParcelableExtra("backdrop");
        movieDatabase = MovieDatabase.getMovieInstance(this);

        String overView = movie.getOverview();
        mTxtOverview.setText(overView);
        Log.d("overview", "" + overView);

        String title = movie.getTitle();
        Log.d("title", "" + title);
        mTxtTitle.setText(title);

        String voteAverage = movie.getVote_average();
        Log.d("average", "" + voteAverage);
        mTxtVoteAverage.setText(voteAverage);

        String releaseDate = movie.getRelease_date();
        Log.d("release date", "" + releaseDate);
        mReleaseDate.setText(releaseDate);

        getReviews();
        getTrailerLinks();
        final String mImageUrl = movie.getPoster_path();
        Picasso.get().load(ImageAdapter.BASE_URL + mImageUrl).placeholder(R.mipmap.ic_launcher).into(mImageView);
        Log.d("imgUrl", "" + movie.getPoster_path());
        //favViewModel = ViewModelProviders.of(this).get(FavViewModel.class);
        // getFavorites();
        favViewModel = ViewModelProviders.of(this).get(FavViewModel.class);
        favViewModel.getmAllmovies().observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovie> favouriteMovies) {
                if (favouriteMovies != null && favouriteMovies.size() > 0) {
                    ///   favoriteAdapter.setImage(favouriteMovies);
                }
            }
        });
        /*favoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FavouriteMovie favouriteMovie = new FavouriteMovie();
                favouriteMovie.setId(movieList.getId());
                favouriteMovie.setOverview(movieList.getOverview());
                favouriteMovie.setRelease_date(movieList.getReleaseDate());
                favouriteMovie.setVote_average(movieList.getVoteAverage());
                favouriteMovie.setPoster_path(movieList.getPosterPath());
                favouriteMovie.setTitle(movieList.getTitle());
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isButtonClicked) {
                            movieDatabase.movieDao().delete(favouriteMovie);
                            //favoImageView.setImageResource(R.drawable.t4);
                            Log.d("Delete Movie", "" + "Movie Removed from the list");
                        } else {
                            movieDatabase.movieDao().insert(favouriteMovie);
                            *//**
         android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
         favoImageView.setImageResource(R.drawable.baseline_favorite_white_24dp);
         dont update UI here
         *
         ***//*
                            Log.d("Insert Movie", "" + "Movie Inserted into the list");
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (isButtonClicked) {
                                    favoImageView.setImageResource(R.drawable.t4);
                                } else
                                    favoImageView.setImageResource(R.drawable.baseline_favorite_white_24dp);
                            }
                        });
                    }
                });
            }
        });*/
        favoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isButtonClicked) {
                    favouriteMovie = new FavouriteMovie();
                    favouriteMovie.setId(movie.getId());
                    favouriteMovie.setPoster_path(movie.getPoster_path());
                    favouriteMovie.setRelease_date(movie.getRelease_date());
                    favouriteMovie.setOverview(movie.getOverview());
                    favouriteMovie.setVote_average(movie.getVote_average());
                    favViewModel.insert(favouriteMovie);
                    Log.d("inserted items", "" + favouriteMovie.toString());
                }/* else {
                    favViewModel.delete(favouriteMovie);
                    Log.d("Deleted items", "" + favouriteMovie);
                }*/
            }
        });
    }

   /* private void getFavorites() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                FavouriteMovie favouriteMovie = movieDatabase.movieDao().getMovieId(movie.getId());
                if (favouriteMovie != null) {
                    if (isButtonClicked) {
                        favoImageView.setImageResource(R.drawable.baseline_favorite_white_24dp);
                    } else {
                        favoImageView.setImageResource(R.drawable.t4);
                    }
                }
            }
        });
    }*/

    private void getTrailerLinks() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        final RecyclerView trailerRecylerview = findViewById(R.id.trailerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        trailerRecylerview.setHasFixedSize(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        trailerRecylerview.setLayoutManager(linearLayoutManager);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        final TrailerInterface trailerInterface = retrofit.create(TrailerInterface.class);
        final Call<TrailerResults> reviewResponseCall = trailerInterface.getTrailer(movie.getId(), "7eac19859fbd0741e0e038be3466e17b");
        reviewResponseCall.enqueue(new Callback<TrailerResults>() {
            @Override
            public void onResponse(@NonNull Call<TrailerResults> call, @NonNull Response<TrailerResults> response) {
                if (response.body() != null && response.code() == 200) {
                    List<TrailerModel> reviewResponses = response.body().getResults();
                    Log.d("Response", "" + response.body());
                    Log.d("Total number of movies", "" + reviewResponses.size());
                    trailerRecylerview.setAdapter(new TrailerAdapter(reviewResponses));
                }
            }

            @Override
            public void onFailure(Call<TrailerResults> call, Throwable t) {

            }
        });
    }

    private void getReviews() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        final RecyclerView reviewRecylerview = findViewById(R.id.reviewView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        reviewRecylerview.setHasFixedSize(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reviewRecylerview.setLayoutManager(linearLayoutManager);


        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        final ReviewInterface reviewInterface = retrofit.create(ReviewInterface.class);
        final Call<ReviewResults> reviewResponseCall = reviewInterface.getReviews(movie.getId(), "7eac19859fbd0741e0e038be3466e17b");
        reviewResponseCall.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(@NonNull Call<ReviewResults> call, @NonNull Response<ReviewResults> response) {
                if (response.body() != null && response.code() == 200) {
                    List<ReviewModel> reviewResponses = response.body().getResults();
                    Log.d("Response", "" + response.body());
                    Log.d("Total number of movies", "" + reviewResponses.size());
                    reviewRecylerview.setAdapter(new ReviewAdapter(reviewResponses));
                }
            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {
            }
        });
    }

}
