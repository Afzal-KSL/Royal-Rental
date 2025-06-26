package com.example.rental;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameET, ageET, emailET, passwordET;
    private RadioGroup genderGroup;
    private Button signUpBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI elements
        nameET = findViewById(R.id.login_name);
        ageET = findViewById(R.id.login_age);
        genderGroup = findViewById(R.id.radioGroupGender);
        emailET = findViewById(R.id.login_email); // Change from mobile to email
        passwordET = findViewById(R.id.login_password);
        signUpBtn = findViewById(R.id.sign_up_btn);

        // Sign Up Button Click Listener
        signUpBtn.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String name = nameET.getText().toString().trim();
        String age = ageET.getText().toString().trim();
        String email = emailET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        int selectedId = genderGroup.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedId);
        String gender = (selectedGender != null) ? selectedGender.getText().toString() : "";

        if (name.isEmpty() || age.isEmpty() || email.isEmpty() || password.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication using email
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                String userId = user.getUid();

                // Store user details in Firebase Database
                User newUser = new User(name, age, gender, email);
                mDatabase.child(userId).setValue(newUser).addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Database Error: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(SignUpActivity.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void myLoginIntent(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
