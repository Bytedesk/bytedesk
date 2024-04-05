package com.bytedesk.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bytedesk.core.room.entity.FeedbackEntity;
import com.bytedesk.ui.R;

import java.util.List;

/**
 *
 * @author bytedesk.com
 */
public class FeedbackRecordAdapter extends RecyclerView.Adapter<FeedbackRecordAdapter.ViewHolder> {

    private Context mContext;
    private List<FeedbackEntity> feedbackEntities;

    public FeedbackRecordAdapter(Context context) {
        mContext = context;
    }

    public void setFeedbacks(List<FeedbackEntity> feedbackEntityList) {
        if (null == feedbackEntities) {
            feedbackEntities = feedbackEntityList;
            notifyItemRangeInserted(0, feedbackEntityList.size());
        }
        else {
            feedbackEntities = feedbackEntityList;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return feedbackEntities == null ? 0 : feedbackEntities.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bytedesk_feedback_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setContact(feedbackEntities.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mContentTextView;
        private TextView mReplyTextView;

        private FeedbackEntity mFeedbackEntity;

        public ViewHolder(View itemView) {
            super(itemView);

            mContentTextView = itemView.findViewById(R.id.textview_title);
            mReplyTextView = itemView.findViewById(R.id.bytedesk_feedback_item_reply);

        }

        public void setContact(FeedbackEntity feedbackEntity) {
//            Logger.i("contact nickname: " + contact.getNickname());
            mFeedbackEntity = feedbackEntity;

            mContentTextView.setText(mFeedbackEntity.getContent());

            if (mFeedbackEntity.getReplyContent() == null ||
                    mFeedbackEntity.getReplyContent().equals("null")) {
                mReplyTextView.setText("暂未回复");
            } else {
                mReplyTextView.setText(mFeedbackEntity.getReplyContent());
            }
        }

    }

}
