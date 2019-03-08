package com.srikanth.popularmoviestage2;

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
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import adapter.ImageAdapter;
import adapter.ReviewAdapter;
import adapter.TrailerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
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
    FavouriteMovie favouriteMovie;
    List<FavouriteMovie> favList;
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

        final String title = movie.getTitle();
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

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                favList = movieDatabase.movieDao().loadAll(title);
                setFavorites(favList != null);

            }
        });

        favoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favouriteMovie = new FavouriteMovie();
                favouriteMovie.setId(movie.getId());
                favouriteMovie.setPoster_path(movie.getPoster_path());
                favouriteMovie.setRelease_date(movie.getRelease_date());
                favouriteMovie.setOverview(movie.getOverview());
                favouriteMovie.setVote_average(movie.getVote_average());
                Log.d("inserted items", "" + favouriteMovie.toString());

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        final String msg;
                        if (isButtonClicked) {
                            msg = "Removed from favorites";
                            MovieDatabase.getMovieInstance(DetailActivity.this).movieDao().delete(favouriteMovie);
                        } else {
                            MovieDatabase.getMovieInstance(DetailActivity.this).movieDao().insert(favouriteMovie);
                            msg = "Insert into favorites";
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DetailActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

        });
    }

    private void setFavorites(boolean favorite) {
        if(favorite){
            isButtonClicked=true;
            favoImageView.setImageResource(R.drawable.button_selected);
        }
        else{
            isButtonClicked=false;
            favoImageView.setImageResource(R.drawable.button_nott_selected);
        }
    }


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
