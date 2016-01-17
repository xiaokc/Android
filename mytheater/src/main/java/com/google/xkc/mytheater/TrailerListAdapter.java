package com.google.xkc.mytheater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by xkc on 1/16/16.
 */
public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.ViewHolder> {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ATrailer> mList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);

        void onItemLongClick(View v, int pos);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public TrailerListAdapter(Context context, List<ATrailer> list) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_movie_trailer, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ATrailer trailer = mList.get(position);

        holder.imageView.setImageResource(R.drawable.ic_video);
        holder.textView.setText("trailer " + (position + 1));


        if (mListener != null) {
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mListener.onItemLongClick(holder.itemView, pos);

                    return false;
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.trailer_play_iv);
            textView = (TextView) itemView.findViewById(R.id.trailer_play_tv);
        }
    }
}
