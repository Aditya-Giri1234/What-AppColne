package com.aditya.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.aditya.whatsapp.databinding.ActivitySignInBinding;
import com.aditya.whatsapp.models.Users;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


public class SignIn extends AppCompatActivity {
    ActivitySignInBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseStorage storage;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();


        auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login into your account");


        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("152585221210-s11gjvm4pjfd0gi3jh5nf4sai8lbk44k.apps.googleusercontent.com").requestEmail().build();
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso);


        binding.btnSignIn.setOnClickListener(view -> {

            if(binding.etEmailSignIn.getText().toString().isEmpty()){
                binding.etEmailSignIn.setError("Enter your Email");
                return;
            }
            if(binding.etPasswordSignIn.getText().toString().isEmpty()){
                binding.etPasswordSignIn.setError("Enter your password");
                return;
            }
            progressDialog.show();
            auth.signInWithEmailAndPassword(binding.etEmailSignIn.getText().toString(), binding.etPasswordSignIn.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignIn.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
        });

        binding.tvSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(SignIn.this, SignUp.class);
            startActivity(intent);
            finish();
        });

        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(SignIn.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.btnGoogleSignIn.setOnClickListener(view -> {
            signIn();
        });

    }
    int RC_SIGN_IN=65;

    private void signIn() {
        Intent intent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(String account) {
        AuthCredential credential= GoogleAuthProvider.getCredential(account,null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser user=auth.getCurrentUser();
                    Users users=new Users();
                    users.setUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    users.setMail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    users.setProfilePic(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());

                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).setValue(users);


                    Intent intent=new Intent(SignIn.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(SignIn.this, "Failed !", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}