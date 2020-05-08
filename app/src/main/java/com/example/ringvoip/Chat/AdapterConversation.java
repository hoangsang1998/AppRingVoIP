package com.example.ringvoip.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ringvoip.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dytstudio.
 */

public class AdapterConversation extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<ChatMessageClass> chatMessages;
    private Context context;

    private final int ME = 0, YOU = 1, MISSEDCALLME = 2, MISSEDCALLYOU = 3, ENDCALLME = 4, ENDCALLYOU = 5, DECLINEDYOU = 6, DECLINEDME = 7;
    private String username;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterConversation(ArrayList<ChatMessageClass> chatMessages, Context context, String username) {
        this.context = context;
        this.username = username;
        this.chatMessages = chatMessages;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        //More to come
        if (chatMessages.get(position).getType() == null) {

            if (chatMessages.get(position).getUsername().equals(username)) {
                return ME;
            } else if (!chatMessages.get(position).getUsername().equals(username)) {
                return YOU;
            }


        } else if (chatMessages.get(position).getType().equals("Success")) {

            if (chatMessages.get(position).getUsername().equals(username)) {
                return ENDCALLME;
            } else if (!chatMessages.get(position).getUsername().equals(username)) {
                return ENDCALLYOU;
            }

        } else if (chatMessages.get(position).getType().equals("Declined")) {

            if (chatMessages.get(position).getUsername().equals(username)) {
                return DECLINEDME;
            } else if (!chatMessages.get(position).getUsername().equals(username)) {
                return DECLINEDYOU;
            }

        } else if (chatMessages.get(position).getType().equals("Missed")) {

            if (chatMessages.get(position).getUsername().equals(username)) {
                return MISSEDCALLME;
            } else if (!chatMessages.get(position).getUsername().equals(username)) {
                return MISSEDCALLYOU;
            }

        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ME:
                View v1 = inflater.inflate(R.layout.layout_message_me, parent, false);
                viewHolder = new HolderMe(v1);
                break;
            case YOU:
                View v2 = inflater.inflate(R.layout.layout_message_you, parent, false);
                viewHolder = new HolderYou(v2);
                break;
            case MISSEDCALLME:
                View v3 = inflater.inflate(R.layout.layout_missedcall_me, parent, false);
                viewHolder = new HolderMissedCallMe(v3);
                break;
            case MISSEDCALLYOU:
                View v4 = inflater.inflate(R.layout.layout_missedcall_you, parent, false);
                viewHolder = new HolderMissedCallYou(v4);
                break;
            case ENDCALLME:
                View v5 = inflater.inflate(R.layout.layout_endcall_me, parent, false);
                viewHolder = new HolderEndCallMe(v5);
                break;
            case ENDCALLYOU:
                View v6 = inflater.inflate(R.layout.layout_endcall_you, parent, false);
                viewHolder = new HolderEndCallYou(v6);
                break;
            case DECLINEDME:
                View v7 = inflater.inflate(R.layout.layout_declinedcall_me, parent, false);
                viewHolder = new HolderMissedCallMe(v7);
                break;
            default:
                View v8 = inflater.inflate(R.layout.layout_declinedcall_you, parent, false);
                viewHolder = new HolderMissedCallYou(v8);
                break;
        }
        return viewHolder;
    }

    public void addItem(List<ChatMessageClass> chatMessages) {
        this.chatMessages.addAll(chatMessages);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ME:
                HolderMe vh1 = (HolderMe) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case YOU:
                HolderYou vh2 = (HolderYou) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            case MISSEDCALLME:
                HolderMissedCallMe vh3 = (HolderMissedCallMe) viewHolder;
                configureViewHolder3(vh3, position);
                break;
            case MISSEDCALLYOU:
                HolderMissedCallYou vh4 = (HolderMissedCallYou) viewHolder;
                configureViewHolder4(vh4, position);
                break;
            case ENDCALLME:
                HolderEndCallMe vh5 = (HolderEndCallMe) viewHolder;
                configureViewHolder5(vh5, position);
                break;
            case ENDCALLYOU:
                HolderEndCallYou vh6 = (HolderEndCallYou) viewHolder;
                configureViewHolder6(vh6, position);
                break;
            case DECLINEDME:
                HolderMissedCallMe vh7 = (HolderMissedCallMe) viewHolder;
                configureViewHolder7(vh7, position);
                break;
            default:
                HolderMissedCallYou vh8 = (HolderMissedCallYou) viewHolder;
                configureViewHolder8(vh8, position);
                break;
        }
    }

    // normal messages
    private void configureViewHolder1(HolderMe vh1, int position) {
        vh1.getTime().setText(chatMessages.get(position).getDatetime());
        vh1.getMessage().setText(chatMessages.get(position).getContext());
    }

    private void configureViewHolder2(HolderYou vh1, int position) {
        vh1.getTime().setText(chatMessages.get(position).getDatetime());
        vh1.getMessage().setText(chatMessages.get(position).getContext());
    }

    // messages for missed calls
    @SuppressLint("SetTextI18n")
    private void configureViewHolder3(HolderMissedCallMe vh_me, int position) {
        vh_me.getTime().setText(chatMessages.get(position).getDatetime());
        vh_me.getMissedCall().setText("Call missed.");
    }

    @SuppressLint("SetTextI18n")
    private void configureViewHolder4(HolderMissedCallYou vh_you, int position) {
        vh_you.getTime().setText(chatMessages.get(position).getDatetime());
        vh_you.getMissedCall().setText("Call missed.");
    }

    // messages for ended calls
    @SuppressLint("SetTextI18n")
    private void configureViewHolder5(HolderEndCallMe vh_me, int position) {
        vh_me.getTime().setText(chatMessages.get(position).getDatetime());
        vh_me.getEndCall().setText("Call ended | " + chatMessages.get(position).getContext());
    }

    @SuppressLint("SetTextI18n")
    private void configureViewHolder6(HolderEndCallYou vh_you, int position) {
        vh_you.getTime().setText(chatMessages.get(position).getDatetime());
        vh_you.getEndCall().setText("Call ended | " + chatMessages.get(position).getContext());
    }

    // messages for declined calls
    @SuppressLint("SetTextI18n")
    private void configureViewHolder7(HolderMissedCallMe vh_me, int position) {
        vh_me.getTime().setText(chatMessages.get(position).getDatetime());
        vh_me.getMissedCall().setText("Call declined.");
    }

    @SuppressLint("SetTextI18n")
    private void configureViewHolder8(HolderMissedCallYou vh_you, int position) {
        vh_you.getTime().setText(chatMessages.get(position).getDatetime());
        vh_you.getMissedCall().setText("Call declined.");
    }

    public class HolderMe extends RecyclerView.ViewHolder {
        TextView txtv_time;
        TextView txtv_message;
        ImageView img;
        TextView txtv_name;

        public HolderMe(View itemView) {
            super(itemView);
            txtv_message = (TextView) itemView.findViewById(R.id.txtv_message_me);
            img = (ImageView) itemView.findViewById(R.id.img_friend);
            txtv_time = (TextView) itemView.findViewById(R.id.txtv_time);
        }

        public TextView getTime() {
            return txtv_time;
        }

        public TextView getMessage() {
            return txtv_message;
        }

        public TextView getName() {
            return txtv_name;
        }

        public ImageView getImg() {
            return img;
        }
    }

    public class HolderYou extends RecyclerView.ViewHolder {
        TextView txtv_time;
        TextView txtv_message;
        ImageView img;
        TextView txtv_name;

        public HolderYou(View itemView) {
            super(itemView);
            txtv_message = (TextView) itemView.findViewById(R.id.txtv_message_you);
            img = (ImageView) itemView.findViewById(R.id.img_friend);
            txtv_time = (TextView) itemView.findViewById(R.id.txtv_time);
        }

        public TextView getTime() {
            return txtv_time;
        }

        public TextView getMessage() {
            return txtv_message;
        }

        public TextView getName() {
            return txtv_name;
        }

        public ImageView getImg() {
            return img;
        }
    }

    public class HolderMissedCallMe extends RecyclerView.ViewHolder {
        TextView txtv_time;
        TextView txtv_missedcall_me;

        public HolderMissedCallMe(View itemView) {
            super(itemView);
            txtv_time = (TextView) itemView.findViewById(R.id.txtv_time);
            txtv_missedcall_me = (TextView) itemView.findViewById(R.id.txtv_missedcall_me);
        }

        public TextView getTime() {
            return txtv_time;
        }

        public void setTime(TextView txtv_time) {
            this.txtv_time = txtv_time;
        }

        public TextView getMissedCall() {
            return txtv_missedcall_me;
        }

        public void setMissedCall(TextView txtv_missedcall_me) {
            this.txtv_missedcall_me = txtv_missedcall_me;
        }
    }

    public class HolderMissedCallYou extends RecyclerView.ViewHolder {
        TextView txtv_time;
        TextView txtv_missedcall_you;

        public HolderMissedCallYou(View itemView) {
            super(itemView);
            txtv_time = (TextView) itemView.findViewById(R.id.txtv_time);
            txtv_missedcall_you = (TextView) itemView.findViewById(R.id.txtv_missedcall_you);
        }

        public TextView getTime() {
            return txtv_time;
        }

        public void setTime(TextView txtv_time) {
            this.txtv_time = txtv_time;
        }

        public TextView getMissedCall() {
            return txtv_missedcall_you;
        }

        public void setMissedCall(TextView txtv_missedcall_you) {
            this.txtv_missedcall_you = txtv_missedcall_you;
        }
    }

    public class HolderEndCallMe extends RecyclerView.ViewHolder {
        TextView txtv_time;
        TextView txtv_endcall_me;

        public HolderEndCallMe(View itemView) {
            super(itemView);
            txtv_time = (TextView) itemView.findViewById(R.id.txtv_time);
            txtv_endcall_me = (TextView) itemView.findViewById(R.id.txtv_endcall_me);
        }

        public TextView getTime() {
            return txtv_time;
        }

        public void setTime(TextView txtv_time) {
            this.txtv_time = txtv_time;
        }

        public TextView getEndCall() {
            return txtv_endcall_me;
        }

        public void setEndCall(TextView txtv_endcall_me) {
            this.txtv_endcall_me = txtv_endcall_me;
        }
    }

    public class HolderEndCallYou extends RecyclerView.ViewHolder {
        TextView txtv_time;
        TextView txtv_endcall_you;

        public HolderEndCallYou(View itemView) {
            super(itemView);
            txtv_time = (TextView) itemView.findViewById(R.id.txtv_time);
            txtv_endcall_you = (TextView) itemView.findViewById(R.id.txtv_endcall_you);
        }

        public TextView getTime() {
            return txtv_time;
        }

        public void setTime(TextView txtv_time) {
            this.txtv_time = txtv_time;
        }

        public TextView getEndCall() {
            return txtv_endcall_you;
        }

        public void setEndCall(TextView txtv_endcall_you) {
            this.txtv_endcall_you = txtv_endcall_you;
        }
    }

}