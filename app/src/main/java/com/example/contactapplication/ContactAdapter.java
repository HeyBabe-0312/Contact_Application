package com.example.contactapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private ArrayList<Contact > contacts;
    private OnNoteListener mOnNoteListener; //
    public ContactAdapter(ArrayList<Contact> contacts, OnNoteListener onNoteListener) {
        this.contacts = contacts;
        this.mOnNoteListener = onNoteListener; //
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView tv;
        private ImageView img;
        OnNoteListener onNoteListener; //
        public ViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            img = itemView.findViewById(R.id.img_ava);
            this.onNoteListener = onNoteListener; //

            itemView.setOnClickListener(this);
        }
        public TextView getTv(){
            return tv;
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        } //
    }
    public interface OnNoteListener{
        void onNoteClick(int position); //
    }
    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);
        return new ViewHolder(v, mOnNoteListener); //
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.tv.setText(contact.getName());

        byte[] avaImage = contact.getImg();
        if(avaImage!=null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(avaImage,0,avaImage.length);
            holder.img.setImageBitmap(bitmap);
        }else holder.img.setImageResource(R.drawable.ic_person);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
