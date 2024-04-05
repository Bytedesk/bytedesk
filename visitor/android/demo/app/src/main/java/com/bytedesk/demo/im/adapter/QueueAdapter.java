package com.bytedesk.demo.im.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedesk.demo.R;
import com.bytedesk.core.room.entity.QueueEntity;

import java.util.List;

/**
 * Created by YOLANDA on 2016/7/22.
 */
public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private Context mContext;
    private List<QueueEntity> mQueueList;

    public QueueAdapter(Context context) {
        mContext = context;
    }

    public void setQueues(List<QueueEntity> queueEntities) {
        if (null == mQueueList) {
            mQueueList = queueEntities;
            notifyItemRangeInserted(0, queueEntities.size());
        }
        else {
            mQueueList = queueEntities;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mQueueList == null ? 0 : mQueueList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setQueue(mQueueList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNicknameTextView;
        private TextView mContentTextView;
        private TextView mTimestampTextView;
        private ImageView mAvatarImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mNicknameTextView = itemView.findViewById(R.id.textview_firstitem_nickname);
//            mContentTextView = itemView.findViewById(R.id.textview_content);
            mTimestampTextView = itemView.findViewById(R.id.textview_timestamp);
            mAvatarImageView = itemView.findViewById(R.id.imageview_avatar);

        }

        public void setQueue(QueueEntity queue) {
            mNicknameTextView.setText(queue.getNickname());
            mTimestampTextView.setText(queue.getActionedAt());
            Glide.with(mContext).load(queue.getAvatar()).into(mAvatarImageView);
        }
    }

}
