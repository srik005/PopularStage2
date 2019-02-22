package retrofitreview;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewInterface {
    @GET("movie/{id}/reviews")
    Call<ReviewResults> getReviews(@Path("id") Integer id, @Query("api_key") String apiKey);
}
