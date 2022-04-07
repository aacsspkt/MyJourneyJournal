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

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editEmail, editPassword, editConfirmPassword;

    public static Intent getIntent(Context context) {
        return new Intent(context, SignUp.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirmPassword = findViewById(R.id.edit_confirm_password);
        TextView txtSignIn = findViewById(R.id.text_sign_in);
        Button btnSignUp = findViewById(R.id.btn_sign_up);

        txtSignIn.setOnClickListener(this::startSignInActivity);
        btnSignUp.setOnClickListener(this::processSignUp);
    }

    // go to back sign in activity
    private void startSignInActivity(View view) {
        // instead of starting activity we remove this activity from activity stack
        onBackPressed();
    }

    // start sign up process
    private void processSignUp(View view) {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if (validateData(email, password, confirmPassword)) {
            signUp(view, email, password);
        }
    }

    // sign up user
    private void signUp(View view, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startSignInActivity(view);
                        Toast.makeText(getApplicationContext(), "Signed up successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Toast.makeText(getApplicationContext(), e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Sign up failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // validate input data
    private boolean validateData(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            editEmail.setError("Please enter email.");
            editEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            editPassword.setError("Please enter password.");
            editPassword.requestFocus();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            editConfirmPassword.setError("Please enter confirm password");
            editConfirmPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            editPassword.setError("Password must have least 6 characters.");
            editPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            editPassword.setError("Password does not match confirm password.");
            editPassword.setText("");
            editConfirmPassword.setText("");
            editPassword.requestFocus();
            return false;
        }
        return true;
    }
}