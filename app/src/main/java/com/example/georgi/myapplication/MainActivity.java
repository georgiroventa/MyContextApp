package com.example.georgi.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity implements OnClickListener {

    public static String userEmail;
    FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.edit_text_email);
        editTextPassword = (EditText) findViewById(R.id.edit_text_password);

        findViewById(R.id.button_register).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }

    String password;
    String email;
    private void userLogin() {
        email = editTextEmail.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (mRef != null)
            try {
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Parent parent = null;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot result : dataSnapshot.getChildren()) {
                                parent = result.getValue(Parent.class);

                                if ((parent != null) && (parent.getEmail().equals(email))) {
                                    userEmail = parent.getEmail();
                                    String aux = parent.getType();
                                     if (aux.equals("user") && (password.equals(parent.password))) {
                                            userEmail = parent.getEmail();
                                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_LONG).show();
                                            Intent start = new Intent(MainActivity.this, ParentActivity.class);
                                            start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(start);
                                     } else {
                                            Toast.makeText(MainActivity.this, "The password is incorrect", Toast.LENGTH_LONG).show();
                                        }

                                }
                                else{
                                    Toast.makeText(MainActivity.this, "The user is not registered", Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "User don t exit!", Toast.LENGTH_LONG).show();
            }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_register:
                finish();
                startActivity(new Intent(this, RegisterActivity.class));
                break;

            case R.id.button_login:
                userLogin();
                break;
        }
    }

}