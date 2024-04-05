package com.bytedesk.demo.im.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedesk.core.room.entity.NoticeEntity;
import com.bytedesk.demo.R;

import java.util.List;

/**
 *
 * @author bytedesk.com
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    private Context mContext;
    private List<NoticeEntity> mNoticeList;

    public NoticeAdapter(Context context) {
        mContext = context;
    }

    public void setNotices(List<NoticeEntity> noticeEntities) {
        if (null == mNoticeList) {
            mNoticeList = noticeEntities;
            notifyItemRangeInserted(0, noticeEntities.size());
        }
        else {
            mNoticeList = noticeEntities;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mNoticeList == null ? 0 : mNoticeList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setNotice(mNoticeList.get(position));
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

        public void setNotice(NoticeEntity notice) {

            mNicknameTextView.setText(notice.getTitle());
            mContentTextView.setText(notice.getContent());
        }

    }

}
