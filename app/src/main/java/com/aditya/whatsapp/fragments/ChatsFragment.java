package com.aditya.whatsapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aditya.whatsapp.adapter.UserAdapter;
import com.aditya.whatsapp.databinding.FragmentChatsBinding;
import com.aditya.whatsapp.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ChatsFragment extends Fragment {

    FragmentChatsBinding binding;
    ArrayList<Users> list;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentChatsBinding.inflate(inflater,container,false);
        database=FirebaseDatabase.getInstance();

        list=new ArrayList<>();
        UserAdapter adapter=new UserAdapter(list,getContext());
        binding.chatRecyclerVIew.setAdapter(adapter);
        binding.chatRecyclerVIew.setLayoutManager(new LinearLayoutManager(getContext()));

        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()) {
                    Users users=ds.getValue(Users.class);
                    users.setUserId(ds.getKey());
                    if(!users.getUserId().equals(FirebaseAuth.getInstance().getUid()))
                    list.add(users);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return binding.getRoot();
    }
}