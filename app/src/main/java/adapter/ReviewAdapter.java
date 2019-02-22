package adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.srikanth.popularmoviestage2.R;

import java.util.List;

import retrofitreview.ReviewModel;


public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewModel> mReviewList;

    public ReviewAdapter(List<ReviewModel> reviewResponses) {
        this.mReviewList = reviewResponses;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        ReviewModel reviewModel = mReviewList.get(position);
        String author = reviewModel.getAuthor();
        String content = reviewModel.getContent();
        holder.mAuthorTv.setText("Author" + author);
        holder.mContentTv.setText(content);

    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mAuthorTv;
        TextView mContentTv;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorTv = itemView.findViewById(R.id.authorText);
            mContentTv = itemView.findViewById(R.id.contentText);

        }
    }
}
