package com.bytedesk.demo.kefu.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedesk.core.room.entity.ThreadEntity;
import com.bytedesk.demo.R;

import java.util.List;


public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {


    private List<ThreadEntity> mThreadList;
    private Context mContext;

    public ThreadAdapter(Context context) {
        mContext = context;
    }

    public void setThreadList(List<ThreadEntity> threadList) {
        mThreadList = threadList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ThreadEntity thread = mThreadList.get(position);
        holder.setThread(thread);
    }

    @Override
    public int getItemCount() {
        //
        if (null != mThreadList)
            return mThreadList.size();
        //
        return 0;
    }

    //
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNicknameTextView;
        private TextView mContentTextView;
        private TextView mTimestampTextView;
        private ImageView mAvatarImageView;

        private ThreadEntity mThread;

        public ViewHolder(View itemView) {
            super(itemView);

            mNicknameTextView = itemView.findViewById(R.id.textview_firstitem_nickname);
            mContentTextView = itemView.findViewById(R.id.textview_content);
            mTimestampTextView = itemView.findViewById(R.id.textview_timestamp);
            mAvatarImageView = itemView.findViewById(R.id.imageview_avatar);
        }

        public void setThread(ThreadEntity thread) {
            mThread = thread;

            mNicknameTextView.setText(mThread.getNickname());
            mContentTextView.setText(mThread.getContent());
            mTimestampTextView.setText(mThread.getTimestamp());
            Glide.with(mContext).load(mThread.getAvatar()).into(mAvatarImageView);
        }


    }
}
