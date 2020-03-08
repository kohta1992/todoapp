package com.simontan.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class EmailPasswordActivity extends BaseActivity implements
        View.OnClickListener {

    private final String TAG = "EmailPassword";

    private FirebaseAuth mAuth;

    RelativeLayout mRootLayout;
    EditText mEmailField, mPasswordField;
    Button mSignInButton, mSignUpTextView;
    TextView mStatusTextView;

    String mStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);

        mRootLayout = (RelativeLayout) findViewById(R.id.root);
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignUpTextView = (Button) findViewById(R.id.sign_up_button);
        mStatusTextView = (TextView) findViewById(R.id.status);

        mRootLayout.setOnClickListener(this);
        mSignInButton.setOnClickListener(this);
        mSignUpTextView.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void updateUI() {
        mStatusTextView.setText(mStatus);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);


        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmailAndPassword:success");
                    Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        mStatus = "そのメールアドレスは既に使用されています。";
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                            task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        mStatus = "メールアドレス/パスワードに誤りがあります。";
                    } else {
                        mStatus = "アカウント登録に失敗しました。";
                    }
                }

                updateUI();

                hideProgressDialog();
            }
        });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmailAndPassword:success");
                    Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());

                    if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                            task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        mStatus = "メールアドレス/パスワードに誤りがあります。";
                    } else {
                        mStatus = "アカウント認証に失敗しました。";
                    }
                }

                updateUI();

                hideProgressDialog();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.error_message_required));
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.error_message_required));
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.root:
                hideKeyboard(v);
                break;
            case R.id.sign_in_button:
                hideKeyboard(v);
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.sign_up_button:
                hideKeyboard(v);
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            default:
                break;
        }
    }
}
