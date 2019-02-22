package database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.srikanth.popularmoviestage2.R;

import java.util.Collections;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavViewHolder> {
    List<FavouriteMovie> mFavMovie = Collections.emptyList();
    MovieDatabase movieDatabase;
    public static String BASE_URL = "http://image.tmdb.org/t/p/w185";
    LayoutInflater inflater;
    boolean isFav = false;
    FavViewModel favViewModel;

    public FavoriteAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FavoriteAdapter.FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View favView = inflater.inflate(R.layout.favorite_list, parent, false);
        return new FavViewHolder(favView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.FavViewHolder holder, final int position) {
        if (mFavMovie != null) {
            final FavouriteMovie addMovie = mFavMovie.get(position);
            final String favImage = BASE_URL + addMovie.getPoster_path();
            Picasso.get().load(favImage).fit().into(holder.favIView);
          /*  holder.favIView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    *//*fMovie.setId(addMovie.getId());
                    fMovie.setPoster_path(addMovie.getPoster_path());
                    fMovie.setTitle(addMovie.getTitle());
                    fMovie.setOverview(fMovie.getOverview());
                    fMovie.setRelease_date(fMovie.getRelease_date());.*//*
                    favViewModel.insert(addMovie.getOverview());
                }
            });*/
        }
    }

    public void setImage(List<FavouriteMovie> favMovies) {
        this.mFavMovie = favMovies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mFavMovie != null) {
            return mFavMovie.size();
        } else
            return 0;
    }

    public class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView favIView;

        public FavViewHolder(View itemView) {
            super(itemView);
            favIView = itemView.findViewById(R.id.favImagView);
        }
    }
}
