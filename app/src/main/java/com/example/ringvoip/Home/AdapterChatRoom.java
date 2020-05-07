package com.example.ringvoip.Home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ringvoip.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterChatRoom extends RecyclerView.Adapter<AdapterChatRoom.ViewHolder> {
    ArrayList<ChatRoomClass> chatRoomClasses;
    Context context;
    DataChatClickListener dataChatClickListener;
    String username_from_home;

    public static String getUserContact(ChatRoomClass chatroom, String username_from_home) {
        if (chatroom.getUsername1().equals(username_from_home)) {
            return chatroom.getUsername2();
        } else return chatroom.getUsername1();
    }

    public AdapterChatRoom(ArrayList<ChatRoomClass> chatRoomClasses, Context context, String username) {
        this.chatRoomClasses = chatRoomClasses;
        this.context = context;
        this.username_from_home = username;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.list_chat, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String context = chatRoomClasses.get(position).getContext();
        if (chatRoomClasses.get(position).getUsername1().equals(username_from_home)) {
            context = "Bạn: " + context;
        } else {
            context = chatRoomClasses.get(position).getUsername1() + ": " + context;
        }
        if (context.length() > 22) {
            context = context.substring(0, 22) + "...";
        }
        holder.txtv_chat_friend.setText(context);
        holder.txtv_name_friend.setText(getUserContact(chatRoomClasses.get(position), username_from_home));

        // change the datetime format to display
        Date dateline = new Date();
        SimpleDateFormat formatterNew = new SimpleDateFormat("dd-MM-yyyy | HH:mm", Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd | HH:mm:ss", Locale.getDefault());
        try {
            dateline = format.parse(chatRoomClasses.get(position).getDatetime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert dateline != null;
        String dateTime = formatterNew.format(dateline);
        holder.txtv_time_friend.setText(dateTime);
    }

    public int getItemCount() {
        return chatRoomClasses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtv_time_friend;
        TextView txtv_chat_friend;
        ImageView img_friend;
        TextView txtv_name_friend;

        public ViewHolder(View itemView) {
            super(itemView);
            txtv_chat_friend = (TextView) itemView.findViewById(R.id.txtv_chat_friend);
            img_friend = (ImageView) itemView.findViewById(R.id.img_friend);
            txtv_name_friend = (TextView) itemView.findViewById(R.id.txtv_name_friend);
            txtv_time_friend = (TextView) itemView.findViewById(R.id.txtv_time_friend);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (dataChatClickListener != null)
                dataChatClickListener.onDataChatClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(DataChatClickListener dataChatClickListener) {
        this.dataChatClickListener = dataChatClickListener;
    }

    public interface DataChatClickListener {
        void onDataChatClick(View view, int position);
    }
}
