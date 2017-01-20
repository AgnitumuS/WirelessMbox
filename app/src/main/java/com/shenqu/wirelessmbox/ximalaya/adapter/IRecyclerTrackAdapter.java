package com.shenqu.wirelessmbox.ximalaya.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.shenqu.wirelessmbox.R;
import com.shenqu.wirelessmbox.tools.JLUtils;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import org.xutils.x;

import java.util.List;

/**
 * 发现页 配置的 分类维度 -- ListView
 */
public class IRecyclerTrackAdapter extends RecyclerView.Adapter<IViewHolder> {

    private List<Track> mList;
    private OnItemClickListener<Track> mOnItemClickListener;

    private class ViewHolder  extends IViewHolder{
        ViewGroup content;
        ImageView ivCover;
        TextView tvIntro;
        TextView tvCacl;
        TextView tvDuration;
        TextView tvUpdateTime;

        ViewHolder (View view) {
            super(view);
            content = (ViewGroup) view;
            ivCover = (ImageView) view.findViewById(R.id.ivCover);
            tvIntro = (TextView) view.findViewById(R.id.tvIntro);
            tvCacl = (TextView) view.findViewById(R.id.tvCacl);
            tvDuration = (TextView) view.findViewById(R.id.tvDuration);
            tvUpdateTime = (TextView) view.findViewById(R.id.tvUpdateTime);
        }
    }

    public IRecyclerTrackAdapter(List<Track> datas) {
        mList = datas;
    }

    public void setOnItemClickListener(OnItemClickListener<Track> listener) {
        this.mOnItemClickListener = listener;
    }

    public Track getItem(int position) {
        if (mList != null && mList.size() > 0)
            return mList.get(position);
        else
            return null;
    }

    @Override
    public IViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.xm_item_meta_track, parent, false);
        final ViewHolder  holder = new ViewHolder (view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Note:
                 * in order to get the right position, you must use the method with i- prefix in
                 * {@link IViewHolder} eg:
                 * {@code IViewHolder.getIPosition()}
                 * {@code IViewHolder.getILayoutPosition()}
                 * {@code IViewHolder.getIAdapterPosition()}
                 */
                final int position = holder.getIAdapterPosition();
                final Track track= mList.get(position);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, track, v);
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(IViewHolder holde, int position) {
        Track track = getItem(position);
        ViewHolder  holder = (ViewHolder ) holde;
        if (track != null) {
            if (track.getTrackIntro() == null || track.getTrackIntro().length() == 0)
                holder.tvIntro.setText(track.getTrackTitle());
            else
                holder.tvIntro.setText(track.getTrackIntro());
            long count = track.getPlayCount();
            if (count > 10000) {
                holder.tvCacl.setText("" + count / 10000 + "万");
            } else {
                holder.tvCacl.setText("" + count);
            }

            String str = JLUtils.formatMediaTime(track.getDuration());
            holder.tvDuration.setText(str);

            String up = formatMsToDate(System.currentTimeMillis() - track.getCreatedAt());
            if (up.equals("0"))
                holder.tvUpdateTime.setText("今天");
            else
                holder.tvUpdateTime.setText(up + "天前");
            x.image().bind(holder.ivCover, track.getCoverUrlSmall());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    private String formatMsToDate(long ms) {
        long sec = ms / 1000;
        return String.valueOf(sec / (24 * 60 * 60));
    }
}