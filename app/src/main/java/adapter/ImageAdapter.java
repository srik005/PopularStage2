package adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.srikanth.popularmoviestage2.DetailActivity;
import com.srikanth.popularmoviestage2.Movie;
import com.srikanth.popularmoviestage2.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RetrofitResult;

public class ImageAdapter extends BaseAdapter {
    private List<RetrofitResult> imageUrls;
    private Context context;
    private LayoutInflater layoutInflater;
    public static String BASE_URL = "http://image.tmdb.org/t/p/w185";
    private RetrofitResult result;
    private Movie mMovie;

    public ImageAdapter(List<RetrofitResult> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ImageViewHolder viewHolder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.grid_view_item, parent, false);
            viewHolder = new ImageViewHolder(view);
            view.setTag(viewHolder);
        } else {
            view.setTag(imageUrls.get(position));
            viewHolder = (ImageViewHolder) view.getTag();
        }
        result = imageUrls.get(position);
        final String urlImage = BASE_URL + result.getPosterPath();
        Log.d("imgUrl", "" + urlImage);
        Picasso.get().load(urlImage).placeholder(R.mipmap.ic_launcher).fit().into(viewHolder.iv);
        return view;
    }

    class ImageViewHolder {
        @BindView(R.id.image_view)
        ImageView iv;

        public ImageViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.image_view)
        public void onClick(View v) {
            int position = (int) v.getTag();
            Intent i = new Intent(context, DetailActivity.class);
            mMovie = new Movie();
            mMovie.setId(imageUrls.get(position).getId());
            mMovie.setPoster_path(imageUrls.get(position).getPosterPath());
            mMovie.setOverview(imageUrls.get(position).getOverview());
            mMovie.setTitle(imageUrls.get(position).getTitle());
            mMovie.setVote_average(String.valueOf(imageUrls.get(position).getVoteAverage()));
            mMovie.setRelease_date(imageUrls.get(position).getReleaseDate());
            Log.d("GetId", "" + imageUrls.get(position).getId());
            Log.d("GetPoster", "" + imageUrls.get(position).getPosterPath());
            Log.d("GetOverview", "" + imageUrls.get(position).getOverview());
            Log.d("GetTitle", "" + imageUrls.get(position).getTitle());
            Log.d("GetReleaseDate", "" + imageUrls.get(position).getReleaseDate());
            i.putExtra("backdrop", mMovie);
            context.startActivity(i);
        }
    }
}
