package com.google.xkc.mytheater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by xkc on 1/14/16.
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder>{
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AMovie> mList;
    private final String LOG_TAG = "MovieListAdapter";
    private RecyclerView mRecyclerView;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
        void onItemLongClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public MovieListAdapter(Context context, List<AMovie> list){
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.item_movie,viewGroup,false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        AMovie movie = mList.get(i);
        String poster_path = movie.getPoster_path();

//        Log.i(LOG_TAG,"poster_path="+poster_path);

        //use Picasso to load image
        Picasso.with(mContext).load(poster_path).into(viewHolder.imageView);


        //If there is a item click event
        if (mListener != null){
           viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   int pos = viewHolder.getLayoutPosition();
                   mListener.onItemClick(viewHolder.itemView, pos);
               }
           });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = viewHolder.getLayoutPosition();
                    mListener.onItemLongClick(viewHolder.itemView,pos);
                    return false;
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageview_poster_item_movie);
        }
    }
}
