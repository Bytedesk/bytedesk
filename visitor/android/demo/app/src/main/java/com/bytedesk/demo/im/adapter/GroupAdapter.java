package com.bytedesk.demo.im.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedesk.core.room.entity.GroupEntity;
import com.bytedesk.demo.R;

import java.util.List;

/**
 *
 * @author bytedesk.com
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context mContext;
    private List<GroupEntity> mGroupList;

    public GroupAdapter(Context context) {
        mContext = context;
    }

    public void setGroups(List<GroupEntity> groupEntities) {
        if (null == mGroupList) {
            mGroupList = groupEntities;
            notifyItemRangeInserted(0, groupEntities.size());
        }
        else {
            mGroupList = groupEntities;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mGroupList == null ? 0 : mGroupList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setGroup(mGroupList.get(position));
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
            mAvatarImageView = itemView.findViewById(R.id.imageview_avatar);
        }

        public void setGroup(GroupEntity group) {
//            Logger.i("contact nickname: " + contact.getNickname());

            mNicknameTextView.setText(group.getNickname());
            mContentTextView.setText(group.getDescription());
            Glide.with(mContext).load(group.getAvatar()).into(mAvatarImageView);
        }

    }

}
