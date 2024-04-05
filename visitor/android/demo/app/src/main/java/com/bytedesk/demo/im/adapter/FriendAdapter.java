package com.bytedesk.demo.im.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedesk.core.room.entity.FriendEntity;
import com.bytedesk.demo.R;

import java.util.List;

/**
 *
 * @author bytedesk.com
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private Context mContext;
    private List<FriendEntity> mFriendList;

    public FriendAdapter(Context context) {
        mContext = context;
    }

    public void setFriends(List<FriendEntity> friendEntities) {
        if (null == mFriendList) {
            mFriendList = friendEntities;
            notifyItemRangeInserted(0, friendEntities.size());
        }
        else {
            mFriendList = friendEntities;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mFriendList == null ? 0 : mFriendList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setFriend(mFriendList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNicknameTextView;
        private TextView mContentTextView;
//        private TextView mTimestampTextView;
        private ImageView mAvatarImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mNicknameTextView = itemView.findViewById(R.id.textview_firstitem_nickname);
            mContentTextView = itemView.findViewById(R.id.textview_content);
//            mTimestampTextView = itemView.findViewById(R.id.textview_timestamp);
            mAvatarImageView = itemView.findViewById(R.id.imageview_avatar);
        }

        public void setFriend(FriendEntity friend) {

            mNicknameTextView.setText(friend.getRealName());
            mContentTextView.setText(friend.getDescription());
            Glide.with(mContext).load(friend.getAvatar()).into(mAvatarImageView);
        }

    }

}
