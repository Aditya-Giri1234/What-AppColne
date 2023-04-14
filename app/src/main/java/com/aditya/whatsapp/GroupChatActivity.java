package com.aditya.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.aditya.whatsapp.adapter.ChatAdapter;
import com.aditya.whatsapp.adapter.GroupAdapter;
import com.aditya.whatsapp.databinding.ActivityGroupChatBinding;
import com.aditya.whatsapp.models.MessageModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    FirebaseDatabase database;
    FirebaseAuth auth;

    ActivityGroupChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        final  String senderId=auth.getUid();

        binding.userName.setText("Friend's Group");


        binding.backArrow.setOnClickListener(view-> {
            finish();
        });

        final ArrayList<MessageModel> messageModels=new ArrayList<>();
        final GroupAdapter groupAdapter=new GroupAdapter(messageModels,this);
        binding.chatRecyclerView.setAdapter(groupAdapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        database.getReference().child("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messageModels.clear();
                    for (DataSnapshot dataSnapShot : snapshot.getChildren()) {
                        MessageModel model = dataSnapShot.getValue(MessageModel.class);
                        model.setMessageId(dataSnapShot.getKey());
                        messageModels.add(model);
                    }
                    groupAdapter.notifyDataSetChanged();
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

            database.getReference().child("Groups").push().setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                }
            });
        });
    }
}