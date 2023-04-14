package com.aditya.whatsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aditya.whatsapp.ChatDetailActivity;
import com.aditya.whatsapp.R;
import com.aditya.whatsapp.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    ArrayList<Users> list;
    Context context;

    public UserAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user=list.get(position);
        Picasso.get().load(user.getProfilePic()).placeholder(R.drawable.avatar).into(holder.user_profile_image);
        holder.chatUserName.setText(user.getUsername());

        FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getUid()+user.getUserId()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for (DataSnapshot ds:snapshot.getChildren()) {
                        holder.chatUserLastMessage.setText(ds.child("message").getValue().toString());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(view->{
            Intent intent=new Intent(context, ChatDetailActivity.class);
            intent.putExtra("userName",user.getUsername());
            intent.putExtra("userId",user.getUserId());
            intent.putExtra("profilePic",user.getProfilePic());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView user_profile_image;
        TextView chatUserName,chatUserLastMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_profile_image=itemView.findViewById(R.id.user_profile_image);
            chatUserName=itemView.findViewById(R.id.chatUserName);
            chatUserLastMessage=itemView.findViewById(R.id.chatUserLastMessage);
        }
    }
}
