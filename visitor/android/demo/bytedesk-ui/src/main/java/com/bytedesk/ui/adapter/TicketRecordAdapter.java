package com.bytedesk.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bytedesk.core.room.entity.TicketEntity;
import com.bytedesk.ui.R;

import java.util.List;

/**
 *
 * @author bytedesk.com
 */
public class TicketRecordAdapter extends RecyclerView.Adapter<TicketRecordAdapter.ViewHolder> {

    private List<TicketEntity> ticketEntities;

    public TicketRecordAdapter() {
//        mContext = context;
    }

    public void setTickets(List<TicketEntity> ticketEntityList) {
        if (null == ticketEntities) {
            ticketEntities = ticketEntityList;
            notifyItemRangeInserted(0, ticketEntityList.size());
        }
        else {
            ticketEntities = ticketEntityList;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return ticketEntities == null ? 0 : ticketEntities.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bytedesk_ticket_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setContact(ticketEntities.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mContentTextView;
//        private TextView mReplyTextView;

        private TicketEntity mTicketEntity;

        public ViewHolder(View itemView) {
            super(itemView);

            mContentTextView = itemView.findViewById(R.id.bytedesk_ticket_item_content);
//            mReplyTextView = itemView.findViewById(R.id.bytedesk_ticket_item_reply);
        }

        public void setContact(TicketEntity ticketEntity) {
            mTicketEntity = ticketEntity;

            mContentTextView.setText(mTicketEntity.getContent());
//            mReplyTextView.setText(mTicketEntity.);
        }

    }

}
