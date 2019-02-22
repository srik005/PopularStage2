package adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.srikanth.popularmoviestage2.R;

import java.util.List;

import retrofittrailer.TrailerModel;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<TrailerModel> mTrailerList;

    public TrailerAdapter(List<TrailerModel> mTrailerList) {
        this.mTrailerList = mTrailerList;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        final TrailerModel trailerModel = mTrailerList.get(position);
        String name = trailerModel.getName();

        holder.mBtn.setText(name);
        holder.mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playIntentnew = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailerModel.getKey()));
                view.getContext().startActivity(playIntentnew);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        Button mBtn;

        @SuppressLint("SetJavaScriptEnabled")
        public TrailerViewHolder(View itemView) {
            super(itemView);
            mBtn = itemView.findViewById(R.id.trailerButton);
            /*mBtn.getSettings().setJavaScriptEnabled(true);
            mBtn.setWebChromeClient(new WebChromeClient());*/
        }

    }

}
