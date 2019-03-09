package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.srikanth.popularmoviestage2.DetailActivity;
import com.srikanth.popularmoviestage2.Movie;
import com.srikanth.popularmoviestage2.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class ImageRecycleAdapter extends RecyclerView.Adapter<ImageRecycleAdapter.ImageViewHolder> {
    private List<Movie> imgUrls;
    private Context context;
    private LayoutInflater layoutInflater;
    public static String BASE_URL = "http://image.tmdb.org/t/p/w185";
    private Movie mMovie;

    public ImageRecycleAdapter(List<Movie> imageUrls, Context context) {
        this.imgUrls = imageUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageRecycleAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageRecycleAdapter.ImageViewHolder holder, int position) {
        final Movie retrofitResult = imgUrls.get(position);
        final String urlImage = BASE_URL + retrofitResult.getPoster_path();
        Log.d("imgUrl", "" + urlImage);
        Picasso.get().load(urlImage).placeholder(R.mipmap.ic_launcher).fit().into(holder.iv);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailActivity.class);
                mMovie = new Movie();
                mMovie.setId(retrofitResult.getId());
                mMovie.setPoster_path(retrofitResult.getPoster_path());
                mMovie.setOverview(retrofitResult.getOverview());
                mMovie.setTitle(retrofitResult.getTitle());
                mMovie.setVote_average(String.valueOf(retrofitResult.getVote_average()));
                mMovie.setRelease_date(retrofitResult.getRelease_date());
                Log.d("GetId", "" + retrofitResult.getId());
                Log.d("GetPoster", "" + retrofitResult.getPoster_path());
                Log.d("GetOverview", "" + retrofitResult.getOverview());
                Log.d("GetTitle", "" + retrofitResult.getTitle());
                Log.d("GetReleaseDate", "" + retrofitResult.getRelease_date());
                i.putExtra("backdrop", mMovie);
                context.startActivity(i);
            }
        });
    }

    public void setImage(List<Movie> favMovies) {
        imgUrls = favMovies;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        Log.d("Array Size", "" + imgUrls.size());
        return imgUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view)
        ImageView iv;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.image_view);
        }
    }
}
