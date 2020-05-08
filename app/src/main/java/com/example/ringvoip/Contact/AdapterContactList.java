package com.example.ringvoip.Contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ringvoip.R;

import java.util.ArrayList;

public class AdapterContactList extends RecyclerView.Adapter<AdapterContactList.ViewHolder> {
    ArrayList<ContactClass> dataContacts;
    Context context;
    ContactClickListener contactClickListener;

    public AdapterContactList(ArrayList<ContactClass> dataContacts, Context context) {
        this.context = context;
        this.dataContacts = dataContacts;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.list_contact, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    public void onBindViewHolder(@NonNull AdapterContactList.ViewHolder holder, int position) {
        holder.txtv_name.setText(dataContacts.get(position).getName());
        holder.txtv_status.setText(dataContacts.get(position).getStatus());

        if (holder.txtv_status.getText().toString().equals("Online")) {
            holder.txtv_status.setTextColor(ContextCompat.getColor(context, R.color.colorOnline));
        } else {
            holder.txtv_status.setTextColor(ContextCompat.getColor(context, R.color.colorOffline));
            holder.txtv_status.setTypeface(null, Typeface.ITALIC);
        }
    }

    public int getItemCount() {
        return dataContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtv_name;
        TextView txtv_status;
        LinearLayout ll_options;
        LinearLayout ll_contact;

        @SuppressLint("ResourceAsColor")
        ViewHolder(View itemView) {
            super(itemView);
            txtv_name = (TextView) itemView.findViewById(R.id.txtv_name);
            ll_options = (LinearLayout) itemView.findViewById(R.id.ll_options);
            ll_contact = (LinearLayout) itemView.findViewById(R.id.ll_contact);
            txtv_status = (TextView) itemView.findViewById(R.id.txtv_status);

            ll_contact.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (contactClickListener != null)
                contactClickListener.onContactClick(view, getAdapterPosition());
        }
    }


    public void setClickListener(ContactClickListener contactClickListener) {
        this.contactClickListener = contactClickListener;
    }

    public interface ContactClickListener {
        void onContactClick(View view, int position);
    }
}