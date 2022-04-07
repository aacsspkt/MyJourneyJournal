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

public class SignIn extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth auth;

    public static Intent getIntent(Context context) {
        return new Intent(context, SignIn.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.edit_email);
        editTextPassword = findViewById(R.id.edit_password);
        TextView txtForgotPassword = findViewById(R.id.text_forgot_password);
        TextView txtSignUp = findViewById(R.id.text_sign_up);
        Button buttonSignIn = findViewById(R.id.login);

        txtForgotPassword.setOnClickListener(this::startForgotPasswordActivity);
        txtSignUp.setOnClickListener(this::startSignUpActivity);
        buttonSignIn.setOnClickListener(this::signInBypass);
    }

    // go to forgot password activity
    private void startForgotPasswordActivity(View view) {
        startActivity(ForgotPassword.getIntent(this));
    }

    // go to sign up activity
    private void startSignUpActivity(View view) {
        startActivity(SignUp.getIntent(this));
    }

    // start sign in process
    private void processSignIn(View view) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (validateData(email, password)) {
            signIn(view, email, password);
        }

    }

    // validate data
    private boolean validateData(String email, String password) {
        if (email.isEmpty()) {
            editTextEmail.setError("Please enter email.");
            editTextEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Please enter password.");
            editTextPassword.requestFocus();
            return false;
        }
        return true;
    }

    // just in development mode
    private void signInBypass(View view) {
        startHomeActivity(view);
    }

    // sign in using firebase
    private void signIn(View view, String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startHomeActivity(view);
                        Toast.makeText(SignIn.this, "Signed in successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(SignIn.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignIn.this, "Sign in failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // start new activity
    private void startHomeActivity(View view) {
        startActivity(Home.getIntent(this));
    }
}