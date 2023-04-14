package com.aditya.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aditya.whatsapp.databinding.ActivitySignUpBinding;
import com.aditya.whatsapp.models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    ActivitySignUpBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("We are creating your account !");

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference().child("Users");


        binding.btnSignUp.setOnClickListener(view->{
            if(binding.etUsername.getText().toString().isEmpty()){
                binding.etUsername.setError("Enter a user name");
                return;
            }
            if(binding.etEmail.getText().toString().isEmpty()){
                binding.etEmail.setError("Enter a Email Id");
                return;
            }
            if(binding.etPassword.getText().toString().isEmpty()){
                binding.etPassword.setError("Enter a Password");
                return;
            }
            progressDialog.show();
            auth.createUserWithEmailAndPassword(binding.etEmail.getText().toString(),binding.etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                    if(task.isSuccessful()){
                        Users user=new Users(binding.etUsername.getText().toString(),binding.etEmail.getText().toString(),binding.etPassword.getText().toString());
                        String id=task.getResult().getUser().getUid();

                        reference.child(id).setValue(user);
                        Toast.makeText(SignUp.this, "You sign up successfully !", Toast.LENGTH_SHORT).show();

                        Intent intent=new Intent(SignUp.this,SignIn.class);
                        startActivity(intent);
                        finish();

                    }
                    else{

                        Toast.makeText(SignUp.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        });

        binding.tvSignIn.setOnClickListener(view->{
            Intent intent=new Intent(SignUp.this,SignIn.class);
            startActivity(intent);
            finish();
        });



    }
}