package com.aditya.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.aditya.whatsapp.adapter.ChatAdapter;
import com.aditya.whatsapp.databinding.ActivityChatDetailBinding;
import com.aditya.whatsapp.models.MessageModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

       final  String senderId=auth.getUid();
         String receiverId=getIntent().getStringExtra("userId");
         String userName=getIntent().getStringExtra("userName");
         String profilePic=getIntent().getStringExtra("profilePic");

         binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage);


        binding.backArrow.setOnClickListener(view->{
         finish();
        });

        final ArrayList<MessageModel> messageModels=new ArrayList<>();
        final ChatAdapter chatAdapter=new ChatAdapter(messageModels,this,receiverId);
        binding.chatRecyclerView.setAdapter(chatAdapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        final String senderRoom=senderId+receiverId;
        final String receiverRoom=receiverId+senderId;

        database.getReference().child("Chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModels.clear();
                for (DataSnapshot dataSnapShot: snapshot.getChildren()) {
                    MessageModel model=dataSnapShot.getValue(MessageModel.class);
                    model.setMessageId(dataSnapShot.getKey());
                    messageModels.add(model);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener(view->{
            if(binding.chatSender.getText().toString().isEmpty()){
                binding.chatSender.setError("Enter your message");
                return;
            }
            String message=binding.chatSender.getText().toString();
            final MessageModel messageModel=new MessageModel(senderId,message);
             messageModel.setTimeStamp(new Date().getTime());
             binding.chatSender.setText("");

             database.getReference().child("Chats").child(senderRoom).push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                 @Override
                 public void onSuccess(Void unused) {

                     database.getReference().child("Chats").child(receiverRoom).push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {

                         }
                     });
                 }
             });
        });

    }
}