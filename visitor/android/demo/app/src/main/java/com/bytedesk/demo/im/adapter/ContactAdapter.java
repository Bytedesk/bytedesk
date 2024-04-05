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
import com.bytedesk.core.room.entity.ContactEntity;

import java.util.List;

/**
 *
 * @author bytedesk.com
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context mContext;
    private List<ContactEntity> mContactList;

    public ContactAdapter(Context context) {
        mContext = context;
    }

    public void setContacts(List<ContactEntity> contactEntities) {
        if (null == mContactList) {
            mContactList = contactEntities;
            notifyItemRangeInserted(0, contactEntities.size());
        }
        else {
            mContactList = contactEntities;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mContactList == null ? 0 : mContactList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setContact(mContactList.get(position));
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

        public void setContact(ContactEntity contact) {

            mNicknameTextView.setText(contact.getNickname());
            mContentTextView.setText(contact.getDescription());
            Glide.with(mContext).load(contact.getAvatar()).into(mAvatarImageView);
        }

    }

}
