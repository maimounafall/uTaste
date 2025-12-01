package com.example.utaste;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.utaste.backend.Role;
import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.backend.UTasteBackend;
import com.example.utaste.backend.User;
import com.example.utaste.backend.UserEntity;
import com.example.utaste.backend.UserManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AdminProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPassword;
    private TextView tvCreatedAt, tvUpdatedAt;

    private UTasteBackend backend;
    private UserManager userManager;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        backend = UTasteApplication.getInstance().getBackend();
        userManager = backend.getUserManager();
        user = backend.getCurrentUser();

        if (user == null || user.getRole() != Role.ADMINISTRATOR) {
            Toast.makeText(this, "Access denied.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        etFirstName = findViewById(R.id.editText2);
        etLastName = findViewById(R.id.editText3);
        etEmail = findViewById(R.id.editEmailAdmin);
        etPassword = findViewById(R.id.editText5);

        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvUpdatedAt = findViewById(R.id.tvUpdatedAt);

        Button btnSave = findViewById(R.id.button);
        Button btnLogout = findViewById(R.id.btnLogout);
        ImageButton btnBack = findViewById(R.id.btnBack);

        LinearLayout cardAddWaiter = findViewById(R.id.cardAddWaiter);
        LinearLayout cardManageUsers = findViewById(R.id.cardManageUsers);
        LinearLayout cardResetDb = findViewById(R.id.cardResetDb);

        Button btnAddWaiter = findViewById(R.id.btn_add_waiter);
        Button btnManageUsers = findViewById(R.id.btn_manage_users);
        Button btnResetData = findViewById(R.id.btn_reset_data);

        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setText(user.getPassword()); // masked but visible

        // Load timestamps
        updateTimestamps();

        etFirstName.setText(user.getFirstName() == null ? "Admin" : user.getFirstName());
        etLastName.setText(user.getLastName() == null ? "" : user.getLastName());
        etEmail.setText(user.getEmail());

        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        btnLogout.setOnClickListener(v -> showLogoutDialog());

        cardAddWaiter.setOnClickListener(v -> startActivity(new Intent(this, AddWaiterActivity.class)));
        btnAddWaiter.setOnClickListener(v -> startActivity(new Intent(this, AddWaiterActivity.class)));

        cardManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));
        btnManageUsers.setOnClickListener(v -> startActivity(new Intent(this, ManageUsersActivity.class)));

        cardResetDb.setOnClickListener(v -> showResetDialog());
        btnResetData.setOnClickListener(v -> showResetDialog());

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void updateTimestamps() {
        UserEntity entity = backend.getDatabase().userDao().findByEmail(user.getEmail());
        if (entity == null) return;

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        tvCreatedAt.setText(entity.createdAt == 0 ? "" : fmt.format(entity.createdAt));
        tvUpdatedAt.setText(entity.updatedAt == 0 ? "" : fmt.format(entity.updatedAt));
    }

    private void saveProfile() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String newPwd = etPassword.getText().toString().trim();

        if (first.isEmpty()) {
            etFirstName.setError("Required");
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Required");
            return;
        }

        try {
            backend.updateUserProfile(user.getEmail(), first, last, email);

            if (!newPwd.isEmpty()) {
                showPasswordDialog(newPwd);
            } else {
                Toast.makeText(this, "Profile updated.", Toast.LENGTH_SHORT).show();
            }

            updateTimestamps();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // --------------------------- LOGOUT ---------------------------

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.tvConfirmTitle);
        TextView msg = dialog.findViewById(R.id.tvConfirmMessage);
        Button cancel = dialog.findViewById(R.id.btnCancel);
        Button confirm = dialog.findViewById(R.id.btnConfirm);

        title.setText("Log out");
        msg.setText("Are you sure you want to log out?");
        confirm.setText("Log out");

        cancel.setOnClickListener(v -> dialog.dismiss());

        confirm.setOnClickListener(v -> {
            dialog.dismiss();
            backend.logout(user);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        dialog.show();
    }

    // --------------------------- CHANGE PASSWORD  ---------------------------

    private void showPasswordDialog(String newPwd) {
        Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_confirm_delete);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = d.findViewById(R.id.tvConfirmTitle);
        TextView placeholder = d.findViewById(R.id.tvConfirmMessage);
        Button cancel = d.findViewById(R.id.btnCancel);
        Button confirm = d.findViewById(R.id.btnConfirm);

        title.setText("Confirm Password Change");

        EditText pwdInput = new EditText(this);
        pwdInput.setHint("Enter current password");
        pwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout box = (LinearLayout) placeholder.getParent();
        box.removeView(placeholder);
        box.addView(pwdInput, 1);

        confirm.setText("Confirm");

        cancel.setOnClickListener(v -> d.dismiss());

        confirm.setOnClickListener(v -> {
            String current = pwdInput.getText().toString().trim();
            if (current.isEmpty()) {
                Toast.makeText(this, "Current password required.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                backend.changeOwnPassword(current, newPwd);
                Toast.makeText(this, "Password updated!", Toast.LENGTH_LONG).show();
                etPassword.setText("");

                d.dismiss();
                updateTimestamps();

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        d.show();
    }

    // --------------------------- RESET DB (WITH PASSWORD) ---------------------------

    private void showResetDialog() {
        Dialog pop = new Dialog(this);
        pop.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pop.setContentView(R.layout.dialog_confirm_delete);
        pop.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = pop.findViewById(R.id.tvConfirmTitle);
        TextView msg = pop.findViewById(R.id.tvConfirmMessage);
        Button cancel = pop.findViewById(R.id.btnCancel);
        Button confirm = pop.findViewById(R.id.btnConfirm);

        title.setText("Reset All Data");
        msg.setText("This will permanently erase all users, recipes, and data.");
        confirm.setText("Continue");

        cancel.setOnClickListener(v -> pop.dismiss());

        confirm.setOnClickListener(v -> {
            pop.dismiss();
            showAdminPasswordCheck();
        });

        pop.show();
    }

    private void showAdminPasswordCheck() {
        Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_confirm_delete);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = d.findViewById(R.id.tvConfirmTitle);
        TextView placeholder = d.findViewById(R.id.tvConfirmMessage);
        Button cancel = d.findViewById(R.id.btnCancel);
        Button confirm = d.findViewById(R.id.btnConfirm);

        title.setText("Confirm Admin Identity");

        EditText pwdInput = new EditText(this);
        pwdInput.setHint("Enter password");
        pwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout box = (LinearLayout) placeholder.getParent();
        box.removeView(placeholder);
        box.addView(pwdInput, 1);

        confirm.setText("Reset Now");

        cancel.setOnClickListener(v -> d.dismiss());

        confirm.setOnClickListener(v -> {
            String entered = pwdInput.getText().toString().trim();
            if (!entered.equals(user.getPassword())) {
                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                return;
            }

            d.dismiss();
            resetDatabase();
        });

        d.show();
    }

    private void resetDatabase() {
        Toast.makeText(this, "Resetting, please wait...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                backend.resetDatabase();

                runOnUiThread(() -> {
                    Toast.makeText(this, "Database reset.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finishAffinity();
                });
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
