package com.example.utaste;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.backend.UTasteBackend;
import com.example.utaste.backend.User;
import com.example.utaste.backend.Role;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load global backend
        UTasteBackend backend = UTasteApplication.getInstance().getBackend();
        User currentUser = backend.getCurrentUser();

        // Auto-redirect if a user session exists
        if (currentUser != null) {
            switch (currentUser.getRole()) {
                case ADMINISTRATOR:
                    startActivity(new Intent(this, AdminProfileActivity.class));
                    finish();
                    return;
                case CHEF:
                    startActivity(new Intent(this, ChefActivity.class));
                    finish();
                    return;
                case WAITER:
                    startActivity(new Intent(this, WaiterActivity.class));
                    finish();
                    return;
            }
        }

        // Otherwise show login button
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
