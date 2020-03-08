package com.simontan.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    TextView mMessageTexView;

    Button mSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mMessageTexView = (TextView) findViewById(R.id.message);

        mSignOutButton = (Button) findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void updateUI(FirebaseUser user) {
        mMessageTexView.setText("Hello, " + user.getEmail() + "!");
    }

    private void signOut() {
        mAuth.signOut();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_out_button:
                signOut();
                Intent intent = new Intent(this, EmailPasswordActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
