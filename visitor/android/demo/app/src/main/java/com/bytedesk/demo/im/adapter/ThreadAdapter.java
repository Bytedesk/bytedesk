package com.bytedesk.demo.im.adapter;

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
import com.bytedesk.ui.util.BDUiUtils;

import java.util.List;

/**
 * Created by YOLANDA on 2016/7/22.
 */
public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

    private Context mContext;
    private List<ThreadEntity> mThreadList;

    public ThreadAdapter(Context context) {
        mContext = context;
    }

    public void setThreads(List<ThreadEntity> threadEntities) {
        if (null == mThreadList) {
            mThreadList = threadEntities;
            notifyItemRangeInserted(0, threadEntities.size());
        }
        else {
            mThreadList = threadEntities;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mThreadList == null ? 0 : mThreadList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ThreadEntity threadEntity = mThreadList.get(position);
//        Logger.i("is mark Top: " + threadEntity.isMarkTop() +
//                " is mark unread: " + threadEntity.isMarkUnread());

        int viewType = 0;
        if (threadEntity.isMarkTop() && threadEntity.isMarkUnread()) {
            viewType = ViewType.TOP_UNREAD.ordinal();
        } else if (threadEntity.isMarkTop() && !threadEntity.isMarkUnread()) {
            viewType = ViewType.TOP_READ.ordinal();
        } else if (!threadEntity.isMarkTop() && threadEntity.isMarkUnread()) {
            viewType = ViewType.UNTOP_UNREAD.ordinal();
        } else if (!threadEntity.isMarkTop() && !threadEntity.isMarkUnread()) {
            viewType = ViewType.UNTOP_READ.ordinal();
        }

//        Logger.i("viewType adapter: " + viewType);

        return viewType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setThread(mThreadList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNicknameTextView;
        private TextView mContentTextView;
        private TextView mTimestampTextView;
        private ImageView mAvatarImageView;
        private TextView mUnreadNumTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            mNicknameTextView = itemView.findViewById(R.id.textview_firstitem_nickname);
            mContentTextView = itemView.findViewById(R.id.textview_content);
            mTimestampTextView = itemView.findViewById(R.id.textview_timestamp);
            mAvatarImageView = itemView.findViewById(R.id.imageview_avatar);
            mUnreadNumTextView = itemView.findViewById(R.id.textview_unreadnum);
        }

        public void setThread(ThreadEntity thread) {

            mNicknameTextView.setText(thread.getNickname());

            if (thread.isTemp()) {
                mContentTextView.setText(thread.getContent()+"[临时会话]");
            } else {
                mContentTextView.setText(thread.getContent());
            }

            mTimestampTextView.setText(BDUiUtils.friendlyTime(thread.getTimestamp(), mContext));
            Glide.with(mContext).load(thread.getAvatar()).into(mAvatarImageView);

            if (thread.getUnreadCount() == 0) {
                mUnreadNumTextView.setVisibility(View.GONE);
            }

            if (thread.isMarkUnread()) {
                mUnreadNumTextView.setVisibility(View.VISIBLE);
            }

        }
    }

    public enum ViewType {
        TOP_UNREAD,
        TOP_READ,
        UNTOP_UNREAD,
        UNTOP_READ
    }

}
