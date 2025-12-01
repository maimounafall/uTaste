package com.example.utaste;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.backend.UTasteBackend;
import com.example.utaste.backend.UserManager;
import com.example.utaste.backend.Waiter;

public class AddWaiterActivity extends AppCompatActivity {

    private UTasteBackend backend;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_waiter);

        backend = UTasteApplication.getInstance().getBackend();
        userManager = backend.getUserManager();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        EditText etName = findViewById(R.id.etWaiterName);
        EditText etEmail = findViewById(R.id.etWaiterEmail);
        EditText etPassword = findViewById(R.id.etWaiterPassword);

        Button btnSave = findViewById(R.id.btnSaveWaiter);
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                String firstName = "";
                String lastName = "";
                String[] parts = name.split(" ", 2);
                if (parts.length >= 1) firstName = parts[0];
                if (parts.length == 2) lastName = parts[1];

                if (userManager.getUserByEmail(email) != null) {
                    Toast.makeText(this, "User already exists", Toast.LENGTH_LONG).show();
                    return;
                }

                Waiter newWaiter = new Waiter(firstName, lastName, email, password);
                userManager.addUser(newWaiter);

                Toast.makeText(this,
                        "Waiter added succesfully : " + newWaiter.getFirstName(),
                        Toast.LENGTH_LONG).show();

                etName.setText("");
                etEmail.setText("");
                etPassword.setText("");

                finish();

            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (SecurityException e) {
                Toast.makeText(this, "Permission refused: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
