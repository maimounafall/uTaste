package com.example.utaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utaste.backend.Role;
import com.example.utaste.backend.User;
import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.backend.UTasteBackend;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private UTasteBackend backend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Load shared backend
        backend = UTasteApplication.getInstance().getBackend();
        Log.i("BackendCheck", "Backend instance: " + backend);

        // Bind UI
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                User user = backend.getUserManager().authenticate(email, password);

                if (user != null) {
                    backend.setCurrentUser(user);
                    Log.i("LoginActivity", "Logged in as: " + user.getEmail() + " (Role: " + user.getRole() + ")");
                    openRolePage(user.getRole(), user.getEmail());
                } else {
                    Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginError", "Login exception", e);
            }
        });
    }

    private void openRolePage(Role role, String email) {
        Intent intent;

        switch (role) {
            case ADMINISTRATOR:
                intent = new Intent(this, AdminProfileActivity.class);
                break;
            case CHEF:
                intent = new Intent(this, ChefActivity.class);
                break;
            case WAITER:
                intent = new Intent(this, WaiterActivity.class);
                break;
            default:
                Toast.makeText(this, "Unknown role, cannot open page.", Toast.LENGTH_SHORT).show();
                return;
        }

        intent.putExtra("user_email", email);
        startActivity(intent);
        finish();
    }
}
