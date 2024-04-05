package com.bytedesk.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedesk.core.room.entity.ContactEntity;
import com.bytedesk.ui.R;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 *
 * @author bytedesk.com
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {

    private Context mContext;
    private List<ContactEntity> mContactList;

    public SelectAdapter(Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bytedesk_contact_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setContact(mContactList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox mCheckBox;
        private TextView mNicknameTextView;
        private TextView mContentTextView;
        private ImageView mAvatarImageView;

        private ContactEntity mContactEntity;

        public ViewHolder(View itemView) {
            super(itemView);

            mCheckBox = itemView.findViewById(R.id.bytedesk_select_checkbox);
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 暂时禁用点击checkbox事件，否则需要添加listener
                    Logger.i(mCheckBox.isChecked() ? mContactEntity.getNickname() + "is checked"
                            : mContactEntity.getNickname() + "not checked");
                }
            });
            mNicknameTextView = itemView.findViewById(R.id.textview_firstitem_nickname);
            mContentTextView = itemView.findViewById(R.id.textview_content);
            mAvatarImageView = itemView.findViewById(R.id.imageview_avatar);
        }

        public void setContact(ContactEntity contact) {
//            Logger.i("contact nickname: " + contact.getNickname());
            mContactEntity = contact;

            mCheckBox.setChecked(contact.isSelected());
            mNicknameTextView.setText(contact.getRealName());
            mContentTextView.setText(contact.getDescription());
            Glide.with(mContext).load(contact.getAvatar()).into(mAvatarImageView);
        }

    }

}
