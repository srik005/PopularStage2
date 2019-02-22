package com.srikanth.popularmoviestage2;

import retrofit.MovieResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieInterface {
    @GET("movie/{now_playing}")
    Call<MovieResponse> getNowPlaying(@Path("now_playing") String nowPlaying, @Query("api_key") String apiKey);
}
