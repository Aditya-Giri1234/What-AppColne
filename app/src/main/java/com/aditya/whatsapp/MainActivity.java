package com.aditya.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aditya.whatsapp.adapter.FragmentAdapter;
import com.aditya.whatsapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.pageViewer.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.pageViewer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
            {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MainActivity.this,SignIn.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.groupChat:
                Intent intent=new Intent(this,GroupChatActivity.class);
                startActivity(intent);
                break;
            case R.id.settings:
                Intent intent1=new Intent(this,SettingActivity.class);
                startActivity(intent1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}