package retrofittrailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TrailerInterface {
    @GET("movie/{id}/videos")
    Call<TrailerResults> getTrailer(@Path("id") Integer id,@Query("api_key") String key);
}
