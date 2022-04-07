package com.example.myjourneyjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    private FirebaseAuth auth;

    private EditText editEmail;

    public static Intent getIntent(Context context) {
        return new Intent(context, ForgotPassword.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        auth = FirebaseAuth.getInstance();

        editEmail = (EditText) findViewById(R.id.edit_email);
        TextView txtJustRemember = findViewById(R.id.text_just_remember);
        TextView txtSignUp = findViewById(R.id.text_sign_up);
        Button btnSendEmail = (Button) findViewById(R.id.btn_verify_email);

        txtJustRemember.setOnClickListener(this::startSignInActivity);
        txtSignUp.setOnClickListener(this::startSignUpActivity);
        btnSendEmail.setOnClickListener(this::startEmailVerification);
    }

    // start verification process
    private void startEmailVerification(View view) {
        String email = editEmail.getText().toString();
        if (validateData(email)) {
            sendResetEmail(view, email);
        }
    }

    // validate data
    private boolean validateData(String email) {
        if (email.isEmpty()) {
            editEmail.setError("Please enter your email.");
            editEmail.requestFocus();
            return false;
        }
        return true;
    }

    // send email
    private void sendResetEmail(View view, String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startSignInActivity(view);
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(ForgotPassword.this,
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Some error occurred.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // go to sign in activity
    private void startSignInActivity(View view) {
        onBackPressed();
    }

    // go to sign up activity
    private void startSignUpActivity(View view) {
        startActivity(SignUp.getIntent(this));
        finish();
    }
}